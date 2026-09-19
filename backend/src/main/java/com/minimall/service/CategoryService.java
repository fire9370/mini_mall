package com.minimall.service;

import com.minimall.dto.product.CategoryDTO;
import com.minimall.entity.Category;

import java.util.List;

/**
 * 分类服务
 */
public interface CategoryService {

    /** 前台：启用分类列表 */
    List<Category> listEnabled();

    /** 后台：全部分类列表 */
    List<Category> listAll();

    void create(CategoryDTO dto);

    void update(Long id, CategoryDTO dto);

    void delete(Long id);
}
