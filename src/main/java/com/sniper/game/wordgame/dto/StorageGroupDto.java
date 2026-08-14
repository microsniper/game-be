package com.sniper.game.wordgame.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仓库分组 tab：只含该用户实际有持有记录的分组。
 * 分页后前端拿不到全量目录，算不出 tab 列表，所以由服务端一并下发。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageGroupDto {

    /** 分组编码（CollectGroupEnum.code），前端 subTab 的 key */
    private String groupCode;

    /** 分组中文名 */
    private String groupName;
}
