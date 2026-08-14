package com.sniper.game.wordgame.service;

import com.sniper.game.wordgame.constant.enums.CollectGroupEnum;
import com.sniper.game.wordgame.dto.CollectItemDto;
import com.sniper.game.wordgame.mapper.GameCollectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 收集品：只读目录下发（配置在 game_collect 表）。
 * 拥有/当前展示状态不落后端，由前端本地 CollectStore 处理（与道具/金币存储边界一致）。
 */
@Service
@RequiredArgsConstructor
public class CollectService {

    private final GameCollectMapper gameCollectMapper;

    public List<CollectItemDto> listCatalog() {
        List<CollectItemDto> items = gameCollectMapper.findAll();
        items.forEach(item -> item.setGroupName(resolveGroupName(item.getGroupCode())));
        return items;
    }

    /** 随机抽取指定数量的 fruit 分组收集品（首页圆盘人群用） */
    public List<CollectItemDto> listRandomFruits(int limit) {
        List<CollectItemDto> items = gameCollectMapper.findRandomFruits(limit);
        items.forEach(item -> item.setGroupName(resolveGroupName(item.getGroupCode())));
        return items;
    }

    /**
     * 按 id 批量查（猫咪图标查当前展示项、抽奖排除已拥有等场景用）：单表 in 查询，不整表下发。
     * ids 为空直接返回空列表，不打后端。
     */
    public List<CollectItemDto> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<CollectItemDto> items = gameCollectMapper.findByIds(ids);
        items.forEach(item -> item.setGroupName(resolveGroupName(item.getGroupCode())));
        return items;
    }

    /** 新用户默认赠送的那一条；未配置返回 null（调用方跳过补领） */
    public CollectItemDto getStarterGift() {
        CollectItemDto item = gameCollectMapper.findStarterGift();
        if (item != null) {
            item.setGroupName(resolveGroupName(item.getGroupCode()));
        }
        return item;
    }

    /**
     * 按 collectCode 批量查（奖励结果反查名称/id 用）：单表 in 查询，不整表下发。
     * codes 为空直接返回空列表，不打后端。
     */
    public List<CollectItemDto> listByCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyList();
        }
        List<CollectItemDto> items = gameCollectMapper.findByCodes(codes);
        items.forEach(item -> item.setGroupName(resolveGroupName(item.getGroupCode())));
        return items;
    }

    /** 分组中文名取自 CollectGroupEnum；未收录的分组编码（后续新增未及时补枚举）回落编码原文，不报错 */
    public String resolveGroupName(String groupCode) {
        try {
            CollectGroupEnum group = CollectGroupEnum.fromCode(groupCode);
            return group != null ? group.getName() : groupCode;
        } catch (IllegalArgumentException e) {
            return groupCode;
        }
    }
}
