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

    /** 仅待支付可支付 */
    public boolean canPay() {
        return this == UNPAID;
    }

    /** 仅待支付可取消 */
    public boolean canCancel() {
        return this == UNPAID;
    }

    /** 仅已支付可发货 */
    public boolean canShip() {
        return this == PAID;
    }

    /** 仅已发货可确认完成 */
    public boolean canComplete() {
        return this == SHIPPED;
    }
}
