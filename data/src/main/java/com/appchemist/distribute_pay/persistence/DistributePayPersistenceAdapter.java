package com.appchemist.distribute_pay.persistence;

import com.appchemist.distribute_pay.application.port.out.AddPickUpPort;
import com.appchemist.distribute_pay.application.port.out.DistributePayPort;
import com.appchemist.distribute_pay.application.port.out.SaveDistributionPort;
import com.appchemist.distribute_pay.application.port.out.PickUpDistributePayPort;
import com.appchemist.distribute_pay.common.PersistenceAdapter;
import com.appchemist.distribute_pay.domain.DistributePay;
import com.appchemist.distribute_pay.domain.DistributePayID;
import com.appchemist.distribute_pay.domain.PickUp;
import com.appchemist.distribute_pay.exception.NotExistTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import reactor.core.publisher.Mono;

@PersistenceAdapter
@RequiredArgsConstructor
public class DistributePayPersistenceAdapter implements DistributePayPort, AddPickUpPort, SaveDistributionPort, PickUpDistributePayPort {
    private final DistributePayRepository distributePayRepository;
    private final DistributionRepository distributionRepository;
    private final DistributePayMapper mapper;

    @Override
    public Mono<DistributePay> save(DistributePay distributePay) {
        return distributePayRepository
                .save(mapper.mapToEntity(distributePay))
                .map(o -> distributePay)
                .onErrorMap(throwable -> {
                    if (throwable instanceof DuplicateKeyException) {
                        return new DuplicateDistributePayIDException(distributePay.getToken(), distributePay.getRoomId());
                    }
                    return throwable;
                });
    }

    @Override
    public Mono<DistributePay> saveDistribution(DistributePay distributePay) {
        return distributionRepository.saveDistribution(mapper.mapToEntity(distributePay))
                .map(mapper::mapToDistributePay);
    }

    @Override
    public Mono<DistributePay> load(DistributePayID distributePayID, long userId) {
        return distributePayRepository
                .load(distributePayID, userId)
                .map(mapper::mapToDistributePay);
    }

    @Override
    public Mono<PickUp> add(DistributePayID distributePayID, PickUp pickUp) {
        return distributePayRepository
                .addPickUp(distributePayID, mapper.mapToPickUpEntity(pickUp))
                .handle((ret, sink) -> {
                    if (ret.getModifiedCount() == 0) {
                        sink.error(new NotExistTokenException(distributePayID.getToken(), distributePayID.getRoomId()));
                        return;
                    }
                    sink.next(pickUp);
                });

    }

    @Override
    public Mono<Long> pickUpPay(DistributePayID distributePayID, long targetId) {
        return distributionRepository.pickUpPay(distributePayID, targetId);
    }
}
