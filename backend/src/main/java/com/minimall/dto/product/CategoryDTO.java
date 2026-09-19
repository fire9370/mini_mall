package com.minimall.dto.product;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 分类创建/更新请求
 */
@Data
public class CategoryDTO {

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private Long parentId;

    private Integer sort;

    private Integer status;
}
