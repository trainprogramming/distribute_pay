package com.appchemist.distribute_pay.application.service;

public interface DistributeStrategy {
    long distributeEach(long total, int size);
}
