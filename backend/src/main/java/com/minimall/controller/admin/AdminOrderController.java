package com.minimall.controller.admin;

import com.minimall.common.PageResult;
import com.minimall.common.R;
import com.minimall.dto.order.OrderVO;
import com.minimall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台订单管理接口
 */
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public R<PageResult<OrderVO>> page(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(required = false) String status) {
        return R.ok(orderService.pageAdmin(page, size, status));
    }

    @GetMapping("/{id}")
    public R<OrderVO> detail(@PathVariable Long id) {
        return R.ok(orderService.detailAdmin(id));
    }

    @PutMapping("/{id}/ship")
    public R<Void> ship(@PathVariable Long id) {
        orderService.ship(id);
        return R.ok();
    }
}
