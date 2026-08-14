# 螺丝游戏项目规则

## 项目概述
螺丝解谜游戏，包含前端游戏（Cocos Creator）和后端（Spring Boot）两个子项目。

## 项目列表（/Users/sniper/java/project/self/personal/screw/）
| 目录 | 说明 |
|------|------|
| game-be | Spring Boot 后端服务 |
| unscrew | Cocos Creator 游戏项目（构建输出微信小游戏 / 抖音小游戏） |

## 前端游戏 (unscrew)
- **路径**: /Users/sniper/java/project/self/personal/screw/unscrew
- **引擎**: Cocos Creator 4.x (TypeScript)
- **核心脚本**: assets/Scripts/GameManager.ts（游戏主逻辑）、assets/Scripts/api.ts（接口层）
- **API 地址**: https://game.sniper.net.cn
- **构建目标**: 微信小游戏 (wechatgame) / 抖音小游戏 (bytedance-mini-game)
- **平台判断**: api.ts 中通过 `typeof tt` 判断抖音、`typeof wx` 判断微信
- **抖音登录**: 调用 `tt.login()` 获取 code，传 `source: "DOUYIN"` 给后端

## 后端项目 (game-be)
- **路径**: /Users/sniper/java/project/self/personal/screw/game-be
- **框架**: Spring Boot 2.7.12 + JDK 1.8 + Maven
- **包路径**: com.sniper.game.wordgame
- **数据库**: MySQL (39.105.174.212:3300) + Redis
- **认证方式**: Bearer Token（Redis存储，7天过期）
- **已集成**: 微信登录、抖音登录、阿里云OSS、通义千问SDK
- **部署**: Docker + docker-compose，服务器 9001 端口

### 抖音登录注意事项
- **抖音 API 是 POST 请求**，参数通过 JSON body 传 `{appid, secret, code}`
- **不是 GET**，不能用 `restTemplate.getForObject`
- URL: `https://developer.toutiao.com/api/apps/v2/jscode2session`

## 代码规范
- 前端: Cocos Creator + TypeScript
- 后端: Lombok 简化代码
- 统一响应格式: Result(code, message, data)

## 工作约定
- 写完代码后，要提交并 push

## 难度配置
### 每日挑战
FRUIT_BLOCK_COVERAGE = 10% 下层水果被上层板盖住 ≥10% 就判不可点
LAYER_INITIAL_LOAD = 2   开局首批加载 2 层
LAYER_MAX_COUNT = 10   一关最多 10 层

## 上传图片
curl -X POST 'http://localhost:8080/api/game/resource/upload' \
  -H 'token: <你的登录token>' \
  -F 'file=@/path/to/reward.png'
  
INSERT INTO game_resource (name, url, type) VALUES ('签到第1天奖励', '<上面的url>', 'image');
INSERT INTO sign_in_reward (day_num, resource_id, reward_type, amount) VALUES (1, LAST_INSERT_ID(), 'sun', 50);

## 后端项目部署命令
```bash
# 启动测试环境容器 (映射 9002 端口，使用 application-test.yml)
docker build -t game-test:latest . 
docker compose up -d game-test --force-recreate

# 启动生产环境容器 (映射 9001 端口，使用 application-prod.yml)
docker build -t game-prod:latest . 
docker compose up -d game-prod --force-recreate
```
## 解析base64图片的
jq -r '.data[0].b64_json' response.json | base64 -d > picture.png


## 密码
redis密码 Zp0303171
MySQL密码 Zp0303171

## 分包命令
python3 fix_subpackage.py

