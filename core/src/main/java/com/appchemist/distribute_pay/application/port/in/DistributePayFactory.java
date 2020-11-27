package com.appchemist.distribute_pay.application.port.in;

import com.appchemist.distribute_pay.domain.DistributePay;
import com.appchemist.distribute_pay.domain.PickUp;

import java.util.Date;
import java.util.List;

public interface DistributePayFactory {
    DistributePay newOne(String roomId, long ownerId, long maxPay, int maxTarget);
    DistributePay forQuery(String token, String roomId, long ownerId, long maxPay, int maxTarget, Date created, List<PickUp> pickUps);
}
