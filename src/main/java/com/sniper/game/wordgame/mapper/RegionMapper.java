package com.sniper.game.wordgame.mapper;

import com.sniper.game.wordgame.entity.Region;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RegionMapper {

    /** 查所有启用的地区，按 sort_order 升序（缓存未命中时回源用） */
    List<Region> findAllEnabled();

    /** 校验用：指定 id 的启用地区是否存在（不走缓存，避开反序列化类型问题） */
    int countEnabledById(@Param("id") Integer id);
}
