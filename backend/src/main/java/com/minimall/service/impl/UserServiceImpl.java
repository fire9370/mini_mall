package com.minimall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minimall.common.BizException;
import com.minimall.common.enums.MemberLevel;
import com.minimall.dto.auth.LoginDTO;
import com.minimall.dto.auth.LoginVO;
import com.minimall.dto.auth.RegisterDTO;
import com.minimall.entity.User;
import com.minimall.mapper.UserMapper;
import com.minimall.service.UserService;
import com.minimall.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void register(RegisterDTO dto) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count != null && count > 0) {
            throw new BizException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null && !dto.getNickname().isBlank() ? dto.getNickname() : dto.getUsername());
        user.setTotalSpent(BigDecimal.ZERO);
        user.setMemberLevel(MemberLevel.NORMAL.getCode());
        user.setStatus(1);
        userMapper.insert(user);
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BizException("账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), "user");
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setMemberLevel(user.getMemberLevel());
        vo.setMemberLevelLabel(MemberLevel.fromCode(user.getMemberLevel()).getLabel());
        vo.setTotalSpent(user.getTotalSpent());
        return vo;
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }
}
