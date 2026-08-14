package com.sniper.game.wordgame.dto;

import lombok.Data;

import java.util.List;

/**
 * 按 collectCode 批量查收集品目录：单表 in 查询，不整表下发。
 * 奖励结果（RewardItem.collectCode）反查名称/id 用，本地已知目标 code，不需要拉全量目录再内存过滤。
 */
@Data
public class CollectByCodesRequest {

    /** 目标 collectCode 列表，调用方保证非空 */
    private List<String> codes;
}
