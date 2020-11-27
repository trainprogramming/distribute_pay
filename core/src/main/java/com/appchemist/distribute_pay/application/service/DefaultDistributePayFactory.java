package com.appchemist.distribute_pay.application.service;

import com.appchemist.distribute_pay.application.port.in.DistributePayFactory;
import com.appchemist.distribute_pay.common.ComponentObject;
import com.appchemist.distribute_pay.domain.DistributePay;
import com.appchemist.distribute_pay.domain.DistributePayID;
import com.appchemist.distribute_pay.domain.PickUp;
import com.appchemist.distribute_pay.exception.InvalidDistribueArgException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ComponentObject
class DefaultDistributePayFactory implements DistributePayFactory {
    private final TokenGenerator tokenGenerator;
    private final DistributeStrategy distributeStrategy;

    public DefaultDistributePayFactory() {
        tokenGenerator = new RandomTokenGenerator();
        distributeStrategy = new SameDistribute();
    }

    public DefaultDistributePayFactory(final TokenGenerator tokenGenerator, final DistributeStrategy distributeStrategy) {
        this.tokenGenerator = tokenGenerator;
        this.distributeStrategy = distributeStrategy;
    }

    public DistributePay newOne(String roomId, long ownerId, long maxPay, int maxTarget) {
        if (maxTarget > maxPay) {
            throw new InvalidDistribueArgException(maxPay, maxTarget);
        }

        return new DistributePay(new DistributePayID(null, roomId)
                , ownerId
                , maxPay
                , maxTarget
                , new Date()
                , new ArrayList<>()
                , tokenGenerator
                , distributeStrategy
        );
    }

    public DistributePay forQuery(String token, String roomId, long ownerId, long maxPay, int maxTarget, Date created, List<PickUp> pickUps) {
        return new DistributePay(new DistributePayID(token, roomId)
                , ownerId
                , maxPay
                , maxTarget
                , created
                , pickUps
                , null
                , null
        );
    }
}
