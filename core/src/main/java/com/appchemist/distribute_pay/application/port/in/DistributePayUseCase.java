package com.appchemist.distribute_pay.application.port.in;

import com.appchemist.distribute_pay.domain.DistributePay;
import reactor.core.publisher.Mono;

public interface DistributePayUseCase {
    Mono<DistributePay> distributePay(long userId, String roomId, long maxPay, int maxTarget);
}