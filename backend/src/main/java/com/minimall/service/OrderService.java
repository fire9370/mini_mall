package com.minimall.service;

import com.minimall.common.PageResult;
import com.minimall.dto.order.OrderCreateDTO;
import com.minimall.dto.order.OrderVO;

import java.util.List;

/**
 * 订单服务
 */
public interface OrderService {

    Long create(Long userId, OrderCreateDTO dto);

    List<OrderVO> list(Long userId);

    OrderVO detail(Long userId, Long id);

    void pay(Long userId, Long id);

    void cancel(Long userId, Long id);

    PageResult<OrderVO> pageAdmin(int page, int size, String status);

    OrderVO detailAdmin(Long id);

    void ship(Long id);
}
