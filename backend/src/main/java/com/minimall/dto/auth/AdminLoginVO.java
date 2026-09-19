package com.minimall.dto.auth;

import lombok.Data;

/**
 * 管理员登录返回
 */
@Data
public class AdminLoginVO {

    private String token;
    private Long id;
    private String username;
    private String name;
}
