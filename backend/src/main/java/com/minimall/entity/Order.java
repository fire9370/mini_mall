package com.minimall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minimall.common.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体（表名 orders 避开 MySQL 保留字 order）
 */
@Data
@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private OrderStatus status;

    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;

    private LocalDateTime payTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
