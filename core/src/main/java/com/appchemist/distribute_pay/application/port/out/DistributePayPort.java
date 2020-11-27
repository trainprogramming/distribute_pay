package com.appchemist.distribute_pay.application.port.out;

import com.appchemist.distribute_pay.domain.DistributePay;
import com.appchemist.distribute_pay.domain.DistributePayID;
import reactor.core.publisher.Mono;

public interface DistributePayPort {
    Mono<DistributePay> save(DistributePay distributePay);
    Mono<DistributePay> load(DistributePayID distributePayID, long userId);

    class DuplicateDistributePayIDException extends RuntimeException {
        public DuplicateDistributePayIDException(String token, String roomId) {
            super(String.format("Duplicate Distribute Pay Id : token(%s), roomId(%s)", token, roomId));
        }
    }

    class NotExistTokenException extends RuntimeException {
        public NotExistTokenException(String token, String roomId) {
            super(String.format("Not Exist Token : token(%s), roomId(%s)", token, roomId));
        }
    }
}
