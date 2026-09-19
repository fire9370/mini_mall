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

> 脚本会自动建库 `mini_mall`、8 张表，并写入默认管理员与示例商品。

### 2. 启动后端（:8080）

```bash
cd backend
# 需 JDK 17，且 application.yml 中数据库账号密码已改为本机实际值
mvn spring-boot:run
```

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
| 后台管理员 | `admin` | `admin123` |
| 前台用户 | 自行注册 | — |

## 文档

- 产品需求：[PRD.md](PRD.md)
- 开发指南：[CLAUDE.md](CLAUDE.md)
