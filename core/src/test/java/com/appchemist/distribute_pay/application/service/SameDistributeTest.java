package com.appchemist.distribute_pay.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SameDistributeTest {
    private DistributeStrategy distributeStrategy;
    @BeforeEach
    void setUp() {
        distributeStrategy = new SameDistribute();
    }

    @Test
    void 단건_테스트() {
        // given

        // when
        long ret = distributeStrategy.distributeEach(1000L, 1);

        // then
        assertEquals(1000L, ret);
    }

    @Test
    void 두_건_테스트() {
        // given
        int size = 2;
        long maxAmount = 5000L;

        // when
        List<Long> ret = distribute(maxAmount, size);
        Long total = 0L;

        // then
        assertEquals(size, ret.size());

        for (int i = 0; i < size; i++) {
            total += ret.get(i);
            assertEquals(2500L, ret.get(i).longValue());
        }
        assertEquals(maxAmount, total.longValue());
    }

    @Test
    public void 여러건_동일하게_나눠지지_않는_케이스() {
        // given
        int size = 5;
        long maxAmount = 99999L;

        // when
        List<Long> ret = distribute(maxAmount, size);
        Long total = 0L;

        // then
        assertEquals(size, ret.size());

        total += ret.get(0);
        assertEquals(19999L, ret.get(0).longValue());
        for (int i = 1; i < size; i++) {
            total += ret.get(i);
            assertEquals(20000L, ret.get(i).longValue());
        }
        assertEquals(maxAmount, total.longValue());
    }

    @Test
    public void 여러건_최대_케이스() {
        // given
        int size = 9999999;
        long maxAmount = 9223372036854775807L;

        // when
        List<Long> ret = distribute(maxAmount, size);
        Long total = 0L;

        // then
        assertEquals(size, ret.size());

        for (int i = 0; i < size; i++) {
            total += ret.get(i);
            assertTrue(ret.get(i).longValue() > 0);
        }
        assertEquals(maxAmount, total.longValue());
    }

    List<Long> distribute(long maxAmount, int size) {
        List<Long> all = new ArrayList<>();
        long rest = maxAmount;
        int restTarget = size;

        for (int i = 0; i < size; i ++) {
            long pay = distributeStrategy.distributeEach(rest, restTarget);
            rest -= pay;
            restTarget--;

            all.add(pay);
        }

        return all;
    }
}