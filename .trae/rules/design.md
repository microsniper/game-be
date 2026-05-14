# 螺丝游戏 — 用户进度存档方案

> 状态：讨论中 | 未开始编码

---

## 一、整体流程

```
用户打开小程序
    │
    ▼
uni.login() 获取微信 code
    │
    ▼
POST /api/game/login  { code }
    │
    ├─► 后端用 code 调用微信接口获取 openid / unionid
    ├─► 查 DB 判断是否新用户
    │     ├─ 新用户 → INSERT user + INSERT user_progress(level_num=1, game_type=SCREW)
    │     └─ 老用户 → 查出 user_progress.level_num（按 game_type=SCREW 查）
    ├─► 生成 token 存 Redis（key: wordgame:user:login:{token}，7天过期）
    │
    ▼
返回 { token, progress: { levelNum: N } }
    │
    ▼
前端：取服务端返回的 levelNum 作为当前关卡开始玩
    │
    ▼
用户通关 → levelNum++ → POST /api/game/progress { levelNum }
    │
    ▼
用户未通关 → 不保存，下次进入还是从同一个 levelNum 开始
```

---

## 二、本地缓存策略

| 项目 | 决定 |
|------|------|
| 存储方式 | **仅 JS 内存变量**（不用 uni.setStorageSync） |
| 小程序杀掉后 | **数据消失**，重新从服务端获取进度 |
| 用户想从第 0 关重来 | 杀掉小程序重开即可 |
| 兜底 | 服务端是唯一权威数据源 |

### 为什么不用持久化？

> 用户可能想从 0 关重玩。如果持久化到微信存储，即使用户杀了小程序，重新进入还是从上次的关卡开始，无法回到第 1 关。

> 只要内存变量，杀掉就清空，下次进入走服务端获取进度，灵活性更高。

---

## 三、接口设计

### 3.1 POST /api/game/login

```
请求:
{
  "code": "wx_login_code"  // 微信 wx.login() 返回的 code
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "xxxxxxxxx",
    "progress": {
      "levelNum": 5
    }
  }
}
```

### 3.2 POST /api/game/progress

```
请求头: Authorization: Bearer xxxxxxxxx

请求:
{
  "levelNum": 6
}

响应:
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 四、数据库表设计

### 4.1 user（用户表）

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | BIGINT | 用户ID | PK, AUTO_INCREMENT |
| openid | VARCHAR(64) | 微信openid | NOT NULL |
| unionid | VARCHAR(64) | 微信unionid | NULL（预留） |
| source | VARCHAR(16) | 来源渠道（枚举） | NOT NULL, DEFAULT 'WECHAT' |
| game_type | VARCHAR(32) | 游戏类型（枚举） | NOT NULL, DEFAULT 'SCREW' |
| nickname | VARCHAR(64) | 昵称 | NULL |
| avatar_url | VARCHAR(256) | 头像 | NULL |
| created_at | DATETIME | 创建时间 | DEFAULT NOW() |
| updated_at | DATETIME | 更新时间 | ON UPDATE NOW() |

> **联合唯一索引**: `uk_openid_game_type` (openid, game_type)
>
> 同一 openid 在不同游戏类型下各有一条记录

### 4.2 user_progress（游戏进度表）

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | BIGINT | 主键 | PK, AUTO_INCREMENT |
| user_id | BIGINT | 用户ID | NOT NULL, FK→user.id |
| game_type | VARCHAR(32) | 游戏类型（枚举） | NOT NULL, DEFAULT 'SCREW' |
| level_num | INT | 当前关卡数 | NOT NULL, DEFAULT 1 |
| updated_at | DATETIME | 更新时间 | ON UPDATE NOW() |

> **联合唯一索引**: `uk_user_game_type` (user_id, game_type)
>
> 同一用户在同一游戏类型下只有一条进度记录

### 4.3 枚举定义

**SourceEnum（来源渠道）**

| 枚举值 | 说明 |
|--------|------|
| WECHAT | 微信小程序 |
| ALIPAY | 支付宝小程序（预留） |
| H5 | H5 网页（预留） |

**GameTypeEnum（游戏类型）**

| 枚举值 | 说明 |
|--------|------|
| SCREW | 螺丝游戏 |

---

## 五、安全设计

### 5.1 当前后端已具备的基础设施

| 项 | 现状 |
|------|------|
| Redis 支撑 | 已有 Redis 配置与 `RedisUtils` 工具类 |
| Token 常量 | 已有 Redis Key 前缀与 Token 过期时间常量 |
| 请求拦截 | 已有 `TokenInterceptor` 与 `WebMvcConfig` 拦截器注册 |
| 用户上下文 | 已有 `UserContext`，可从请求头读取 `Authorization` 并保存当前用户ID |
| 异常返回 | 已有统一返回结构 `Result` 与全局异常处理 |

### 5.2 本次方案要补全的能力

| 项 | 目标方案 |
|------|------|
| 认证方式 | Bearer Token（登录成功后下发，存 Redis，7天过期） |
| Token 格式 | UUID 随机生成 |
| 登录态存储 | Redis 中保存 `token -> userId` 映射 |
| 拦截路径 | `/api/**`（排除 `/api/game/login`） |
| 鉴权逻辑 | 拦截器中真实校验 token 是否存在、是否过期，并解析出 userId |
| 进度写入 | 只能改自己的进度（从 Token 解析出 userId） |
| 未登录响应 | token 缺失或失效时返回 401 |

### 5.3 当前注意事项

- `TokenInterceptor` 目前还是 mock 逻辑，用户ID 写死为 `1L`，后续要改成真实 Redis 校验。
- `WebMvcConfig` 当前放行路径不是 `/api/game/login`，编码时需要同步调整。

---

## 六、需要创建/修改的文件清单

### 后端（game-be）
| 文件 | 操作 | 说明 |
|------|------|------|
| `schema.sql` | 修改 | 添加建表 DDL |
| `constant/enums/SourceEnum.java` | 新建 | 来源渠道枚举 |
| `constant/enums/GameTypeEnum.java` | 新建 | 游戏类型枚举 |
| `entity/User.java` | 新建 | 用户实体 |
| `entity/UserProgress.java` | 新建 | 进度实体 |
| `mapper/UserMapper.java` | 新建 | 用户 Mapper |
| `mapper/UserProgressMapper.java` | 新建 | 进度 Mapper |
| `mapper/*.xml` | 新建 | MyBatis XML |
| `service/UserService.java` | 新建 | 用户服务 |
| `controller/GameController.java` | 新建 | 游戏接口 |
| `dto/LoginRequest.java` | 新建 | 登录请求 DTO |
| `dto/ProgressRequest.java` | 新建 | 进度请求 DTO |
| `TokenInterceptor.java` | 修改 | 启用真实 Token 校验 |

### 前端（screw-game）
| 文件 | 操作 | 说明 |
|------|------|------|
| `src/utils/api.ts` | 修改 | 本地存储改为内存变量 |
| `src/pages/index/index.vue` | 复核 | 确认流程无需改动 |

---

## 七、讨论记录

| 日期 | 内容 |
|------|------|
| 2026-05-13 | 确定方案：服务端为主，前端仅内存缓存。openid 做标识，unionid 预留。 |
| 2026-05-13 | 表名去 t_ 前缀；加 game_type 支持多游戏；加 source 枚举标识多端；主键改自增；去掉总游戏次数。 |
