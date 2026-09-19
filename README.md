# Mini Mall 微商城

一个学习/演示用的全栈微商城项目，跑通「商品浏览 → 注册登录 → 加购 → 下单 → 模拟支付 → 后台管理」完整闭环。

## 功能

- **商品浏览**：列表、详情、关键词搜索、分类筛选（支持二级分类）
- **用户**：注册登录（JWT 无状态）、个人中心
- **购物车**：加购、改数量、勾选、删除
- **订单**：下单（防超卖扣库存）、模拟支付、取消（回滚库存）、后台发货
- **会员等级**：普通 / 黄金 / 铂金 / 钻石，按累计消费额自动升级（10000 / 50000 / 100000 元），只升不降
- **后台管理**：分类 CRUD、商品 CRUD + 上下架 + 图片上传、订单管理

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | SpringBoot 3.3.x · JDK 17 · MyBatis-Plus · MySQL 8 · JWT(jjwt) |
| 前端 | Vue3 · TypeScript(strict) · Vite · Pinia · Vue Router · Element Plus |

## 目录结构

```
mini_mall/
├── backend/    SpringBoot 后端（com.minimall）
├── frontend/   Vue3 前端
├── PRD.md      产品需求文档
├── CLAUDE.md   开发指南
└── README.md
```

## 快速开始

### 1. 初始化数据库

```bash
mysql -u root -p < backend/src/main/resources/sql/init.sql
```

> 脚本会自动建库 `mini_mall`、8 张表，并写入示例商品。默认管理员改为后端首次启动时自动创建。

### 2. 配置环境变量并启动后端（:8080）

密钥与数据库口令不再提交到仓库，启动前需通过环境变量注入（参考 [backend/.env.example](backend/.env.example)）：

```bash
cd backend
# Git Bash：
export JWT_SECRET="$(openssl rand -base64 48)"   # JWT 密钥，至少 32 字节，务必随机生成
export DB_USERNAME=root
export DB_PASSWORD=123456                          # 你本机 MySQL 的实际密码
export ADMIN_INIT_PASSWORD="你的管理员初始密码"     # 可选；留空则启动时生成随机密码并打印到日志
mvn spring-boot:run
```

> PowerShell 下用 `$env:JWT_SECRET="..."` 等语法，或在 IDE 运行配置里添加同名环境变量。

### 3. 启动前端（:5173）

```bash
cd frontend
npm install
npm run dev
```

打开 <http://localhost:5173> 即可访问前台，后台入口在 <http://localhost:5173/admin/login>。

> 开发期 Vite 已配置代理：`/api` 与 `/uploads` 转发到 `http://localhost:8080`，无需额外处理跨域。

## 默认账号

| 角色 | 用户名 | 密码 |
|---|---|---|
| 后台管理员 | `admin` | 首次启动自动创建：来自 `ADMIN_INIT_PASSWORD`，或自动生成并打印到后端日志 |
| 前台用户 | 自行注册 | — |

## 文档

- 产品需求：[PRD.md](PRD.md)
- 开发指南：[CLAUDE.md](CLAUDE.md)
