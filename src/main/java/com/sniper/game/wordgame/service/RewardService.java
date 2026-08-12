package com.sniper.game.wordgame.service;

import com.sniper.game.wordgame.constant.enums.ItemTypeEnum;
import com.sniper.game.wordgame.constant.enums.ResourceCodeTypeEnum;
import com.sniper.game.wordgame.dto.CollectItemDto;
import com.sniper.game.wordgame.dto.RewardItem;
import com.sniper.game.wordgame.entity.GameResource;
import com.sniper.game.wordgame.mapper.GameCollectMapper;
import com.sniper.game.wordgame.mapper.GameResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 过关奖励：规则硬编码在代码里（game_reward_config 表已废弃），抽奖池复用 game_resource/game_collect 两张目录表。
 * 每日挑战：stage1=金币200；stage2=道具池等概率抽1（3道具+2特殊果）；stage3=收集表等概率抽1。
 * 无限模式：普通关=[金币200]；5的倍数关=[金币200 + 50/50 定道具/收集后等概率抽1]。
 * 无副作用、不写任何用户状态；奖励实际发放（金币/道具入账）仍由前端写本地 PropStore/totalCoins。
 * 收集品抽奖会排除前端传入的已拥有编码（ownedCollectCodes）；排除后池子为空（已全部拥有）则回退全量池抽。道具/特殊果不做排除。
 * 池子空返回空列表（不含该条），调用方自行兜底，不阻断过关。
 */
@Service
@RequiredArgsConstructor
public class RewardService {

    /** 固定金币奖励数（两模式统一） */
    private static final int COIN_AMOUNT = 200;

    /** 道具抽奖池：除金币外全部资源编码（3 道具 + 2 特殊果） */
    private static final List<ResourceCodeTypeEnum> PROP_POOL = Arrays.asList(
            ResourceCodeTypeEnum.ADD_TRAY, ResourceCodeTypeEnum.CLEAR, ResourceCodeTypeEnum.ADD,
            ResourceCodeTypeEnum.RAINBOW, ResourceCodeTypeEnum.BOMB);

    private final GameResourceMapper gameResourceMapper;
    private final GameCollectMapper gameCollectMapper;

    /** 每日挑战按 stage 发奖：1=金币 2=道具抽 3=收集抽；stage 越界返回空列表 */
    public List<RewardItem> dailyStageReward(int stage, List<String> ownedCollectCodes) {
        List<RewardItem> list = new ArrayList<>();
        if (stage == 1) {
            addIfNotNull(list, coinReward());
        } else if (stage == 2) {
            addIfNotNull(list, randomProp());
        } else if (stage == 3) {
            addIfNotNull(list, randomCollect(ownedCollectCodes));
        }
        return list;
    }

    /** 无限模式过关结算：普通关=[金币]；5 的倍数关=[金币 + 随机类型抽1] */
    public List<RewardItem> endlessClearReward(int level, List<String> ownedCollectCodes) {
        List<RewardItem> list = new ArrayList<>();
        addIfNotNull(list, coinReward());
        if (level % 5 == 0) {
            RewardItem drawn = ThreadLocalRandom.current().nextBoolean()
                    ? randomProp() : randomCollect(ownedCollectCodes);
            addIfNotNull(list, drawn);
        }
        return list;
    }

    private void addIfNotNull(List<RewardItem> list, RewardItem item) {
        if (item != null) list.add(item);
    }

    /** 金币奖励：图/名取 game_resource code=1 行 */
    private RewardItem coinReward() {
        GameResource coin = findByCode(ResourceCodeTypeEnum.COIN);
        if (coin == null) return null;
        RewardItem item = new RewardItem();
        item.setItemType(ItemTypeEnum.PROP);
        item.setResourceCode(ResourceCodeTypeEnum.COIN);
        item.setAmount(COIN_AMOUNT);
        item.setImageUrl(coin.getUrl());
        return item;
    }

    /** 道具池等概率抽 1（道具/特殊果消耗型资源，不做已拥有排除） */
    private RewardItem randomProp() {
        List<GameResource> pool = new ArrayList<>();
        for (GameResource r : gameResourceMapper.findAllWithCode()) {
            if (r.getResourceCode() != null && PROP_POOL.contains(r.getResourceCode())) {
                pool.add(r);
            }
        }
        if (pool.isEmpty()) return null;
        GameResource picked = pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
        RewardItem item = new RewardItem();
        item.setItemType(ItemTypeEnum.PROP);
        item.setResourceCode(picked.getResourceCode());
        item.setAmount(1);
        item.setImageUrl(picked.getUrl());
        return item;
    }

    /**
     * 收集全量目录等概率抽 1：优先排除 ownedCodes 中已拥有的收集品编码；排除后池子为空（已全部拥有）则回退全量池抽。
     */
    private RewardItem randomCollect(List<String> ownedCodes) {
        List<CollectItemDto> all = gameCollectMapper.findAll();
        if (all == null || all.isEmpty()) return null;
        List<CollectItemDto> candidates = all;
        if (ownedCodes != null && !ownedCodes.isEmpty()) {
            List<CollectItemDto> filtered = new ArrayList<>();
            for (CollectItemDto c : all) {
                if (!ownedCodes.contains(c.getCollectCode())) {
                    filtered.add(c);
                }
            }
            if (!filtered.isEmpty()) {
                candidates = filtered;
            }
        }
        CollectItemDto picked = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        RewardItem item = new RewardItem();
        item.setItemType(ItemTypeEnum.COLLECT);
        item.setCollectCode(picked.getCollectCode());
        item.setAmount(1);
        item.setImageUrl(picked.getColorUrl());
        return item;
    }

    private GameResource findByCode(ResourceCodeTypeEnum code) {
        for (GameResource r : gameResourceMapper.findAllWithCode()) {
            if (code.equals(r.getResourceCode())) return r;
        }
        return null;
    }
}
