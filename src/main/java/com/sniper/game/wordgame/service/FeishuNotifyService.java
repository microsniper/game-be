package com.sniper.game.wordgame.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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

    @Resource
    private Environment environment;

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
     * @param dailyChallengeCount 今日每日挑战入口人数（独立挑战用户数）
     * @param endlessChallengeCount 今日无限模式入口人数（独立进入用户数）
     */
    @Async
    public void sendSummaryNotify(String envLabel, String now, long delta, long dailyTotal, String sceneDetail, long skipTotal, long dau, long signInCount, long dailyChallengeCount, long endlessChallengeCount) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            log.debug("飞书 Webhook URL 未配置，跳过通知");
            return;
        }

        try {
            String content = String.format(
                "时间：%s\n近10分钟看完广告新增：%d 次\n今日观看广告累计：%d 次\n%s\n广告中途关闭/跳过：%d 次\n\n\n日活：%d 人\n签到人数：%d 人\n每日挑战人数：%d 人\n无限模式人数：%d 人",
                now, delta, dailyTotal, sceneDetail, skipTotal, dau, signInCount, dailyChallengeCount, endlessChallengeCount
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

    /**
     * 接口异常告警（红色卡片）：GlobalExceptionHandler 兕底分支触发，字段在调用线程解析完再传入
     * （@Async 线程里 RequestContextHolder 为空，拿不到 request/userId）。
     *
     * @param time         告警时间（yyyy-MM-dd HH:mm:ss）
     * @param uri          接口地址
     * @param apiName      接口名称（@ApiName 中文，未标注为 类名.方法名）
     * @param userIdLabel  用户ID（异常发生在登录前为"未登录"）
     * @param errorMessage 告警信息（异常类名: message + 堆栈前5行）
     */
    @Async
    public void sendErrorAlert(String time, String uri, String apiName, String userIdLabel, String errorMessage) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            log.debug("飞书 Webhook URL 未配置，跳过异常告警");
            return;
        }

        try {
            String content = String.format(
                "时间：%s\n接口地址：%s\n接口名称：%s\n用户ID：%s\n告警信息：%s",
                time, uri, apiName, userIdLabel, errorMessage
            );

            JSONObject body = new JSONObject();
            body.put("msg_type", "interactive");

            JSONObject card = new JSONObject();
            JSONObject header = new JSONObject();
            header.put("title", new JSONObject() {{
                put("tag", "plain_text");
                put("content", String.format("[%s] 接口异常告警", resolveEnvLabel()));
            }});
            header.put("template", "red");
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
            log.info("飞书异常告警发送结果: {}", resp);
        } catch (Exception e) {
            log.error("飞书异常告警发送失败", e);
        }
    }

    /** 环境标识：与 EventReportScheduler 口径一致，prod 为生产，其余为测试 */
    private String resolveEnvLabel() {
        String[] profiles = environment != null ? environment.getActiveProfiles() : new String[0];
        String current = profiles.length > 0 ? profiles[0] : "prod";
        return "prod".equals(current) ? "生产" : "测试";
    }
}
