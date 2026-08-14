package com.sniper.game.wordgame.dto;

import lombok.Data;

import java.util.List;

/**
 * 按 id 批量查收集品目录：单表 in 查询，不整表下发。
 * 猫咪图标（查当前展示项）、抽奖排除已拥有等场景本地已知目标 id，不需要拉全量目录再内存过滤。
 */
@Data
public class CollectByIdsRequest {

    /** 目标 id 列表，调用方保证非空 */
    private List<Long> ids;
}
