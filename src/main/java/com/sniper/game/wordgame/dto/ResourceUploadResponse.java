package com.sniper.game.wordgame.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源上传结果：OSS CDN 地址（公共上传接口，不落库）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceUploadResponse {

    /** OSS CDN 地址 */
    private String url;
}
