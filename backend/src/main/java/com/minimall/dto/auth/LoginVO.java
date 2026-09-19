package com.minimall.dto.auth;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户登录返回
 */
@Data
public class LoginVO {

    private String token;
    private Long id;
    private String username;
    private String nickname;
    private Integer memberLevel;
    private String memberLevelLabel;
    private BigDecimal totalSpent;
}
