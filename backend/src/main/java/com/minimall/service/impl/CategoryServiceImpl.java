package com.minimall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minimall.common.BizException;
import com.minimall.dto.product.CategoryDTO;
import com.minimall.entity.Category;
import com.minimall.entity.Product;
import com.minimall.mapper.CategoryMapper;
import com.minimall.mapper.ProductMapper;
import com.minimall.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @Override
    public List<Category> listEnabled() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId));
    }

    @Override
    public List<Category> listAll() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId));
    }

    @Override
    public void create(CategoryDTO dto) {
        Category c = new Category();
        c.setName(dto.getName());
        c.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        c.setSort(dto.getSort() != null ? dto.getSort() : 0);
        c.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        categoryMapper.insert(c);
    }

    @Override
    public void update(Long id, CategoryDTO dto) {
        Category c = categoryMapper.selectById(id);
        if (c == null) {
            throw new BizException("分类不存在");
        }
        c.setName(dto.getName());
        if (dto.getParentId() != null) {
            c.setParentId(dto.getParentId());
        }
        if (dto.getSort() != null) {
            c.setSort(dto.getSort());
        }
        if (dto.getStatus() != null) {
            c.setStatus(dto.getStatus());
        }
        categoryMapper.updateById(c);
    }

    @Override
    public void delete(Long id) {
        Long childCount = categoryMapper.selectCount(new LambdaQueryWrapper<Category>().eq(Category::getParentId, id));
        if (childCount != null && childCount > 0) {
            throw new BizException("存在子分类，无法删除");
        }
        Long productCount = productMapper.selectCount(new LambdaQueryWrapper<Product>().eq(Product::getCategoryId, id));
        if (productCount != null && productCount > 0) {
            throw new BizException("分类下存在商品，无法删除");
        }
        categoryMapper.deleteById(id);
    }
}
