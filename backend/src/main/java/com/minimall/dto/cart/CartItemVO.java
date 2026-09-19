package com.minimall.dto.cart;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车项返回对象（含商品快照信息）
 */
@Data
public class CartItemVO {

    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer stock;
    private Integer quantity;
    private Integer checked;
}
