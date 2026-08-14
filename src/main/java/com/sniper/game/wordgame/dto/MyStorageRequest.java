package com.sniper.game.wordgame.dto;

import lombok.Data;

/**
 * 我的仓库查询入参：按分组筛选 + 分页。
 * 三个字段都可不传：groupCode 空=全部分组；page 空=第1页；pageSize 空=10 条。
 */
@Data
public class MyStorageRequest {

    /** 分组编码（CollectGroupEnum.code），不传返回全部分组 */
    private String groupCode;

    /** 页码，从 1 开始 */
    private Integer page;

    /** 每页条数 */
    private Integer pageSize;
}
