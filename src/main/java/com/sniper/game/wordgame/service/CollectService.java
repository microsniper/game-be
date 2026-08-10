package com.sniper.game.wordgame.service;

import com.sniper.game.wordgame.constant.enums.CollectGroupEnum;
import com.sniper.game.wordgame.dto.CollectItemDto;
import com.sniper.game.wordgame.mapper.GameCollectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收集品：只读目录下发（配置在 game_collect 表）。
 * 拥有/当前展示状态不落后端，由前端本地 CollectStore 处理（与道具/金币存储边界一致）。
 */
@Service
@RequiredArgsConstructor
public class CollectService {

    private final GameCollectMapper gameCollectMapper;

    public List<CollectItemDto> listCatalog() {
        List<CollectItemDto> items = gameCollectMapper.findAll();
        items.forEach(item -> item.setGroupName(resolveGroupName(item.getGroupCode())));
        return items;
    }

    /** 分组中文名取自 CollectGroupEnum；未收录的分组编码（后续新增未及时补枚举）回落编码原文，不报错 */
    private String resolveGroupName(String groupCode) {
        try {
            CollectGroupEnum group = CollectGroupEnum.fromCode(groupCode);
            return group != null ? group.getName() : groupCode;
        } catch (IllegalArgumentException e) {
            return groupCode;
        }
    }
}
