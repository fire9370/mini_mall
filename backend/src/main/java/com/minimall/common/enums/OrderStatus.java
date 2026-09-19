package com.minimall.common.enums;

import lombok.Getter;

/**
 * 订单状态
 */
@Getter
public enum OrderStatus {

    UNPAID("待支付"),
    PAID("已支付"),
    SHIPPED("已发货"),
    COMPLETED("已完成"),
    CANCELLED("已取消");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }
}
