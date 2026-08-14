package com.sniper.game.wordgame.dto;

import lombok.Data;

import java.util.List;

/**
 * 我的仓库分页结果。groups 是该用户拥有的全部分组（不随 groupCode 筛选变化），
 * 供前端渲染二级 tab；items/total 才是当前分组当前页的数据。
 */
@Data
public class MyStoragePageDto {

    /** 当前分组当前页的条目 */
    private List<MyStorageItemDto> items;

    /** 该用户拥有的分组列表（tab 栏用，与筛选条件无关） */
    private List<StorageGroupDto> groups;

    /** 当前筛选条件下的总条数（未分页前） */
    private Integer total;

    private Integer page;

    private Integer pageSize;
}
