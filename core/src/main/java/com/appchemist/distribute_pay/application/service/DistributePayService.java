package com.appchemist.distribute_pay.application.service;

import com.appchemist.distribute_pay.application.port.in.DistributePayFactory;
import com.appchemist.distribute_pay.application.port.in.DistributePayUseCase;
import com.appchemist.distribute_pay.application.port.out.DistributePayPort;
import com.appchemist.distribute_pay.application.port.out.SaveDistributionPort;
import com.appchemist.distribute_pay.common.UseCase;
import com.appchemist.distribute_pay.domain.DistributePay;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@UseCase
@RequiredArgsConstructor
class DistributePayService implements DistributePayUseCase {
    private final static int RETRY_CNT_OF_GENTERATE_TOKEN = 2;

    private final DistributePayFactory factory;
    private final DistributePayPort distributePayPort;
    private final SaveDistributionPort saveDistributionPort;

    @Override
    public Mono<DistributePay> distributePay(long userId, String roomId, long maxPay, int maxTarget) {
        return Mono.just(factory.newOne(roomId, userId, maxPay, maxTarget))
                .flatMap(dPay -> distributePayPort.save(dPay.generateToken()))
                .retryWhen(Retry.max(RETRY_CNT_OF_GENTERATE_TOKEN))
                .map(DistributePay::distribute)
                .flatMap(saveDistributionPort::saveDistribution);
    }
}
