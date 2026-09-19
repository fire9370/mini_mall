package com.minimall.service;

import com.minimall.common.PageResult;
import com.minimall.dto.product.ProductDTO;
import com.minimall.dto.product.ProductVO;

/**
 * 商品服务
 */
public interface ProductService {

    /** 前台分页：仅上架商品，支持关键词与分类筛选 */
    PageResult<ProductVO> page(int pageNum, int pageSize, String keyword, Long categoryId);

    /** 商品详情（仅上架） */
    ProductVO detail(Long id);

    /** 后台分页：全部状态 */
    PageResult<ProductVO> pageAdmin(int pageNum, int pageSize, String keyword, Long categoryId);

    void create(ProductDTO dto);

    void update(Long id, ProductDTO dto);

    void delete(Long id);

    /** 上下架 */
    void updateStatus(Long id, Integer status);
}
