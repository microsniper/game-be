package com.sniper.game.wordgame;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.sniper.game.wordgame.mapper")
public class WordgameApplication {

    public static void main(String[] args) {
        SpringApplication.run(WordgameApplication.class, args);
    }

}
