package com.minimall.config;

import com.minimall.entity.Admin;
import com.minimall.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * 首次启动初始化默认管理员。
 * <p>
 * 默认管理员不再在 init.sql 中写死密码，而是在后端启动时按需创建：
 * 密码取自环境变量 ADMIN_INIT_PASSWORD，未配置则生成随机密码并打印到日志。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    /** 随机密码字符集（去掉易混淆的 0/O、1/l/I） */
    private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.init-password:}")
    private String initPassword;

    @Override
    public void run(String... args) {
        Long count = adminMapper.selectCount(null);
        if (count != null && count > 0) {
            return;
        }
        boolean generated = initPassword == null || initPassword.isBlank();
        String password = generated ? randomPassword(16) : initPassword;

        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode(password));
        admin.setName("超级管理员");
        admin.setStatus(1);
        adminMapper.insert(admin);

        if (generated) {
            log.warn("已创建默认管理员 admin，本次生成的临时密码为：{} —— 请登录后尽快修改", password);
        } else {
            log.info("已创建默认管理员 admin（密码来自 ADMIN_INIT_PASSWORD）");
        }
    }

    private String randomPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }
}
