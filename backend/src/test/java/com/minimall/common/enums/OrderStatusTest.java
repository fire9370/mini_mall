package com.minimall.common.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 订单状态流转规则测试
 */
class OrderStatusTest {

    @Test
    @DisplayName("待支付可支付")
    void unpaidCanPay() {
        assertTrue(OrderStatus.UNPAID.canPay());
    }

    @Test
    @DisplayName("待支付可取消")
    void unpaidCanCancel() {
        assertTrue(OrderStatus.UNPAID.canCancel());
    }

    @Test
    @DisplayName("待支付不可发货")
    void unpaidCannotShip() {
        assertFalse(OrderStatus.UNPAID.canShip());
    }

    @Test
    @DisplayName("已支付可发货")
    void paidCanShip() {
        assertTrue(OrderStatus.PAID.canShip());
    }

    @Test
    @DisplayName("已支付不可再支付")
    void paidCannotPay() {
        assertFalse(OrderStatus.PAID.canPay());
    }

    @Test
    @DisplayName("已发货可确认完成")
    void shippedCanComplete() {
        assertTrue(OrderStatus.SHIPPED.canComplete());
    }

    @Test
    @DisplayName("已取消不可支付")
    void cancelledCannotPay() {
        assertFalse(OrderStatus.CANCELLED.canPay());
    }
}
