package com.sniper.game.wordgame.dto;

import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.constant.enums.SourceEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {

    private String token;

    private String openid;

    private SourceEnum source;

    private Boolean hasProfile;

    private Progress progress;

    private Boolean isNewUser;

    private Boolean dailyRewardClaimable;

    /** 用户已选的地区ID（region.id），没选过为 null，前端据此决定要不要弹选地区 */
    private Integer regionId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Progress {

        private GameTypeEnum gameType;

        private Integer levelNum;
    }
}
