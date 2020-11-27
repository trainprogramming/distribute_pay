package com.appchemist.distribute_pay.application.port.out;

import com.appchemist.distribute_pay.domain.DistributePay;
import reactor.core.publisher.Mono;

public interface SaveDistributionPort {
    Mono<DistributePay> saveDistribution(DistributePay distributePay);
}
