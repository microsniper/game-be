package com.sniper.game.wordgame.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通用资源（上传到 OSS 的图片登记）
 */
@Data
public class GameResource {

    private Long id;

    /** 资源说明 */
    private String name;

    /** OSS CDN 地址 */
    private String url;

    /** 资源类型：image */
    private String type;

    private LocalDateTime createTime;
}
