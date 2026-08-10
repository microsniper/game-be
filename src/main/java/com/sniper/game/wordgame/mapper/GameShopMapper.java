package com.sniper.game.wordgame.mapper;

import com.sniper.game.wordgame.dto.ShopItemDto;

import java.util.List;

public interface GameShopMapper {

    /** 上架商品目录：category=1 JOIN game_resource、category=2 JOIN game_collect，按 category+sort_order 排序 */
    List<ShopItemDto> findAllEnabled();
}
