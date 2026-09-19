package com.minimall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;
    private String name;
    private String subtitle;
    private String mainImage;
    private String detail;

    /** 价格（元） */
    private BigDecimal price;

    private Integer stock;
    private Integer sales;

    /** 状态 1上架 0下架 */
    private Integer status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
