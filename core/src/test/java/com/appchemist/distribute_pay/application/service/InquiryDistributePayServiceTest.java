package com.appchemist.distribute_pay.application.service;

import com.appchemist.distribute_pay.application.port.in.InquiryDistributePayUseCase;
import com.appchemist.distribute_pay.application.port.out.DistributePayPort;
import com.appchemist.distribute_pay.domain.DistributePay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class InquiryDistributePayServiceTest {
    private InquiryDistributePayUseCase useCase;

    private DistributePayPort mockDistributePayPort;

    @BeforeEach
    void setUp() {
        mockDistributePayPort = mock(DistributePayPort.class);
        useCase = new InquiryDistributePayService(mockDistributePayPort);
    }

    @Test
    void 예외_상황() {
        // given
        when(mockDistributePayPort.load(any(), anyLong())).thenReturn(Mono.error(new RuntimeException()));

        // when
        Mono<DistributePay> ret = useCase.inquiry("token", 1, "roomId");

        // then
        StepVerifier.create(ret)
                .expectError(RuntimeException.class)
                .verify();
        verify(mockDistributePayPort, times(1)).load(any(), anyLong());
    }
}