package com.minimall.service;

import com.minimall.dto.auth.AdminLoginDTO;
import com.minimall.dto.auth.AdminLoginVO;

/**
 * 管理员服务
 */
public interface AdminService {

    AdminLoginVO login(AdminLoginDTO dto);
}
