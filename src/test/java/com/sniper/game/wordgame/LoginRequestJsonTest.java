package com.sniper.game.wordgame;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sniper.game.wordgame.constant.enums.GameTypeEnum;
import com.sniper.game.wordgame.constant.enums.SourceEnum;
import com.sniper.game.wordgame.dto.LoginRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginRequestJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldDeserializeNumericEnums() throws Exception {
        String json = "{\"code\":\"mock-code\",\"gameType\":1,\"source\":1}";

        LoginRequest request = objectMapper.readValue(json, LoginRequest.class);

        assertEquals("mock-code", request.getCode());
        assertEquals(GameTypeEnum.SCREW, request.getGameType());
        assertEquals(SourceEnum.WECHAT, request.getSource());
    }

    @Test
    void shouldDeserializeEnumNames() throws Exception {
        String json = "{\"code\":\"mock-code\",\"gameType\":\"SCREW\",\"source\":\"WECHAT\"}";

        LoginRequest request = objectMapper.readValue(json, LoginRequest.class);

        assertEquals(GameTypeEnum.SCREW, request.getGameType());
        assertEquals(SourceEnum.WECHAT, request.getSource());
    }

    @Test
    void shouldUseDefaultEnumsWhenFieldsMissing() throws Exception {
        String json = "{\"code\":\"mock-code\"}";

        LoginRequest request = objectMapper.readValue(json, LoginRequest.class);

        assertEquals("mock-code", request.getCode());
        assertEquals(GameTypeEnum.SCREW, request.getGameType());
        assertEquals(SourceEnum.WECHAT, request.getSource());
    }
}
