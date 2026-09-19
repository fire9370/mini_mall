package com.minimall.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品创建/更新请求
 */
@Data
public class ProductDTO {

    @NotNull(message = "请选择分类")
    private Long categoryId;

    @NotBlank(message = "商品名称不能为空")
    private String name;

    private String subtitle;

    private String mainImage;

    private String detail;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.0", message = "价格不能为负数")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    private Integer status;

    /** 轮播图 URL 列表 */
    private List<String> images;
}
