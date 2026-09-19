package com.minimall.service;

import com.minimall.dto.cart.CartAddDTO;
import com.minimall.dto.cart.CartItemVO;

import java.util.List;

/**
 * 购物车服务
 */
public interface CartService {

    List<CartItemVO> list(Long userId);

    void add(Long userId, CartAddDTO dto);

    void updateQuantity(Long userId, Long id, Integer quantity);

    void delete(Long userId, Long id);

    void toggleChecked(Long userId, Long id, Integer checked);
}
