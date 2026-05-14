# 螺丝游戏项目规则

## 项目概述
螺丝解谜游戏，包含前端（uni-app）和后端（Spring Boot）两个子项目。

## 前端项目 (screw-game)
- **路径**: /Users/sniper/java/project/self/personal/screw/screw-game
- **框架**: uni-app (Vue 3 + Vite + TypeScript)
- **入口**: src/main.ts
- **核心页面**: src/pages/index/index.vue（游戏主页面，约1263行）
- **API 工具**: src/utils/api.ts（接口地址: https://game.sniper.net.cn）
- **多平台支持**: H5 / 微信小程序 / 支付宝小程序等

## 后端项目 (game-be)
- **路径**: /Users/sniper/java/project/self/personal/screw/game-be
- **框架**: Spring Boot 2.7.12 + JDK 1.8 + Maven
- **包路径**: com.sniper.game.wordgame
- **数据库**: MySQL (39.105.174.212:3300) + Redis
- **认证方式**: Bearer Token（Redis存储，7天过期）
- **已集成**: 微信登录、阿里云OSS、通义千问SDK
- **状态**: 基础设施已搭建，业务代码尚未编写

## 代码规范
- 使用 TypeScript 严格模式
- Vue 3 Composition API (`<script setup lang="ts">`)
- 后端使用 Lombok 简化代码
- 统一响应格式: Result(code, message, data)

## 工作约定
- 写完代码后，要提交并 push
