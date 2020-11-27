package com.appchemist.distribute_pay.application.port.in;

import com.appchemist.distribute_pay.domain.PickUp;
import reactor.core.publisher.Mono;

public interface PickUpUseCase {
    Mono<PickUp> pickUpPay(String token, long targetUserId, String roomId);
}
