package com.sniper.game.wordgame.entity;

import com.sniper.game.wordgame.constant.enums.ResourceCodeTypeEnum;
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

    /** 资源类型编码（ResourceCodeTypeEnum 的 code），程序按 code 查资源用，可空 */
    private ResourceCodeTypeEnum resourceCode;

    private LocalDateTime createTime;
}
