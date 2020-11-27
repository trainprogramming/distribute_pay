package com.appchemist.distribute_pay.application.service;

import java.util.Random;

class SameDistribute implements DistributeStrategy {
    private Random rand = new Random();

    @Override
    public long distributeEach(long total, int size) {
        if (size == 1) return total;

        return total / size;
    }
}
