package com.appchemist.distribute_pay.exception;

public class InvalidDistributionException extends RuntimeException {
    public InvalidDistributionException() {
        super("Exceed max pay amount when distribute");
    }
}
