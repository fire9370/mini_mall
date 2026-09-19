package com.minimall.util;

/**
 * 当前登录上下文（基于 ThreadLocal）
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> ADMIN_ID = new ThreadLocal<>();

    public static void setUserId(Long id) {
        USER_ID.set(id);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void setAdminId(Long id) {
        ADMIN_ID.set(id);
    }

    public static Long getAdminId() {
        return ADMIN_ID.get();
    }

    public static void clear() {
        USER_ID.remove();
        ADMIN_ID.remove();
    }

    private UserContext() {
    }
}
