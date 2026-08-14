package com.sniper.game.wordgame.dto;

import lombok.Data;

/**
 * 商城查询入参：按分类（必传）+ 分组（收集品可选）+ 分页。
 * category 非法或不传按 1（道具）处理；groupCode 只对 category=2 生效，传了非法值等价于查全部。
 */
@Data
public class ShopListRequest {

    /** 分类：1=道具 2=收集，不传或非法值按 1 处理 */
    private Integer category;

    /** 收集品分组编码（CollectGroupEnum.code），仅 category=2 时生效，不传返回该分类下全部分组 */
    private String groupCode;

    /** 页码，从 1 开始 */
    private Integer page;

    /** 每页条数 */
    private Integer pageSize;
}
