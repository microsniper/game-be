package com.sniper.game.wordgame.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * 飞书机器人通知服务
 *
 * @author sniper
 */
@Slf4j
@Service
public class FeishuNotifyService {

    @Value("${feishu.webhook.url}")
    private String webhookUrl;

    @Resource
    private RestTemplate restTemplate;

    /**
     * 发送定时汇总通知（每10分钟）
     *
     * @param envLabel    环境标识（生产/测试）
     * @param now         当前时间
     * @param delta       本周期看完广告新增
     * @param dailyTotal  今日观看广告累计
     * @param sceneDetail 各场景明细
     * @param skipTotal   广告中途关闭/跳过总次数
     * @param dau         今日日活（独立登录用户数）
     * @param signInCount 今日签到人数（独立签到用户数）
     */
    @Async
    public void sendSummaryNotify(String envLabel, String now, long delta, long dailyTotal, String sceneDetail, long skipTotal, long dau, long signInCount) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            log.debug("飞书 Webhook URL 未配置，跳过通知");
            return;
        }

        try {
            String content = String.format(
                "时间：%s\n近10分钟看完广告新增：%d 次\n今日观看广告累计：%d 次\n%s\n广告中途关闭/跳过：%d 次\n\n\n日活：%d 人\n签到人数：%d 人",
                now, delta, dailyTotal, sceneDetail, skipTotal, dau, signInCount
            );

            JSONObject body = new JSONObject();
            body.put("msg_type", "interactive");

            JSONObject card = new JSONObject();
            JSONObject header = new JSONObject();
            header.put("title", new JSONObject() {{
                put("tag", "plain_text");
                put("content", String.format("[%s] 广告观看汇总", envLabel));
            }});
            header.put("template", "green");
            card.put("header", header);

            JSONObject elements = new JSONObject();
            elements.put("tag", "div");
            elements.put("text", new JSONObject() {{
                put("tag", "lark_md");
                put("content", content);
            }});
            card.put("elements", new Object[]{elements});
            body.put("card", card);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(body.toJSONString(), headers);

            String resp = restTemplate.postForObject(webhookUrl, request, String.class);
            log.info("飞书汇总通知发送结果: {}", resp);
        } catch (Exception e) {
            log.error("飞书汇总通知发送失败", e);
        }
    }
}
