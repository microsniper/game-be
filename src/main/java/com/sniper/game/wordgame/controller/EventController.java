package com.sniper.game.wordgame.controller;

import com.sniper.game.wordgame.dto.EventReportRequest;
import com.sniper.game.wordgame.util.RedisUtils;
import com.sniper.game.wordgame.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 埋点上报接口
 *
 * @author sniper
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/game/event")
@RequiredArgsConstructor
public class EventController {

    private final RedisUtils redisUtils;

    /**
     * 埋点上报（仅计数，通知由定时任务批量发送）
     */
    @PostMapping("/report")
    public Result<Void> report(@Valid @RequestBody EventReportRequest request) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String dailyKey = "game:event:daily:" + today;
        String sceneKey = "game:event:daily:" + today + ":" + request.getScene();
        Long dailyCount = redisUtils.increment(dailyKey, 1);
        redisUtils.expire(dailyKey, 36, TimeUnit.HOURS);
        redisUtils.increment(sceneKey, 1);
        redisUtils.expire(sceneKey, 36, TimeUnit.HOURS);

        log.info("埋点上报: scene={}, dailyTotal={}", request.getScene(), dailyCount);
        return Result.success();
    }
}
