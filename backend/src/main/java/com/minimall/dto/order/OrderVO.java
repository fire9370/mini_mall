package com.minimall.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单返回
 */
@Data
public class OrderVO {

    private Long id;
    private String orderNo;
    private BigDecimal totalAmount;
    private String status;
    private String statusLabel;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private LocalDateTime payTime;
    private LocalDateTime createdAt;
    private List<OrderItemVO> items;
}
