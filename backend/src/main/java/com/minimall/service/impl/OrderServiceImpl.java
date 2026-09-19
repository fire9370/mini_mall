package com.minimall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimall.common.BizException;
import com.minimall.common.PageResult;
import com.minimall.common.enums.MemberLevel;
import com.minimall.common.enums.OrderStatus;
import com.minimall.dto.order.OrderCreateDTO;
import com.minimall.dto.order.OrderItemVO;
import com.minimall.dto.order.OrderVO;
import com.minimall.entity.CartItem;
import com.minimall.entity.Order;
import com.minimall.entity.OrderItem;
import com.minimall.entity.Product;
import com.minimall.entity.User;
import com.minimall.mapper.CartItemMapper;
import com.minimall.mapper.OrderItemMapper;
import com.minimall.mapper.OrderMapper;
import com.minimall.mapper.ProductMapper;
import com.minimall.mapper.UserMapper;
import com.minimall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Long create(Long userId, OrderCreateDTO dto) {
        // 1. 加载待结算的购物车项（仅本人）
        List<CartItem> cartItems = cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .in(CartItem::getId, dto.getCartItemIds()));
        if (cartItems.isEmpty()) {
            throw new BizException("请选择要结算的商品");
        }
        // 2. 校验库存并计算总额、构建明细
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : cartItems) {
            Product p = productMapper.selectById(ci.getProductId());
            if (p == null || p.getStatus() == null || p.getStatus() != 1) {
                throw new BizException("商品不存在或已下架");
            }
            if (ci.getQuantity() > p.getStock()) {
                throw new BizException("商品「" + p.getName() + "」库存不足");
            }
            BigDecimal itemTotal = p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            total = total.add(itemTotal);
            OrderItem oi = new OrderItem();
            oi.setProductId(p.getId());
            oi.setProductName(p.getName());
            oi.setProductImage(p.getMainImage());
            oi.setPrice(p.getPrice());
            oi.setQuantity(ci.getQuantity());
            oi.setTotalPrice(itemTotal);
            orderItems.add(oi);
        }
        // 3. 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.UNPAID);
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setReceiverAddress(dto.getReceiverAddress());
        orderMapper.insert(order);
        // 4. 保存明细
        for (OrderItem oi : orderItems) {
            oi.setOrderId(order.getId());
            orderItemMapper.insert(oi);
        }
        // 5. 扣减库存（带条件更新兜底防超卖）
        for (OrderItem oi : orderItems) {
            int rows = productMapper.update(null, new LambdaUpdateWrapper<Product>()
                    .eq(Product::getId, oi.getProductId())
                    .ge(Product::getStock, oi.getQuantity())
                    .setSql("stock = stock - " + oi.getQuantity() + ", sales = sales + " + oi.getQuantity()));
            if (rows == 0) {
                throw new BizException("商品「" + oi.getProductName() + "」库存不足");
            }
        }
        // 6. 删除已结算的购物车项
        cartItemMapper.delete(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .in(CartItem::getId, dto.getCartItemIds()));
        return order.getId();
    }

    @Override
    public List<OrderVO> list(Long userId) {
        List<Order> orders = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getId));
        return orders.stream().map(o -> toVO(o, null)).collect(Collectors.toList());
    }

    @Override
    public OrderVO detail(Long userId, Long id) {
        Order order = getOwned(userId, id);
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id));
        return toVO(order, items);
    }

    @Override
    @Transactional
    public void pay(Long userId, Long id) {
        Order order = getOwned(userId, id);
        if (!order.getStatus().canPay()) {
            throw new BizException("订单当前状态不可支付");
        }
        order.setStatus(OrderStatus.PAID);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
        // 累计消费 + 会员升级（只升不降）
        User user = userMapper.selectById(userId);
        BigDecimal current = user.getTotalSpent() != null ? user.getTotalSpent() : BigDecimal.ZERO;
        user.setTotalSpent(current.add(order.getTotalAmount()));
        user.setMemberLevel(MemberLevel.levelForTotalSpent(user.getTotalSpent()).getCode());
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void cancel(Long userId, Long id) {
        Order order = getOwned(userId, id);
        if (!order.getStatus().canCancel()) {
            throw new BizException("订单当前状态不可取消");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderMapper.updateById(order);
        // 回滚库存
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id));
        for (OrderItem oi : items) {
            productMapper.update(null, new LambdaUpdateWrapper<Product>()
                    .eq(Product::getId, oi.getProductId())
                    .setSql("stock = stock + " + oi.getQuantity() + ", sales = sales - " + oi.getQuantity()));
        }
    }

    @Override
    public PageResult<OrderVO> pageAdmin(int page, int size, String status) {
        Page<Order> p = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getId);
        Page<Order> result = orderMapper.selectPage(p, wrapper);
        List<OrderVO> vos = result.getRecords().stream().map(o -> toVO(o, null)).collect(Collectors.toList());
        return new PageResult<>(result.getTotal(), vos);
    }

    @Override
    public OrderVO detailAdmin(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id));
        return toVO(order, items);
    }

    @Override
    public void ship(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        if (!order.getStatus().canShip()) {
            throw new BizException("订单当前状态不可发货");
        }
        order.setStatus(OrderStatus.SHIPPED);
        orderMapper.updateById(order);
    }

    private Order getOwned(Long userId, Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BizException("订单不存在");
        }
        return order;
    }

    private String generateOrderNo() {
        return DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now())
                + String.format("%03d", ThreadLocalRandom.current().nextInt(1000));
    }

    private OrderVO toVO(Order o, List<OrderItem> items) {
        OrderVO vo = new OrderVO();
        vo.setId(o.getId());
        vo.setOrderNo(o.getOrderNo());
        vo.setTotalAmount(o.getTotalAmount());
        vo.setStatus(o.getStatus().name());
        vo.setStatusLabel(o.getStatus().getLabel());
        vo.setReceiverName(o.getReceiverName());
        vo.setReceiverPhone(o.getReceiverPhone());
        vo.setReceiverAddress(o.getReceiverAddress());
        vo.setPayTime(o.getPayTime());
        vo.setCreatedAt(o.getCreatedAt());
        if (items != null) {
            vo.setItems(items.stream().map(i -> {
                OrderItemVO iv = new OrderItemVO();
                iv.setProductId(i.getProductId());
                iv.setProductName(i.getProductName());
                iv.setProductImage(i.getProductImage());
                iv.setPrice(i.getPrice());
                iv.setQuantity(i.getQuantity());
                iv.setTotalPrice(i.getTotalPrice());
                return iv;
            }).collect(Collectors.toList()));
        }
        return vo;
    }
}
