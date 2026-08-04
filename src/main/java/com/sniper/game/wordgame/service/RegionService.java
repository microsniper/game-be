package com.sniper.game.wordgame.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sniper.game.wordgame.entity.Region;
import com.sniper.game.wordgame.entity.User;
import com.sniper.game.wordgame.exception.BusinessException;
import com.sniper.game.wordgame.mapper.RegionMapper;
import com.sniper.game.wordgame.mapper.UserMapper;
import com.sniper.game.wordgame.util.RedisUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 地区服务：省级行政区字典的读取（缓存优先）与用户地区保存。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegionService {

    /** 地区列表缓存 key（公共字典，跟用户/游戏无关） */
    private static final String REGION_LIST_CACHE_KEY = "game:region:list";
    /** 字典基本不变，缓存放 1 天，纯粹当兜底刷新 */
    private static final long REGION_CACHE_DAYS = 1;

    private final RegionMapper regionMapper;
    private final UserMapper userMapper;
    private final RedisUtils redisUtils;
    private final ObjectMapper objectMapper;

    /**
     * 拉地区列表：先读 Redis，没有再查库并回填缓存。
     * 返回给前端的精简结构，只有 id 和 name（选择弹窗够用）。
     * 注意：Redis 反序列化会丢类型信息（元素变 LinkedHashMap），必须用 convertValue 转回 RegionItem，
     * 否则直接强转会在消费元素时报 ClassCastException。
     */
    public List<RegionItem> listRegions() {
        Object cached = redisUtils.get(REGION_LIST_CACHE_KEY);
        if (cached instanceof List) {
            return objectMapper.convertValue(cached, new TypeReference<List<RegionItem>>() {});
        }
        List<Region> regions = regionMapper.findAllEnabled();
        List<RegionItem> items = new ArrayList<>(regions.size());
        for (Region region : regions) {
            items.add(new RegionItem(region.getId(), region.getName()));
        }
        redisUtils.set(REGION_LIST_CACHE_KEY, items, REGION_CACHE_DAYS, TimeUnit.DAYS);
        return items;
    }

    /**
     * 保存用户地区：先对照字典校验 regionId 合法，再写入 user.region_id。
     */
    public void saveUserRegion(Long userId, Integer regionId) {
        if (userId == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        if (regionId == null) {
            throw BusinessException.badRequest("地区不能为空");
        }
        User user = userMapper.findById(userId);
        if (user == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        boolean valid = regionMapper.countEnabledById(regionId) > 0;
        if (!valid) {
            throw BusinessException.badRequest("无效的地区");
        }
        userMapper.updateRegionId(userId, regionId);
    }

    /** 前端选择弹窗用的精简地区项 */
    @Data
    public static class RegionItem {
        private Integer id;
        private String name;

        public RegionItem() {
        }

        public RegionItem(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
