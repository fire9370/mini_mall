# CLAUDE.md

Mini Mall 微型电商项目的开发指南。本文件供 Claude Code 在后续会话中快速理解项目与约定。

## 项目概述
Mini Mall 是一个学习/演示用的全栈微商城，跑通「商品浏览 → 注册登录 → 加购 → 下单 → 模拟支付 → 后台管理」完整闭环。单仓库（monorepo）：`backend/`（后端）+ `frontend/`（前端）。

## 技术栈
- **后端**：SpringBoot 3.x + JDK 17 + Maven + MyBatis-Plus（`mybatis-plus-spring-boot3-starter`）+ MySQL 8 + JWT(jjwt 0.12.x) + `spring-security-crypto`(仅 BCrypt，不引入完整 Spring Security)
- **前端**：Vue3 + TypeScript + Vite + vue-router 4 + Pinia 2 + axios + Element Plus 2.x
- **数据库**：MySQL 8，InnoDB，`utf8mb4`

## 环境要求
- JDK 17（SpringBoot 3.x 必须；本机默认是 JDK 8，需另装并设 `JAVA_HOME`）
- Maven 3.9+、Node 18+/npm、MySQL 8

## 目录结构
```
backend/            SpringBoot 后端
  src/main/java/com/minimall/
    config/         CORS、MyBatis-Plus 分页插件、Web(静态资源+拦截器注册)
    common/         R 统一返回、PageResult、BizException、GlobalExceptionHandler、枚举(MemberLevel/OrderStatus)
    entity/         实体(User/Admin/Category/Product/ProductImage/CartItem/Order/OrderItem)
    mapper/         MyBatis-Plus Mapper(BaseMapper)
    dto/            请求/响应对象
    service/        业务接口 + impl
    controller/     前台控制器
    controller/admin/ 后台控制器
    interceptor/    用户 JWT 拦截器 + 管理员 JWT 拦截器
    util/           JwtUtil、ThreadLocal 上下文
  src/main/resources/
    application.yml 数据源、端口、JWT 密钥、上传路径
    sql/init.sql    建库建表 + seed(默认管理员 admin/admin123、示例数据)
frontend/           Vue3 前端
  src/api/          axios 实例 + 模块接口
  src/router/       前台 + /admin 路由 + 守卫
  src/stores/       Pinia(user/admin/cart)
  src/views/        前台页面
  src/admin/        后台页面 + AdminLayout
```

## 本地开发
```bash
# 1. 初始化数据库
mysql -u root -p < backend/src/main/resources/sql/init.sql

# 2. 启动后端（:8080）
cd backend && mvn spring-boot:run

# 3. 启动前端（:5173，dev proxy 转发 /api 与 /uploads → :8080）
cd frontend && npm install && npm run dev
```
- 默认管理员：`admin` / `admin123`（seed 在 init.sql）。
- Vite dev proxy 已在 `frontend/vite.config.ts` 配置，开发期无需处理跨域（后端仍配了 CORS 兜底）。

## 核心约定
- **统一返回体** `R<T>{ code, message, data }`，`code=0` 表示成功；所有接口一律返回 `R`。
- **分层**：`controller → service → mapper`，DTO 与实体分离，不直接暴露实体。
- **鉴权**：两套独立 JWT——用户拦截 `/api/**`（放行 `/api/auth/**`、`/api/products/**`、`/api/categories`），管理员拦截 `/api/admin/**`（放行 `/api/admin/login`）。密钥在 `application.yml`。
- **两个 Token**：用户 `token` 与管理员 `admin_token` 分开存 localStorage，前端两套 axios 实例分离。
- **会员等级**：`1 普通 / 2 黄金 / 3 铂金 / 4 钻石`，默认 1；支付成功时按 `total_spent`（累计消费）阈值 10000/50000/100000 自动升级，只升不降。
- **订单状态**：`UNPAID → PAID → SHIPPED → COMPLETED`，`UNPAID → CANCELLED`。
- **金额**：`DECIMAL(10,2)`，单位元；主键 `BIGINT` 自增。
- 下单/支付/取消/升级等涉及金额与库存的操作必须包裹 `@Transactional`。

## 关键坑（务必遵守）
- MyBatis-Plus 必须用 `mybatis-plus-spring-boot3-starter`（jakarta 命名空间），不能用旧 `-boot-starter`。
- jjwt 0.12.x 构建/解析 API 与旧版不同：`Jwts.builder().signWith(key)` / `parser().verifyWith(key)`。
- MySQL8 驱动坐标是 `com.mysql:mysql-connector-j`（非旧 `mysql-connector-java`）。
- 下单扣库存用带 `stock >= n` 条件 UPDATE 兜底，防止超卖。
- 订单明细对商品名称/图片/单价做快照，避免商品改价影响历史数据。

## 相关文档
- 产品需求：`PRD.md`
- 架构设计：`plans/ethereal-discovering-toast.md`（或迁移到 `docs/architecture.md` 后以后者为准）