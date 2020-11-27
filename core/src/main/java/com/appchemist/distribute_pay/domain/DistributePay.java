package com.appchemist.distribute_pay.domain;

import com.appchemist.distribute_pay.application.service.DistributeStrategy;
import com.appchemist.distribute_pay.application.service.TokenGenerator;
import com.appchemist.distribute_pay.exception.InvalidDistribueArgException;
import com.appchemist.distribute_pay.exception.InvalidDistributionException;
import com.appchemist.distribute_pay.exception.InvalidMethodCallException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
public class DistributePay {
    @NonNull
    private DistributePayID id;
    @Getter
    private long ownerId;
    @Getter
    private long maxPay;
    @Getter
    private int maxTarget;
    @Getter
    private Date created;
    private List<PickUp> pickUps;

    private TokenGenerator tokenGenerator;
    private DistributeStrategy distributeStrategy;

    public DistributePay generateToken() throws InvalidMethodCallException {
        if (tokenGenerator == null) throw new InvalidMethodCallException("Not Initialized TokenGenerator!");

        id.setToken(tokenGenerator.generate());
        return this;
    }

    public DistributePay distribute() throws InvalidMethodCallException {
        if (distributeStrategy == null) throw new InvalidMethodCallException("Not Initialized DistributeStrategy!");

        if (maxPay < maxTarget) {
            throw new InvalidDistribueArgException(maxPay, maxTarget);
        }

        long rest = maxPay;
        int restTarget = maxTarget;

        for (int i = 0; i < maxTarget; i ++) {
            long pay = distributeStrategy.distributeEach(rest, restTarget);
            rest -= pay;
            restTarget--;

            if (rest < 0) {
                pickUps.clear();
                throw new InvalidDistributionException();
            }

            pickUps.add(new PickUp(-1, pay));
        }

        return this;
    }

    public long getTotalPayments() {
        long total = 0L;
        for (PickUp pickUp : pickUps) {
            total += pickUp.getAmount();
        }

        return total;
    }

    public String getRoomId() { return id.getRoomId(); }
    public String getToken() { return id.getToken(); }
    public List<PickUp> getPickUps() {
        if (pickUps == null) return null;

        return Collections.unmodifiableList(pickUps);
    }
}
