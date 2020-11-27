package com.appchemist.distribute_pay.application.service;

import com.appchemist.distribute_pay.application.port.in.DistributePayUseCase;
import com.appchemist.distribute_pay.application.port.out.DistributePayPort;
import com.appchemist.distribute_pay.application.port.out.SaveDistributionPort;
import com.appchemist.distribute_pay.domain.DistributePay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DistributePayServiceTest {
    private DistributePayUseCase useCase;

    private TokenGenerator mockTokenGenerator;
    private DistributeStrategy mockDistributeStrategy;
    private DistributePayPort mockDistributePayPort;
    private SaveDistributionPort mockSaveDistributionPort;
    private DistributePay mockDistributePay;
    private DefaultDistributePayFactory mockFactory;


    private DefaultDistributePayFactory factory;

    @BeforeEach
    void setUp() {
        mockTokenGenerator = mock(TokenGenerator.class);
        mockDistributeStrategy = mock(DistributeStrategy.class);
        mockDistributePayPort = mock(DistributePayPort.class);
        mockSaveDistributionPort = mock(SaveDistributionPort.class);
        mockDistributePay = mock(DistributePay.class);
        mockFactory = mock(DefaultDistributePayFactory.class);

        factory = new DefaultDistributePayFactory(mockTokenGenerator, mockDistributeStrategy);
    }

    @Test
    void 토큰_생성_기본() {
        // given
        useCase = new DistributePayService(factory, mockDistributePayPort, mockSaveDistributionPort);
        when(mockTokenGenerator.generate()).thenReturn("aaa");
        when(mockDistributePayPort.save(any())).thenAnswer(i -> Mono.just(i.getArguments()[0]));
        when(mockSaveDistributionPort.saveDistribution(any())).thenAnswer(i -> Mono.just(i.getArguments()[0]));

        // when
        Mono<DistributePay> ret = useCase.distributePay(1, "room", 1000, 10);
        DistributePay distributePay = ret.block();

        // then
        assertNotNull(ret);
        assertNotNull(distributePay);
        assertEquals(1, distributePay.getOwnerId());
        assertEquals("room", distributePay.getRoomId());
        assertEquals(1000, distributePay.getMaxPay());
        assertEquals(10, distributePay.getMaxTarget());
        assertEquals("aaa", distributePay.getToken());
    }

    @Test
    void 토큰생성자_지정_토큰_생성() {
        // given
        useCase = new DistributePayService(factory, mockDistributePayPort, mockSaveDistributionPort);
        when(mockTokenGenerator.generate()).thenReturn("abc");
        when(mockDistributePayPort.save(any())).thenAnswer(i -> Mono.just(i.getArguments()[0]));
        when(mockSaveDistributionPort.saveDistribution(any())).thenAnswer(i -> Mono.just(i.getArguments()[0]));

        // when
        Mono<DistributePay> ret = useCase.distributePay(1, "room", 1000, 10);
        DistributePay distributePay = ret.block();

        // then
        assertNotNull(ret);
        assertNotNull(distributePay);
        assertEquals(1, distributePay.getOwnerId());
        assertEquals("room", distributePay.getRoomId());
        assertEquals(1000, distributePay.getMaxPay());
        assertEquals(10, distributePay.getMaxTarget());
        assertEquals("abc", distributePay.getToken());

        verify(mockTokenGenerator, times(1)).generate();
    }

    @Test
    void 토큰_중복_케이스() {
        // given
        when(mockDistributePayPort.save(any())).thenReturn(Mono.error(new DistributePayPort.DuplicateDistributePayIDException("token", "roomId")));
        when(mockSaveDistributionPort.saveDistribution(any())).thenAnswer(i -> Mono.just(i.getArguments()[0]));
        when(mockDistributePay.generateToken()).thenReturn(mockDistributePay);
        when(mockDistributePay.getToken()).thenReturn("abc");
        when(mockFactory.newOne(anyString(), anyLong(), anyLong(), anyInt())).thenReturn(mockDistributePay);
        useCase = new DistributePayService(mockFactory, mockDistributePayPort, mockSaveDistributionPort);

        // when
        Mono<DistributePay> ret = useCase.distributePay(1, "room", 1000, 10);
        StepVerifier.create(ret)
                .expectError(RuntimeException.class)
                .verify();

        verify(mockFactory, times(1)).newOne(anyString(), anyLong(), anyLong(), anyInt());
        verify(mockDistributePayPort, times(3)).save(any());
        verify(mockDistributePay, times(3)).generateToken();
    }

    @Test
    void 토큰_중복_케이스_실패_후_성공() {
        // given
        when(mockDistributePayPort.save(any()))
                .thenReturn(Mono.error(new DistributePayPort.DuplicateDistributePayIDException("token", "roomId")))
                .thenAnswer(i -> Mono.just(i.getArguments()[0]));
        when(mockSaveDistributionPort.saveDistribution(any())).thenAnswer(i -> Mono.just(i.getArguments()[0]));
        when(mockDistributePay.generateToken()).thenReturn(mockDistributePay);
        when(mockDistributePay.distribute()).thenReturn(mockDistributePay);
        when(mockDistributePay.getToken()).thenReturn("abc");
        when(mockFactory.newOne(anyString(), anyLong(), anyLong(), anyInt())).thenReturn(mockDistributePay);
        useCase = new DistributePayService(mockFactory, mockDistributePayPort, mockSaveDistributionPort);

        // when
        Mono<DistributePay> ret = useCase.distributePay(1, "room", 1000, 10);

        // then
        DistributePay distributePay = ret.block();

        assertNotNull(ret);
        assertNotNull(distributePay);
        assertEquals("abc", distributePay.getToken());
        verify(mockFactory, times(1)).newOne(anyString(), anyLong(), anyLong(), anyInt());
        verify(mockDistributePayPort, times(2)).save(any());
        verify(mockDistributePay, times(2)).generateToken();
    }
}