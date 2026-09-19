-- =============================================================
-- Mini Mall 数据库初始化脚本
-- 用途：建库、建表、写入默认管理员与示例数据
-- 编码：UTF-8（执行时请加 --default-character-set=utf8mb4）
-- =============================================================

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS mini_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE mini_mall;

-- 按依赖倒序删除，保证脚本可重复执行
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_item;
DROP TABLE IF EXISTS product_image;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS admin;
DROP TABLE IF EXISTS `user`;

-- ---------------------------------------------------------------
-- 前台用户表
-- ---------------------------------------------------------------
CREATE TABLE `user` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username`     VARCHAR(50)  NOT NULL COMMENT '用户名',
  `password`     VARCHAR(100) NOT NULL COMMENT '密码（BCrypt）',
  `nickname`     VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
  `email`        VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone`        VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `avatar`       VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `total_spent`  DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '累计消费金额（元）',
  `member_level` TINYINT      NOT NULL DEFAULT 1 COMMENT '会员等级 1普通 2黄金 3铂金 4钻石',
  `status`       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1正常 0禁用',
  `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='前台用户表';

-- ---------------------------------------------------------------
-- 后台管理员表（独立账号体系，与前台用户隔离）
-- ---------------------------------------------------------------
CREATE TABLE `admin` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username`   VARCHAR(50)  NOT NULL COMMENT '用户名',
  `password`   VARCHAR(100) NOT NULL COMMENT '密码（BCrypt）',
  `name`       VARCHAR(50)  DEFAULT NULL COMMENT '姓名',
  `status`     TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1正常 0禁用',
  `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台管理员表';

-- ---------------------------------------------------------------
-- 商品分类表（parent_id=0 表示一级分类）
-- ---------------------------------------------------------------
CREATE TABLE `category` (
  `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name`       VARCHAR(50) NOT NULL COMMENT '分类名称',
  `parent_id`  BIGINT      NOT NULL DEFAULT 0 COMMENT '父分类ID，0为一级分类',
  `sort`       INT         NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
  `status`     TINYINT     NOT NULL DEFAULT 1 COMMENT '状态 1启用 0停用',
  `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ---------------------------------------------------------------
-- 商品表
-- ---------------------------------------------------------------
CREATE TABLE `product` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `category_id` BIGINT        NOT NULL COMMENT '分类ID',
  `name`        VARCHAR(100)  NOT NULL COMMENT '商品名称',
  `subtitle`    VARCHAR(200)  DEFAULT NULL COMMENT '副标题',
  `main_image`  VARCHAR(255)  DEFAULT NULL COMMENT '主图URL',
  `detail`      TEXT          COMMENT '商品详情（富文本）',
  `price`       DECIMAL(10,2) NOT NULL COMMENT '价格（元）',
  `stock`       INT           NOT NULL DEFAULT 0 COMMENT '库存',
  `sales`       INT           NOT NULL DEFAULT 0 COMMENT '销量',
  `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态 1上架 0下架',
  `created_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ---------------------------------------------------------------
-- 商品轮播图表
-- ---------------------------------------------------------------
CREATE TABLE `product_image` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `product_id` BIGINT       NOT NULL COMMENT '商品ID',
  `image_url`  VARCHAR(255) NOT NULL COMMENT '图片URL',
  `sort`       INT          NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品轮播图表';

-- ---------------------------------------------------------------
-- 购物车表（同一用户同一商品只保留一条，数量累加）
-- ---------------------------------------------------------------
CREATE TABLE `cart_item` (
  `id`         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
  `user_id`    BIGINT   NOT NULL COMMENT '用户ID',
  `product_id` BIGINT   NOT NULL COMMENT '商品ID',
  `quantity`   INT      NOT NULL DEFAULT 1 COMMENT '数量',
  `checked`    TINYINT  NOT NULL DEFAULT 1 COMMENT '是否勾选 1勾选 0未勾选',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ---------------------------------------------------------------
-- 订单表（表名用 orders 避开 MySQL 保留字 order）
-- ---------------------------------------------------------------
CREATE TABLE `orders` (
  `id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no`         VARCHAR(32)   NOT NULL COMMENT '订单号',
  `user_id`          BIGINT        NOT NULL COMMENT '用户ID',
  `total_amount`     DECIMAL(10,2) NOT NULL COMMENT '订单总金额（元）',
  `status`           VARCHAR(20)   NOT NULL DEFAULT 'UNPAID' COMMENT '状态 UNPAID/PAID/SHIPPED/COMPLETED/CANCELLED',
  `receiver_name`    VARCHAR(50)   DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone`   VARCHAR(20)   DEFAULT NULL COMMENT '收货人电话',
  `receiver_address` VARCHAR(255)  DEFAULT NULL COMMENT '收货地址',
  `pay_time`         DATETIME      DEFAULT NULL COMMENT '支付时间',
  `created_at`       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ---------------------------------------------------------------
-- 订单明细表（商品信息做快照，避免商品改价影响历史订单）
-- ---------------------------------------------------------------
CREATE TABLE `order_item` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id`      BIGINT        NOT NULL COMMENT '订单ID',
  `product_id`    BIGINT        NOT NULL COMMENT '商品ID',
  `product_name`  VARCHAR(100)  NOT NULL COMMENT '商品名称（快照）',
  `product_image` VARCHAR(255)  DEFAULT NULL COMMENT '商品图片（快照）',
  `price`         DECIMAL(10,2) NOT NULL COMMENT '下单时单价（元）',
  `quantity`      INT           NOT NULL COMMENT '数量',
  `total_price`   DECIMAL(10,2) NOT NULL COMMENT '小计金额（元）',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- =============================================================
-- 默认数据（seed）
-- =============================================================

-- 注意：默认管理员不再在此脚本中写死，由后端启动时自动创建
-- （见 com.minimall.config.AdminInitializer，密码来自环境变量 ADMIN_INIT_PASSWORD 或自动生成）

-- 商品分类（1级 + 2级）
INSERT INTO `category` (`id`, `name`, `parent_id`, `sort`, `status`) VALUES
(1, '数码电器', 0, 1, 1),
(2, '手机',     1, 1, 1),
(3, '笔记本电脑', 1, 2, 1),
(4, '服饰鞋包', 0, 2, 1),
(5, '男装',     4, 1, 1),
(6, '女装',     4, 2, 1);

-- 示例商品
INSERT INTO `product` (`id`, `category_id`, `name`, `subtitle`, `main_image`, `detail`, `price`, `stock`, `sales`, `status`) VALUES
(1, 2, '小米14 手机',        '骁龙8Gen3 旗舰，徕卡光学', '', '旗舰配置，拍照出色。', 3999.00, 100, 0, 1),
(2, 2, 'iPhone 15 Pro',     '钛金属边框，A17 Pro',      '', '性能强劲，影像升级。', 7999.00, 50,  0, 1),
(3, 3, 'MacBook Air M3',    '轻薄办公，续航持久',       '', 'M3 芯片，静音无风扇。', 8999.00, 30,  0, 1),
(4, 3, '联想小新Pro 16',     '高性价比大屏本',           '', '16 英寸 2.5K 高刷屏。', 5999.00, 60,  0, 1),
(5, 5, '基础圆领T恤',        '纯棉透气，多色可选',       '', '纯棉面料，舒适百搭。', 79.00,   500, 0, 1),
(6, 6, '碎花雪纺连衣裙',     '夏季新款，显瘦',           '', '雪纺材质，清凉飘逸。', 199.00,  200, 0, 1),
(7, 1, '索尼降噪耳机',       'WH-1000XM5 头戴式',       '', '主动降噪，续航 30 小时。', 2299.00, 80, 0, 1);
