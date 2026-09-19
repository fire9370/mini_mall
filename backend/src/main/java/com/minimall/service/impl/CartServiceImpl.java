package com.minimall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minimall.common.BizException;
import com.minimall.dto.cart.CartAddDTO;
import com.minimall.dto.cart.CartItemVO;
import com.minimall.entity.CartItem;
import com.minimall.entity.Product;
import com.minimall.mapper.CartItemMapper;
import com.minimall.mapper.ProductMapper;
import com.minimall.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 购物车服务实现
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;

    @Override
    public List<CartItemVO> list(Long userId) {
        List<CartItem> items = cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .orderByDesc(CartItem::getId));
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> productIds = items.stream().map(CartItem::getProductId).collect(Collectors.toList());
        Map<Long, Product> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        return items.stream().map(item -> {
            CartItemVO vo = new CartItemVO();
            vo.setId(item.getId());
            vo.setProductId(item.getProductId());
            vo.setQuantity(item.getQuantity());
            vo.setChecked(item.getChecked());
            Product p = productMap.get(item.getProductId());
            if (p != null) {
                vo.setProductName(p.getName());
                vo.setProductImage(p.getMainImage());
                vo.setPrice(p.getPrice());
                vo.setStock(p.getStock());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void add(Long userId, CartAddDTO dto) {
        Product p = productMapper.selectById(dto.getProductId());
        if (p == null || p.getStatus() == null || p.getStatus() != 1) {
            throw new BizException("商品不存在或已下架");
        }
        int qty = dto.getQuantity() != null && dto.getQuantity() > 0 ? dto.getQuantity() : 1;
        CartItem existing = cartItemMapper.selectOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, dto.getProductId()));
        if (existing != null) {
            // 同商品合并数量
            int newQty = existing.getQuantity() + qty;
            if (newQty > p.getStock()) {
                throw new BizException("库存不足");
            }
            existing.setQuantity(newQty);
            existing.setChecked(1);
            cartItemMapper.updateById(existing);
        } else {
            if (qty > p.getStock()) {
                throw new BizException("库存不足");
            }
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setProductId(dto.getProductId());
            item.setQuantity(qty);
            item.setChecked(1);
            cartItemMapper.insert(item);
        }
    }

    @Override
    public void updateQuantity(Long userId, Long id, Integer quantity) {
        CartItem item = getOwned(userId, id);
        Product p = productMapper.selectById(item.getProductId());
        if (p != null && quantity > p.getStock()) {
            throw new BizException("库存不足");
        }
        item.setQuantity(quantity);
        cartItemMapper.updateById(item);
    }

    @Override
    public void delete(Long userId, Long id) {
        cartItemMapper.delete(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getId, id)
                .eq(CartItem::getUserId, userId));
    }

    @Override
    public void toggleChecked(Long userId, Long id, Integer checked) {
        CartItem item = getOwned(userId, id);
        item.setChecked(checked != null && checked == 1 ? 1 : 0);
        cartItemMapper.updateById(item);
    }

    private CartItem getOwned(Long userId, Long id) {
        CartItem item = cartItemMapper.selectById(id);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BizException("购物车项不存在");
        }
        return item;
    }
}
