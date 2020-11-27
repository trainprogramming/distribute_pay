package com.appchemist.distribute_pay.application.port.out;

import com.appchemist.distribute_pay.domain.DistributePayID;
import reactor.core.publisher.Mono;

public interface PickUpDistributePayPort {
    Mono<Long> pickUpPay(DistributePayID distributePayID, long targetId);

    class AlreadyPickUpException extends RuntimeException {
        public AlreadyPickUpException(DistributePayID distributePayID, long targetId) {
            super(String.format("Already Pick Up : Distribute Pay(%s, %s), Target User(%d)", distributePayID.getToken(), distributePayID.getRoomId(), targetId));
        }
    }

    class NotAllowedPickUpException extends RuntimeException {
        public NotAllowedPickUpException(String message) {
            super(message);
        }
    }
}
