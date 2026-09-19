package com.minimall.dto.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细返回
 */
@Data
public class OrderItemVO {

    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;
}
