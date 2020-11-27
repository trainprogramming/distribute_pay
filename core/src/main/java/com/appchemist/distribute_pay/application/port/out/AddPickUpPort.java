package com.appchemist.distribute_pay.application.port.out;

import com.appchemist.distribute_pay.domain.DistributePayID;
import com.appchemist.distribute_pay.domain.PickUp;
import reactor.core.publisher.Mono;

public interface AddPickUpPort {
    Mono<PickUp> add(DistributePayID DistributePayID, PickUp pickUp);
}
