package com.sniper.game.wordgame.mapper;

import com.sniper.game.wordgame.dto.CollectItemDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GameCollectMapper {

    /** 收集品全量目录（仓库页/猫咪图标用，只读配置） */
    List<CollectItemDto> findAll();

    /** 随机抽取指定数量的 fruit 分组收集品（首页圆盘人群用，LIMIT 兜底防爆） */
    List<CollectItemDto> findRandomFruits(@Param("limit") int limit);

    /**
     * 按 id 批量取目录（我的仓库/按需查询用）：单表 in 查询，不连表。
     * 调用方保证 ids 非空，拼装关联在 Service 内存里做，后续目录进 Redis 可整段替换这里。
     */
    List<CollectItemDto> findByIds(@Param("ids") List<Long> ids);

    /** 新用户默认赠送的那一条（is_starter_gift=1），未配置返回 null；LIMIT 1 兜底防多条配置 */
    CollectItemDto findStarterGift();

    /**
     * 按 collect_code 批量取目录（奖励结果反查名称/id 用）：单表 in 查询，不连表。
     * 调用方保证 codes 非空。
     */
    List<CollectItemDto> findByCodes(@Param("codes") List<String> codes);
}
