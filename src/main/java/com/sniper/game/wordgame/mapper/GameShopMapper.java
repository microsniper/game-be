package com.sniper.game.wordgame.mapper;

import com.sniper.game.wordgame.dto.ShopItemDto;
import com.sniper.game.wordgame.dto.StorageGroupDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GameShopMapper {

    /** 道具分页：JOIN game_resource，按 sort_order 排序 */
    List<ShopItemDto> findTools(@Param("offset") int offset, @Param("limit") int limit);

    /** 道具总数 */
    int countTools();

    /** 收集分页：JOIN game_collect，可选按 groupCode 筛选，按 sort_order 排序 */
    List<ShopItemDto> findCollect(@Param("groupCode") String groupCode,
                                   @Param("offset") int offset, @Param("limit") int limit);

    /** 收集总数（同 findCollect 的筛选条件） */
    int countCollect(@Param("groupCode") String groupCode);

    /** 收集品在售的全部分组（与 groupCode 筛选无关，tab 栏用），按分组内最小 sort_order 排序 */
    List<StorageGroupDto> findCollectGroups();
}
