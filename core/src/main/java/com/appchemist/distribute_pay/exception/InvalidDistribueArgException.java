package com.appchemist.distribute_pay.exception;

public class InvalidDistribueArgException extends RuntimeException {
    public InvalidDistribueArgException(Long maxPay, int maxTarget) {
        super(String.format("Invalid Distribution Request : Max Target(%d) exceeds Max Pay(%d)", maxTarget, maxPay));
    }
}
