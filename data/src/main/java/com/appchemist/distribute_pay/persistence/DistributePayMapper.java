package com.appchemist.distribute_pay.persistence;

import com.appchemist.distribute_pay.application.port.in.DistributePayFactory;
import com.appchemist.distribute_pay.common.ComponentObject;
import com.appchemist.distribute_pay.domain.DistributePay;
import com.appchemist.distribute_pay.domain.PickUp;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@ComponentObject
@RequiredArgsConstructor
class DistributePayMapper {
    private final DistributePayFactory factory;

    public DistributePayEntity mapToEntity(DistributePay distributePay) {
        List<PickUpEntity> pickUpEntities = null;
        if (distributePay.getPickUps() != null) {
            pickUpEntities = new ArrayList<>();
            for (PickUp pickUp : distributePay.getPickUps()) {
                pickUpEntities.add(this.mapToPickUpEntity(pickUp));
            }
        }

        return new DistributePayEntity(
                distributePay.getToken(),
                distributePay.getRoomId(),
                distributePay.getOwnerId(),
                distributePay.getMaxPay(),
                distributePay.getMaxTarget(),
                distributePay.getCreated(),
                pickUpEntities
        );
    }

    public DistributePay mapToDistributePay(DistributePayEntity entity) {
        List<PickUp> pickUps = null;
        if (entity.getPickUps() != null) {
            pickUps = new ArrayList<>();
            for (PickUpEntity pickUp : entity.getPickUps()) {
                pickUps.add(this.mapToPickUp(pickUp));
            }
        }

        return factory.forQuery(
                entity.getToken(),
                entity.getRoomId(),
                entity.getOwnerId(),
                entity.getMaxPay(),
                entity.getMaxTarget(),
                entity.getCreated(),
                pickUps
        );
    }

    public PickUp mapToPickUp(PickUpEntity entity) {
        return new PickUp(entity.getTargetId(), entity.getAmount());
    }

    public PickUpEntity mapToPickUpEntity(PickUp pickUp) {
        return new PickUpEntity(pickUp.getTargetId(), pickUp.getAmount());
    }
}
