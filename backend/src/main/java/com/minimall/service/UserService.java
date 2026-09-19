package com.minimall.service;

import com.minimall.dto.auth.LoginDTO;
import com.minimall.dto.auth.LoginVO;
import com.minimall.dto.auth.RegisterDTO;
import com.minimall.entity.User;

/**
 * 用户服务
 */
public interface UserService {

    void register(RegisterDTO dto);

    LoginVO login(LoginDTO dto);

    User getById(Long id);
}
