package com.minimall.dto.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户信息返回
 */
@Data
public class UserVO {

    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer memberLevel;
    private String memberLevelLabel;
    private BigDecimal totalSpent;
}
