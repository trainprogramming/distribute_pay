package com.appchemist.distribute_pay.domain;

import com.appchemist.distribute_pay.application.service.DistributeStrategy;
import com.appchemist.distribute_pay.application.service.TokenGenerator;
import com.appchemist.distribute_pay.exception.InvalidMethodCallException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DistributePayTest {
    private TokenGenerator mockTokenGenerator;
    private DistributeStrategy mockDistributeStrategy;

    @BeforeEach
    public void setUp() {
        mockTokenGenerator = mock(TokenGenerator.class);
        mockDistributeStrategy = mock(DistributeStrategy.class);
    }

    @Test
    public void 뿌리기_생성() {
        // given
        when(mockTokenGenerator.generate())
                .thenReturn("111")
                .thenReturn("222");
        when(mockDistributeStrategy.distributeEach(anyLong(), anyInt()))
                .thenReturn(5000L)
                .thenReturn(5000L);

        // when
        DistributePay distributePay = new DistributePay(new DistributePayID(null, "room1"),
                1, 10000, 2, new Date(), new ArrayList<>(), mockTokenGenerator, mockDistributeStrategy);
        distributePay.generateToken();
        String token1 = distributePay.getToken();
        distributePay.generateToken();
        String token2 = distributePay.getToken();
        distributePay.distribute();

        // then
        assertEquals("room1", distributePay.getRoomId());
        assertEquals(1, distributePay.getOwnerId());
        assertEquals(10000, distributePay.getMaxPay());
        assertEquals(2, distributePay.getMaxTarget());
        assertEquals("111", token1);
        assertEquals("222", token2);
        assertEquals(5000L, distributePay.getPickUps().get(0).getAmount());
        assertEquals(5000L, distributePay.getPickUps().get(1).getAmount());
    }

    @Test
    public void 잘못된_토큰_생성() {
        // given

        // when
        DistributePay distributePay = new DistributePay(new DistributePayID(null, "room1"),
                1, 10000, 2, new Date(), new ArrayList<>(), null, null);

        // then
        assertThrows(InvalidMethodCallException.class, () -> {
            distributePay.generateToken();
        });
    }

    @Test
    public void 잘못된_나누기() {
        // given

        // when
        DistributePay distributePay = new DistributePay(new DistributePayID(null, "room1"),
                1, 10000, 2, new Date(), new ArrayList<>(), null, null);

        // then
        assertThrows(InvalidMethodCallException.class, () -> {
            distributePay.distribute();
        });
    }
}