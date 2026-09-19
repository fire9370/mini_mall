package com.minimall.controller;

import com.minimall.common.R;
import com.minimall.dto.order.OrderCreateDTO;
import com.minimall.dto.order.OrderVO;
import com.minimall.service.OrderService;
import com.minimall.util.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台订单接口
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public R<Long> create(@Valid @RequestBody OrderCreateDTO dto) {
        return R.ok(orderService.create(UserContext.getUserId(), dto));
    }

    @GetMapping
    public R<List<OrderVO>> list() {
        return R.ok(orderService.list(UserContext.getUserId()));
    }

    @GetMapping("/{id}")
    public R<OrderVO> detail(@PathVariable Long id) {
        return R.ok(orderService.detail(UserContext.getUserId(), id));
    }

    @PostMapping("/{id}/pay")
    public R<Void> pay(@PathVariable Long id) {
        orderService.pay(UserContext.getUserId(), id);
        return R.ok();
    }

    @PostMapping("/{id}/cancel")
    public R<Void> cancel(@PathVariable Long id) {
        orderService.cancel(UserContext.getUserId(), id);
        return R.ok();
    }
}
