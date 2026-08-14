package com.sniper.game.wordgame.service;

import com.sniper.game.wordgame.dto.BackpackItemDto;
import com.sniper.game.wordgame.dto.CollectItemDto;
import com.sniper.game.wordgame.dto.MyStorageItemDto;
import com.sniper.game.wordgame.dto.MyStoragePageDto;
import com.sniper.game.wordgame.dto.MyStorageRequest;
import com.sniper.game.wordgame.dto.StorageGroupDto;
import com.sniper.game.wordgame.entity.UserBackpack;
import com.sniper.game.wordgame.exception.BusinessException;
import com.sniper.game.wordgame.mapper.GameCollectMapper;
import com.sniper.game.wordgame.mapper.UserBackpackMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户收集品背包：拥有/当前展示状态落 user_backpack 表（取代原前端本地 CollectStore 存储）。
 * 目录配置仍由 CollectService/game_collect 只读下发，二者结合展示。
 */
@Service
@RequiredArgsConstructor
public class BackpackService {

    /** 我的仓库默认每页条数（前端不传 pageSize 时用） */
    private static final int DEFAULT_PAGE_SIZE = 10;

    /** 每页条数上限，防止前端传超大值把全量捞出来 */
    private static final int MAX_PAGE_SIZE = 100;

    private final UserBackpackMapper userBackpackMapper;
    private final GameCollectMapper gameCollectMapper;
    private final CollectService collectService;

    public List<BackpackItemDto> listBackpack(Long userId) {
        if (userId == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        return userBackpackMapper.findByUserId(userId);
    }

    /**
     * 我的仓库：目录 + 持有状态在服务端拼好再下发，前端拿到即可直接渲染。
     *
     * 刻意不连表：先单表查 user_backpack 拿到该用户持有的 id（量级=用户持有数，不是目录几万条），
     * 再用这批 id 单表 in 查 game_collect，最后在内存里按 collectId 关联、筛分组、排序、切页。
     * game_collect 后续进 Redis 时，只需把 findByIds 换成缓存批量取，下面的拼装逻辑不用动。
     */
    public MyStoragePageDto myStorage(Long userId, MyStorageRequest request) {
        if (userId == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        int page = (request == null || request.getPage() == null || request.getPage() < 1)
                ? 1 : request.getPage();
        int pageSize = (request == null || request.getPageSize() == null || request.getPageSize() < 1)
                ? DEFAULT_PAGE_SIZE : Math.min(request.getPageSize(), MAX_PAGE_SIZE);
        String groupCode = (request == null || request.getGroupCode() == null
                || request.getGroupCode().trim().isEmpty()) ? null : request.getGroupCode().trim();

        // 第一步：单表查持有记录，只留数量>0 的（与前端「已拥有」语义一致）
        List<BackpackItemDto> owned = userBackpackMapper.findByUserId(userId).stream()
                .filter(item -> item.getCount() != null && item.getCount() > 0)
                .collect(Collectors.toList());
        if (owned.isEmpty()) {
            return emptyPage(page, pageSize);
        }

        // 第二步：单表 in 批量取目录，转 Map 供内存关联
        List<Long> collectIds = owned.stream()
                .map(BackpackItemDto::getCollectId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (collectIds.isEmpty()) {
            return emptyPage(page, pageSize);
        }
        Map<Long, CollectItemDto> catalogMap = gameCollectMapper.findByIds(collectIds).stream()
                .collect(Collectors.toMap(CollectItemDto::getId, Function.identity(), (a, b) -> a));

        // 第三步：内存拼装。目录里查不到的持有记录（配置被删）直接丢弃，不下发脏数据
        List<MyStorageItemDto> all = new ArrayList<>();
        for (BackpackItemDto item : owned) {
            CollectItemDto catalog = catalogMap.get(item.getCollectId());
            if (catalog == null) {
                continue;
            }
            all.add(toStorageItem(catalog, item));
        }
        if (all.isEmpty()) {
            return emptyPage(page, pageSize);
        }
        // game_collect 无 sort_order 列，按 分组编码 + id 排，保证分页顺序稳定
        all.sort(Comparator.comparing(MyStorageItemDto::getGroupCode,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(MyStorageItemDto::getCollectId));

        // 当前展示项兜底：整个仓库都没有 is_current 时取排序后第一件，与前端原 ownedIds[0] 兜底一致
        if (all.stream().noneMatch(item -> Boolean.TRUE.equals(item.getIsCurrent()))) {
            all.get(0).setIsCurrent(true);
        }

        // tab 列表取自全量持有（不随 groupCode 筛选变化），保持排序后的出现顺序
        List<StorageGroupDto> groups = buildGroups(all);

        List<MyStorageItemDto> filtered = groupCode == null
                ? all
                : all.stream()
                        .filter(item -> groupCode.equals(item.getGroupCode()))
                        .collect(Collectors.toList());

        MyStoragePageDto result = new MyStoragePageDto();
        result.setGroups(groups);
        result.setTotal(filtered.size());
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setItems(slice(filtered, page, pageSize));
        return result;
    }

    private MyStorageItemDto toStorageItem(CollectItemDto catalog, BackpackItemDto owned) {
        MyStorageItemDto dto = new MyStorageItemDto();
        dto.setCollectId(catalog.getId());
        dto.setCollectCode(catalog.getCollectCode());
        dto.setGroupCode(catalog.getGroupCode());
        dto.setGroupName(collectService.resolveGroupName(catalog.getGroupCode()));
        dto.setName(catalog.getName());
        dto.setGrayUrl(catalog.getGrayUrl());
        dto.setColorUrl(catalog.getColorUrl());
        dto.setCount(owned.getCount());
        dto.setIsCurrent(Boolean.TRUE.equals(owned.getIsCurrent()));
        return dto;
    }

    /** 按已排序的条目抽 distinct 分组，LinkedHashMap 保序，前端 tab 顺序与条目顺序一致 */
    private List<StorageGroupDto> buildGroups(List<MyStorageItemDto> items) {
        Map<String, StorageGroupDto> groups = new LinkedHashMap<>();
        for (MyStorageItemDto item : items) {
            if (item.getGroupCode() == null) {
                continue;
            }
            groups.computeIfAbsent(item.getGroupCode(),
                    code -> new StorageGroupDto(code, item.getGroupName()));
        }
        return new ArrayList<>(groups.values());
    }

    /** 内存切页：页码超出范围返回空列表，不报错（前端切 tab 时可能残留大页码） */
    private List<MyStorageItemDto> slice(List<MyStorageItemDto> items, int page, int pageSize) {
        int from = (page - 1) * pageSize;
        if (from >= items.size()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(items.subList(from, Math.min(from + pageSize, items.size())));
    }

    private MyStoragePageDto emptyPage(int page, int pageSize) {
        MyStoragePageDto result = new MyStoragePageDto();
        result.setItems(Collections.emptyList());
        result.setGroups(Collections.emptyList());
        result.setTotal(0);
        result.setPage(page);
        result.setPageSize(pageSize);
        return result;
    }

    /** 拥有一个收集品：累加数量（支持重复拥有），amount 未传或非正数按1处理 */
    public void own(Long userId, Long collectId, Integer amount) {
        if (userId == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        int addAmount = (amount == null || amount <= 0) ? 1 : amount;

        UserBackpack existing = userBackpackMapper.findByUserIdAndCollectId(userId, collectId);
        if (existing == null) {
            UserBackpack backpack = new UserBackpack();
            backpack.setUserId(userId);
            backpack.setCollectId(collectId);
            backpack.setCount(addAmount);
            backpack.setIsCurrent(false);
            userBackpackMapper.insert(backpack);
            return;
        }
        userBackpackMapper.incrementCount(userId, collectId, addAmount);
    }

    /** 指定当前展示的收集品：需已拥有才生效，否则忽略（与前端 CollectStore.setCurrent 语义一致） */
    @Transactional
    public void setCurrent(Long userId, Long collectId) {
        if (userId == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        UserBackpack existing = userBackpackMapper.findByUserIdAndCollectId(userId, collectId);
        if (existing == null) {
            return;
        }
        userBackpackMapper.clearCurrent(userId);
        userBackpackMapper.setCurrent(userId, collectId);
    }
}
