package com.sniper.game.wordgame.service;

import com.sniper.game.wordgame.constant.enums.GameRewardModeEnum;
import com.sniper.game.wordgame.dto.RewardCandidate;
import com.sniper.game.wordgame.dto.RewardItem;
import com.sniper.game.wordgame.exception.BusinessException;
import com.sniper.game.wordgame.mapper.GameRewardConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 过关奖励：配置放数据库（game_reward_config，按 mode+stage 区分），
 * 后端只做「查配置 / 按权重随机抽一条」，无副作用、不写任何用户状态。
 * 奖励实际发放（金币/道具入账）仍由前端写本地 PropStore/totalCoins。
 */
@Service
@RequiredArgsConstructor
public class RewardService {

    private final GameRewardConfigMapper gameRewardConfigMapper;

    /** 阶段 1 固定奖励（查询，无副作用） */
    public RewardItem getFixedReward(GameRewardModeEnum mode, Integer stage) {
        RewardCandidate candidate = gameRewardConfigMapper.findFixedReward(mode, stage);
        if (candidate == null) {
            throw BusinessException.notFound("奖励配置缺失：mode=" + mode + ", stage=" + stage);
        }
        return candidate.toRewardItem();
    }

    /** 阶段 2 按权重无放回抽 count 条（查询+随机，无副作用，不落库）；候选池不足几条就返回几条 */
    public List<RewardItem> draw(GameRewardModeEnum mode, Integer stage, Integer count) {
        List<RewardCandidate> candidates = gameRewardConfigMapper.findCandidates(mode, stage);
        if (candidates == null || candidates.isEmpty()) {
            throw BusinessException.notFound("奖励配置缺失：mode=" + mode + ", stage=" + stage);
        }
        int want = count != null && count > 0 ? count : 1;
        int n = Math.min(want, candidates.size());
        List<RewardCandidate> pool = new java.util.ArrayList<>(candidates);
        List<RewardItem> result = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) {
            RewardCandidate picked = pickByWeight(pool);
            result.add(picked.toRewardItem());
            pool.remove(picked);
        }
        return result;
    }

    /** 加权随机：权重为 null/<=0 时按 1 计，保证每条候选都有机会命中 */
    private RewardCandidate pickByWeight(List<RewardCandidate> candidates) {
        int totalWeight = candidates.stream()
                .mapToInt(c -> c.getWeight() != null && c.getWeight() > 0 ? c.getWeight() : 1)
                .sum();
        int roll = ThreadLocalRandom.current().nextInt(totalWeight);
        int cursor = 0;
        for (RewardCandidate candidate : candidates) {
            int w = candidate.getWeight() != null && candidate.getWeight() > 0 ? candidate.getWeight() : 1;
            cursor += w;
            if (roll < cursor) {
                return candidate;
            }
        }
        return candidates.get(candidates.size() - 1);
    }
}
