package com.appchemist.distribute_pay.application.service;

import com.appchemist.distribute_pay.application.port.in.PickUpUseCase;
import com.appchemist.distribute_pay.application.port.out.PickUpDistributePayPort;
import com.appchemist.distribute_pay.application.port.out.AddPickUpPort;
import com.appchemist.distribute_pay.domain.DistributePayID;
import com.appchemist.distribute_pay.domain.PickUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class PickUpServiceTest {
    private PickUpUseCase useCase;
    private PickUpDistributePayPort mockPickUpDistributePayPort;
    private AddPickUpPort mockAddPickUpPort;

    @BeforeEach
    void setUp() {
        mockPickUpDistributePayPort = mock(PickUpDistributePayPort.class);
        mockAddPickUpPort = mock(AddPickUpPort.class);
    }

    @Test
    void 페이_줍기() {
        // given
        when(mockPickUpDistributePayPort.pickUpPay(any(), anyLong())).thenReturn(Mono.just(1000L));
        when(mockAddPickUpPort.add(any(), any())).thenReturn(Mono.just(new PickUp(1, 1000L)));
        useCase = new PickUpService(mockPickUpDistributePayPort, mockAddPickUpPort);

        // when
        Mono<PickUp> ret = useCase.pickUpPay("token", 1, "roomId");
        PickUp pickUp = ret.block();

        // then
        assertNotNull(ret);
        assertNotNull(pickUp);
        assertEquals(1, pickUp.getTargetId());
        assertEquals(1000L, pickUp.getAmount());
        verify(mockPickUpDistributePayPort, times(1)).pickUpPay(any(), anyLong());
        verify(mockAddPickUpPort, times(1)).add(any(), any());
    }

    @Test
    void 중복_페이_줍기() {
        // given
        when(mockPickUpDistributePayPort.pickUpPay(any(), anyLong())).thenReturn(Mono.error(new PickUpDistributePayPort.AlreadyPickUpException(new DistributePayID("token", "roomId"), 1)));
        when(mockAddPickUpPort.add(any(), any())).thenReturn(Mono.just(new PickUp(1, 1000L)));
        useCase = new PickUpService(mockPickUpDistributePayPort, mockAddPickUpPort);

        // when
        Mono<PickUp> ret = useCase.pickUpPay("token", 1, "roomId");

        // then
        StepVerifier.create(ret)
                .expectError(PickUpDistributePayPort.AlreadyPickUpException.class)
                .verify();
        verify(mockPickUpDistributePayPort, times(1)).pickUpPay(any(), anyLong());
        verify(mockAddPickUpPort, never()).add(any(), any());
    }

    @Test
    void 허용되지_않_페이_줍기() {
        // given
        when(mockPickUpDistributePayPort.pickUpPay(any(), anyLong())).thenReturn(Mono.error(new PickUpDistributePayPort.NotAllowedPickUpException("not allowed")));
        when(mockAddPickUpPort.add(any(), any())).thenReturn(Mono.just(new PickUp(1, 1000L)));
        useCase = new PickUpService(mockPickUpDistributePayPort, mockAddPickUpPort);

        // when
        Mono<PickUp> ret = useCase.pickUpPay("token", 1, "roomId");

        // then
        StepVerifier.create(ret)
                .expectError(PickUpDistributePayPort.NotAllowedPickUpException.class)
                .verify();
        verify(mockPickUpDistributePayPort, times(1)).pickUpPay(any(), anyLong());
        verify(mockAddPickUpPort, never()).add(any(), any());
    }

    @Test
    void 저장시_예외() {
        // given
        when(mockPickUpDistributePayPort.pickUpPay(any(), anyLong())).thenReturn(Mono.just(10000L));
        when(mockAddPickUpPort.add(any(), any())).thenReturn(Mono.error(new RuntimeException()));
        useCase = new PickUpService(mockPickUpDistributePayPort, mockAddPickUpPort);

        // when
        Mono<PickUp> ret = useCase.pickUpPay("token", 1, "roomId");

        // then
        StepVerifier.create(ret)
                .expectError(RuntimeException.class)
                .verify();
        verify(mockPickUpDistributePayPort, times(1)).pickUpPay(any(), anyLong());
        verify(mockAddPickUpPort, times(1)).add(any(), any());
    }
}