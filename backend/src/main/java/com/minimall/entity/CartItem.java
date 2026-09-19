package com.minimall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 购物车项实体
 */
@Data
@TableName("cart_item")
public class CartItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long productId;
    private Integer quantity;

    /** 是否勾选 1勾选 0未勾选 */
    private Integer checked;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
