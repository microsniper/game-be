package com.sniper.game.wordgame.service;

import com.sniper.game.wordgame.dto.ShopItemDto;
import com.sniper.game.wordgame.mapper.GameShopMapper;
import lombok.RequiredArgsConstructor;
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
        return gameShopMapper.findAllEnabled();
    }
}
