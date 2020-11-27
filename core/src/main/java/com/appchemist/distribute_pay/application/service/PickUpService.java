package com.appchemist.distribute_pay.application.service;

import com.appchemist.distribute_pay.application.port.in.PickUpUseCase;
import com.appchemist.distribute_pay.application.port.out.PickUpDistributePayPort;
import com.appchemist.distribute_pay.application.port.out.AddPickUpPort;
import com.appchemist.distribute_pay.common.UseCase;
import com.appchemist.distribute_pay.domain.DistributePayID;
import com.appchemist.distribute_pay.domain.PickUp;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCase
@RequiredArgsConstructor
public class PickUpService implements PickUpUseCase {
    private final PickUpDistributePayPort pickUpDistributePayPort;
    private final AddPickUpPort addPickUpPort;

    @Override
    public Mono<PickUp> pickUpPay(String token, long targetUserId, String roomId) {
        DistributePayID id = new DistributePayID(token, roomId);

        return Mono.from(pickUpDistributePayPort.pickUpPay(id, targetUserId))
                .flatMap(pay -> addPickUpPort.add(id, new PickUp(targetUserId, pay)));
    }
}
