package com.minimall.common.enums;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * 会员等级
 */
@Getter
public enum MemberLevel {
    NORMAL(1, "普通", BigDecimal.ZERO),
    GOLD(2, "黄金", new BigDecimal("10000")),
    PLATINUM(3, "铂金", new BigDecimal("50000")),
    DIAMOND(4, "钻石", new BigDecimal("100000"));

    private final int code;
    private final String label;
    /** 升级所需累计消费额（元） */
    private final BigDecimal threshold;

    MemberLevel(int code, String label, BigDecimal threshold) {
        this.code = code;
        this.label = label;
        this.threshold = threshold;
    }

    /**
     * 根据累计消费额计算会员等级（取达到阈值的最高等级，等级只升不降）
     */
    public static MemberLevel levelForTotalSpent(BigDecimal totalSpent) {
        if (totalSpent == null) {
            return NORMAL;
        }
        MemberLevel result = NORMAL;
        for (MemberLevel level : values()) {
            if (totalSpent.compareTo(level.threshold) >= 0) {
                result = level;
            }
        }
        return result;
    }
}
