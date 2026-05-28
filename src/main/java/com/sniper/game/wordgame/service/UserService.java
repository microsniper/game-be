package com.sniper.game.wordgame.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sniper.game.wordgame.constant.CommonConstants;
import com.sniper.game.wordgame.constant.RedisKeyConstants;
import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.constant.enums.SourceEnum;
import com.sniper.game.wordgame.dto.LoginResponse;
import com.sniper.game.wordgame.entity.User;
import com.sniper.game.wordgame.entity.UserProgress;
import com.sniper.game.wordgame.exception.BusinessException;
import com.sniper.game.wordgame.mapper.UserMapper;
import com.sniper.game.wordgame.mapper.UserProgressMapper;
import com.sniper.game.wordgame.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserProgressMapper userProgressMapper;
    private final RedisUtils redisUtils;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${wx.miniapp.appid:}")
    private String miniappAppid;

    @Value("${wx.miniapp.secret:}")
    private String miniappSecret;

    @Value("${wx.miniapp.jscode2session-url:https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code}")
    private String jscode2sessionUrl;

    @Value("${dy.miniapp.appid:}")
    private String dyMiniAppId;

    @Value("${dy.miniapp.secret:}")
    private String dyMiniAppSecret;

    @Value("${dy.miniapp.jscode2session-url:https://developer.toutiao.com/api/apps/v2/jscode2session?appid=%s&secret=%s&code=%s}")
    private String dyJscode2sessionUrl;

    public LoginResponse login(String code, GameTypeEnum gameType, SourceEnum source) {
        String openid;
        String unionid;
        if (source == SourceEnum.DOUYIN) {
            JSONObject session = getDouyinSession(code);
            openid = session.getString("openid");
            unionid = null;
        } else {
            JSONObject session = getWechatSession(code);
            openid = session.getString("openid");
            unionid = StringUtils.trimToNull(session.getString("unionid"));
        }

        if (StringUtils.isBlank(openid)) {
            throw BusinessException.badRequest("登录失败，未获取到openid");
        }

        User user = userMapper.findByOpenidAndGameType(openid, gameType);
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setUnionid(unionid);
            user.setSource(source);
            user.setGameType(gameType);
            userMapper.insert(user);
        }

        UserProgress progress = userProgressMapper.findByUserIdAndGameType(user.getId(), gameType);
        if (progress == null) {
            progress = new UserProgress();
            progress.setUserId(user.getId());
            progress.setGameType(gameType);
            progress.setLevelNum(1);
            progress.setSource(source);
            userProgressMapper.insert(progress);
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        redisUtils.set(buildTokenKey(token), user.getId(), CommonConstants.TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);

        return new LoginResponse(token, source, new LoginResponse.Progress(progress.getGameType(), progress.getLevelNum()));
    }

    public void saveProgress(Long userId, GameTypeEnum gameType, Integer levelNum) {
        if (userId == null) {
            throw BusinessException.unauthorized("请先登录");
        }

        User user = userMapper.findById(userId);

        UserProgress progress = userProgressMapper.findByUserIdAndGameType(userId, gameType);
        if (progress == null) {
            progress = new UserProgress();
            progress.setUserId(userId);
            progress.setGameType(gameType);
            progress.setLevelNum(levelNum);
            progress.setSource(user != null ? user.getSource() : SourceEnum.WECHAT);
            userProgressMapper.insert(progress);
            return;
        }

        if (levelNum <= progress.getLevelNum()) {
            return;
        }
        userProgressMapper.updateLevelNum(userId, gameType, levelNum);
    }

    public Long getUserIdByToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        Object value = redisUtils.get(buildTokenKey(token));
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }

    private JSONObject getWechatSession(String code) {
        if (StringUtils.isAnyBlank(miniappAppid, miniappSecret)) {
            throw new BusinessException(500, "微信小程序登录配置缺失");
        }

        String requestUrl = String.format(jscode2sessionUrl, miniappAppid, miniappSecret, code);
        try {
            String response = restTemplate.getForObject(requestUrl, String.class);
            if (StringUtils.isBlank(response)) {
                throw BusinessException.badRequest("微信登录失败，请稍后重试");
            }

            JSONObject jsonObject = JSON.parseObject(response);
            Integer errcode = jsonObject.getInteger("errcode");
            if (errcode != null && errcode != 0) {
                String errmsg = jsonObject.getString("errmsg");
                log.warn("微信登录失败: errcode={}, errmsg={}", errcode, errmsg);
                throw BusinessException.badRequest("微信登录失败:" + StringUtils.defaultIfBlank(errmsg, "未知错误"));
            }
            return jsonObject;
        } catch (RestClientException e) {
            log.error("调用微信登录接口异常", e);
            throw new BusinessException(500, "微信登录失败，请稍后重试");
        }
    }

    private JSONObject getDouyinSession(String code) {
        if (StringUtils.isAnyBlank(dyMiniAppId, dyMiniAppSecret)) {
            throw new BusinessException(500, "抖音小程序登录配置缺失");
        }

        Map<String, String> body = new HashMap<>();
        body.put("appid", dyMiniAppId);
        body.put("secret", dyMiniAppSecret);
        body.put("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            String response = restTemplate.postForObject(dyJscode2sessionUrl, requestEntity, String.class);
            if (StringUtils.isBlank(response)) {
                throw BusinessException.badRequest("抖音登录失败，请稍后重试");
            }

            JSONObject jsonObject = JSON.parseObject(response);
            Integer errNo = jsonObject.getInteger("err_no");
            if (errNo != null && errNo != 0) {
                String errTips = jsonObject.getString("err_tips");
                log.warn("抖音登录失败: err_no={}, err_tips={}", errNo, errTips);
                throw BusinessException.badRequest("抖音登录失败:" + StringUtils.defaultIfBlank(errTips, "未知错误"));
            }

            JSONObject data = jsonObject.getJSONObject("data");
            if (data == null) {
                throw BusinessException.badRequest("抖音登录失败，未获取到用户数据");
            }
            return data;
        } catch (RestClientException e) {
            log.error("调用抖音登录接口异常", e);
            throw new BusinessException(500, "抖音登录失败，请稍后重试");
        }
    }

    private String buildTokenKey(String token) {
        return RedisKeyConstants.USER_LOGIN_TOKEN + token;
    }
}
