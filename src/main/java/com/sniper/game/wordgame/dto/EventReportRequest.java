package com.sniper.game.wordgame.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 埋点上报请求
 *
 * @author sniper
 */
@Data
public class EventReportRequest {

    /** 事件场景：revive-复活 / clear_tray-清空果盘 / unlock_basket-解锁果篮 */
    @NotBlank(message = "事件场景不能为空")
    private String scene;
}
