package com.minimall.controller;

import com.minimall.common.R;
import com.minimall.dto.cart.CartAddDTO;
import com.minimall.dto.cart.CartItemVO;
import com.minimall.dto.cart.CartUpdateDTO;
import com.minimall.service.CartService;
import com.minimall.util.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 购物车接口
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public R<List<CartItemVO>> list() {
        return R.ok(cartService.list(UserContext.getUserId()));
    }

    @PostMapping
    public R<Void> add(@Valid @RequestBody CartAddDTO dto) {
        cartService.add(UserContext.getUserId(), dto);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody CartUpdateDTO dto) {
        cartService.updateQuantity(UserContext.getUserId(), id, dto.getQuantity());
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        cartService.delete(UserContext.getUserId(), id);
        return R.ok();
    }

    @PutMapping("/{id}/checked")
    public R<Void> toggleChecked(@PathVariable Long id, @RequestParam Integer checked) {
        cartService.toggleChecked(UserContext.getUserId(), id, checked);
        return R.ok();
    }
}
