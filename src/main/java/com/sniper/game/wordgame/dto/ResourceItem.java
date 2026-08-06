package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.ResourceCodeTypeEnum;
import lombok.Data;

/**
 * 资源下发明细（game_resource 中登记了类型编码的一条数据）：
 * 前端按 resourceCode 组 Map，value 即本结构。
 */
@Data
public class ResourceItem {

    /** 资源类型编码（ResourceCodeTypeEnum）；JSON 序列化为小写 code */
    private ResourceCodeTypeEnum resourceCode;

    /** OSS CDN 地址 */
    private String url;

    /** 资源说明 */
    private String name;

    /** 资源类型：image */
    private String type;
}
