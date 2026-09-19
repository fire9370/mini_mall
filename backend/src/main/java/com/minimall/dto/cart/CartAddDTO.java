package com.minimall.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 加入购物车请求
 */
@Data
public class CartAddDTO {

    @NotNull(message = "商品不能为空")
    private Long productId;

    /** 数量，缺省为 1 */
    private Integer quantity;
}
