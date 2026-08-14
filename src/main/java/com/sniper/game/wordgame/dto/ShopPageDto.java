package com.sniper.game.wordgame.dto;

import lombok.Data;

import java.util.List;

/**
 * 商城分页结果。groups 仅 category=2（收集）时有值，是该分类下全部分组（与 groupCode 筛选无关），
 * 供前端渲染二级 tab；items/total 才是当前分类当前分组当前页的数据。
 */
@Data
public class ShopPageDto {

    /** 当前分类当前分组当前页的条目 */
    private List<ShopItemDto> items;

    /** category=2 时该分类下全部分组列表（tab 栏用），category=1 时为空列表 */
    private List<StorageGroupDto> groups;

    /** 当前筛选条件下的总条数（未分页前） */
    private Integer total;

    private Integer page;

    private Integer pageSize;
}
