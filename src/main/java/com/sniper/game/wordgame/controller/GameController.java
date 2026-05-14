package com.sniper.game.wordgame.controller;

import com.sniper.game.wordgame.dto.LoginRequest;
import com.sniper.game.wordgame.dto.LoginResponse;
import com.sniper.game.wordgame.dto.ProgressRequest;
import com.sniper.game.wordgame.service.UserService;
import com.sniper.game.wordgame.util.UserContext;
import com.sniper.game.wordgame.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request.getCode(), request.getGameType(), request.getSource()));
    }

    @PostMapping("/progress")
    public Result<Void> saveProgress(@Valid @RequestBody ProgressRequest request) {
        userService.saveProgress(UserContext.getCurrentUserId(), request.getGameType(), request.getLevelNum());
        return Result.success();
    }
}
