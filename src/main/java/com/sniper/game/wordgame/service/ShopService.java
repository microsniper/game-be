package com.sniper.game.wordgame.service;

import com.sniper.game.wordgame.constant.enums.CollectGroupEnum;
import com.sniper.game.wordgame.dto.ShopItemDto;
import com.sniper.game.wordgame.dto.ShopListRequest;
import com.sniper.game.wordgame.dto.ShopPageDto;
import com.sniper.game.wordgame.dto.StorageGroupDto;
import com.sniper.game.wordgame.mapper.GameShopMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 商城：只读目录下发（配置在 game_shop 表，资源关联 game_resource/game_collect）。
 * 购买发放不落后端，由前端本地账处理（与道具/金币存储边界一致）。
 * 道具/收集数据源结构不同，分页在 SQL 层各自独立做，不再 UNION 全量下发。
 */
@Service
@RequiredArgsConstructor
public class ShopService {

    /** 商城默认每页条数（前端不传 pageSize 时用） */
    private static final int DEFAULT_PAGE_SIZE = 10;

    /** 每页条数上限，防止前端传超大值把全量捞出来 */
    private static final int MAX_PAGE_SIZE = 100;

    private final GameShopMapper gameShopMapper;

    /**
     * 商城分页查询：category=1 查道具，category=2 查收集（可选按 groupCode 筛选）。
     * category 不传或非法值按 1 处理。
     */
    public ShopPageDto listShop(ShopListRequest request) {
        int page = (request == null || request.getPage() == null || request.getPage() < 1)
                ? 1 : request.getPage();
        int pageSize = (request == null || request.getPageSize() == null || request.getPageSize() < 1)
                ? DEFAULT_PAGE_SIZE : Math.min(request.getPageSize(), MAX_PAGE_SIZE);
        int category = (request == null || request.getCategory() == null
                || (request.getCategory() != 1 && request.getCategory() != 2))
                ? 1 : request.getCategory();
        int offset = (page - 1) * pageSize;

        if (category == 1) {
            return listTools(offset, pageSize, page);
        }
        String groupCode = (request == null || request.getGroupCode() == null
                || request.getGroupCode().trim().isEmpty()) ? null : request.getGroupCode().trim();
        return listCollect(groupCode, offset, pageSize, page);
    }

    private ShopPageDto listTools(int offset, int pageSize, int page) {
        List<ShopItemDto> items = gameShopMapper.findTools(offset, pageSize);
        int total = gameShopMapper.countTools();
        ShopPageDto result = new ShopPageDto();
        result.setItems(items);
        result.setGroups(Collections.emptyList());
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        return result;
    }

    private ShopPageDto listCollect(String groupCode, int offset, int pageSize, int page) {
        List<ShopItemDto> items = gameShopMapper.findCollect(groupCode, offset, pageSize);
        items.forEach(item -> item.setGroupName(resolveGroupName(item.getGroupCode())));
        int total = gameShopMapper.countCollect(groupCode);

        List<StorageGroupDto> groups = gameShopMapper.findCollectGroups();
        groups.forEach(group -> group.setGroupName(resolveGroupName(group.getGroupCode())));

        ShopPageDto result = new ShopPageDto();
        result.setItems(items);
        result.setGroups(groups);
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        return result;
    }

    /** 分组中文名取自 CollectGroupEnum；未收录的分组编码回落编码原文，不报错（与 CollectService 同一套逻辑） */
    private String resolveGroupName(String groupCode) {
        if (StringUtils.isBlank(groupCode)) {
            return groupCode;
        }
        try {
            CollectGroupEnum group = CollectGroupEnum.fromCode(groupCode);
            return group != null ? group.getName() : groupCode;
        } catch (IllegalArgumentException e) {
            return groupCode;
        }
    }
}
