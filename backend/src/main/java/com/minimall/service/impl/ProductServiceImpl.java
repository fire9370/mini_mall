package com.minimall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimall.common.BizException;
import com.minimall.common.PageResult;
import com.minimall.dto.product.ProductDTO;
import com.minimall.dto.product.ProductVO;
import com.minimall.entity.Category;
import com.minimall.entity.Product;
import com.minimall.entity.ProductImage;
import com.minimall.mapper.CategoryMapper;
import com.minimall.mapper.ProductImageMapper;
import com.minimall.mapper.ProductMapper;
import com.minimall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品服务实现
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public PageResult<ProductVO> page(int pageNum, int pageSize, String keyword, Long categoryId) {
        return doPage(pageNum, pageSize, keyword, categoryId, true);
    }

    @Override
    public PageResult<ProductVO> pageAdmin(int pageNum, int pageSize, String keyword, Long categoryId) {
        return doPage(pageNum, pageSize, keyword, categoryId, false);
    }

    private PageResult<ProductVO> doPage(int pageNum, int pageSize, String keyword, Long categoryId, boolean onlyOnSale) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (onlyOnSale) {
            wrapper.eq(Product::getStatus, 1);
        }
        if (categoryId != null) {
            // 若为父分类，则连同其子分类一起查询
            Set<Long> ids = new HashSet<>();
            ids.add(categoryId);
            List<Category> children = categoryMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getParentId, categoryId));
            for (Category child : children) {
                ids.add(child.getId());
            }
            wrapper.in(Product::getCategoryId, ids);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Product::getName, keyword).or().like(Product::getSubtitle, keyword));
        }
        wrapper.orderByDesc(Product::getSales).orderByDesc(Product::getId);

        Page<Product> result = productMapper.selectPage(page, wrapper);
        Map<Long, String> categoryNames = categoryNameMap(
                result.getRecords().stream().map(Product::getCategoryId).collect(Collectors.toSet()));
        List<ProductVO> vos = result.getRecords().stream()
                .map(p -> toVO(p, categoryNames.get(p.getCategoryId()), null))
                .collect(Collectors.toList());
        return new PageResult<>(result.getTotal(), vos);
    }

    @Override
    public ProductVO detail(Long id) {
        Product p = productMapper.selectById(id);
        if (p == null || p.getStatus() == null || p.getStatus() != 1) {
            throw new BizException("商品不存在或已下架");
        }
        List<String> images = productImageMapper.selectList(new LambdaQueryWrapper<ProductImage>()
                        .eq(ProductImage::getProductId, id)
                        .orderByAsc(ProductImage::getSort))
                .stream().map(ProductImage::getImageUrl).collect(Collectors.toList());
        Category c = categoryMapper.selectById(p.getCategoryId());
        return toVO(p, c != null ? c.getName() : null, images);
    }

    @Override
    public void create(ProductDTO dto) {
        Product p = new Product();
        applyDto(p, dto);
        p.setSales(0);
        productMapper.insert(p);
        saveImages(p.getId(), dto.getImages());
    }

    @Override
    public void update(Long id, ProductDTO dto) {
        Product p = productMapper.selectById(id);
        if (p == null) {
            throw new BizException("商品不存在");
        }
        applyDto(p, dto);
        productMapper.updateById(p);
        // 先删后插，重新保存轮播图
        productImageMapper.delete(new LambdaQueryWrapper<ProductImage>().eq(ProductImage::getProductId, id));
        saveImages(id, dto.getImages());
    }

    @Override
    public void delete(Long id) {
        productMapper.deleteById(id);
        productImageMapper.delete(new LambdaQueryWrapper<ProductImage>().eq(ProductImage::getProductId, id));
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Product p = productMapper.selectById(id);
        if (p == null) {
            throw new BizException("商品不存在");
        }
        p.setStatus(status);
        productMapper.updateById(p);
    }

    private void applyDto(Product p, ProductDTO dto) {
        p.setCategoryId(dto.getCategoryId());
        p.setName(dto.getName());
        p.setSubtitle(dto.getSubtitle());
        p.setMainImage(dto.getMainImage());
        p.setDetail(dto.getDetail());
        p.setPrice(dto.getPrice());
        p.setStock(dto.getStock());
        p.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
    }

    private void saveImages(Long productId, List<String> images) {
        if (images == null || images.isEmpty()) {
            return;
        }
        int sort = 0;
        for (String url : images) {
            ProductImage img = new ProductImage();
            img.setProductId(productId);
            img.setImageUrl(url);
            img.setSort(sort++);
            productImageMapper.insert(img);
        }
    }

    private Map<Long, String> categoryNameMap(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Category> categories = categoryMapper.selectBatchIds(ids);
        return categories.stream().collect(Collectors.toMap(Category::getId, Category::getName));
    }

    private ProductVO toVO(Product p, String categoryName, List<String> images) {
        ProductVO vo = new ProductVO();
        vo.setId(p.getId());
        vo.setCategoryId(p.getCategoryId());
        vo.setCategoryName(categoryName);
        vo.setName(p.getName());
        vo.setSubtitle(p.getSubtitle());
        vo.setMainImage(p.getMainImage());
        vo.setDetail(p.getDetail());
        vo.setPrice(p.getPrice());
        vo.setStock(p.getStock());
        vo.setSales(p.getSales());
        vo.setStatus(p.getStatus());
        vo.setImages(images);
        vo.setCreatedAt(p.getCreatedAt());
        return vo;
    }
}
