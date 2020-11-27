package com.appchemist.distribute_pay.application.service;

import com.appchemist.distribute_pay.application.port.in.InquiryDistributePayUseCase;
import com.appchemist.distribute_pay.application.port.out.DistributePayPort;
import com.appchemist.distribute_pay.common.UseCase;
import com.appchemist.distribute_pay.domain.DistributePay;
import com.appchemist.distribute_pay.domain.DistributePayID;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCase
@RequiredArgsConstructor
public class InquiryDistributePayService implements InquiryDistributePayUseCase {
    private final DistributePayPort distributePayPort;

    @Override
    public Mono<DistributePay> inquiry(String token, long userId, String roomId) {
        return distributePayPort.load(new DistributePayID(token, roomId), userId);
    }
}
