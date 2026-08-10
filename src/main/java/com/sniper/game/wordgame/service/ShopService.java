package com.sniper.game.wordgame.service;

import com.sniper.game.wordgame.constant.enums.CollectGroupEnum;
import com.sniper.game.wordgame.dto.ShopItemDto;
import com.sniper.game.wordgame.mapper.GameShopMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商城：只读目录下发（配置在 game_shop 表，资源关联 game_resource/game_collect）。
 * 购买发放不落后端，由前端本地账处理（与道具/金币存储边界一致）。
 */
@Service
@RequiredArgsConstructor
public class ShopService {

    private final GameShopMapper gameShopMapper;

    public List<ShopItemDto> listEnabled() {
        List<ShopItemDto> items = gameShopMapper.findAllEnabled();
        items.forEach(item -> {
            if (item.getCategory() != null && item.getCategory() == 2) {
                item.setGroupName(resolveGroupName(item.getGroupCode()));
            }
        });
        return items;
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
