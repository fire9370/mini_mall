package com.minimall.dto.product;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品返回对象
 */
@Data
public class ProductVO {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String subtitle;
    private String mainImage;
    private String detail;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private Integer status;

    /** 轮播图 URL 列表 */
    private List<String> images;

    private LocalDateTime createdAt;
}
