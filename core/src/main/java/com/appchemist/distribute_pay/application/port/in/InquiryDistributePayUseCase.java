package com.appchemist.distribute_pay.application.port.in;

import com.appchemist.distribute_pay.domain.DistributePay;
import reactor.core.publisher.Mono;

public interface InquiryDistributePayUseCase {
    Mono<DistributePay> inquiry(String token, long userId, String roomId);
}
