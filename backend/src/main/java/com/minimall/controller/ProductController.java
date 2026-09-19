package com.minimall.controller;

import com.minimall.common.PageResult;
import com.minimall.common.R;
import com.minimall.dto.product.ProductVO;
import com.minimall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台商品接口
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public R<PageResult<ProductVO>> page(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Long categoryId) {
        return R.ok(productService.page(page, size, keyword, categoryId));
    }

    @GetMapping("/{id}")
    public R<ProductVO> detail(@PathVariable Long id) {
        return R.ok(productService.detail(id));
    }
}
