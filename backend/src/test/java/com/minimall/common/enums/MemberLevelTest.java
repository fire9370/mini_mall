package com.minimall.common.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 会员等级升级逻辑测试
 */
class MemberLevelTest {

    @Test
    @DisplayName("累计消费 0 应为普通会员")
    void zeroIsNormal() {
        assertEquals(MemberLevel.NORMAL, MemberLevel.levelForTotalSpent(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("累计消费低于 10000 应为普通会员")
    void below10000IsNormal() {
        assertEquals(MemberLevel.NORMAL, MemberLevel.levelForTotalSpent(new BigDecimal("9999.99")));
    }

    @Test
    @DisplayName("累计消费 10000 应升级为黄金")
    void at10000IsGold() {
        assertEquals(MemberLevel.GOLD, MemberLevel.levelForTotalSpent(new BigDecimal("10000")));
    }

    @Test
    @DisplayName("累计消费 49999.99 仍为黄金")
    void below50000IsGold() {
        assertEquals(MemberLevel.GOLD, MemberLevel.levelForTotalSpent(new BigDecimal("49999.99")));
    }

    @Test
    @DisplayName("累计消费 50000 应升级为铂金")
    void at50000IsPlatinum() {
        assertEquals(MemberLevel.PLATINUM, MemberLevel.levelForTotalSpent(new BigDecimal("50000")));
    }

    @Test
    @DisplayName("累计消费 100000 应升级为钻石")
    void at100000IsDiamond() {
        assertEquals(MemberLevel.DIAMOND, MemberLevel.levelForTotalSpent(new BigDecimal("100000")));
    }

    @Test
    @DisplayName("累计消费超过 100000 仍为钻石")
    void above100000IsDiamond() {
        assertEquals(MemberLevel.DIAMOND, MemberLevel.levelForTotalSpent(new BigDecimal("999999.99")));
    }

    @Test
    @DisplayName("累计消费为 null 应回退为普通会员")
    void nullIsNormal() {
        assertEquals(MemberLevel.NORMAL, MemberLevel.levelForTotalSpent(null));
    }

    @Test
    @DisplayName("fromCode(2) 返回黄金")
    void fromCodeReturnsGold() {
        assertEquals(MemberLevel.GOLD, MemberLevel.fromCode(2));
    }

    @Test
    @DisplayName("fromCode(null) 回退普通")
    void fromCodeNullReturnsNormal() {
        assertEquals(MemberLevel.NORMAL, MemberLevel.fromCode(null));
    }
}
