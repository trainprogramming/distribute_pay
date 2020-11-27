package com.appchemist.distribute_pay.web;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
class DistributePayReqeust {
    @Min(1)
    private long maxPay;
    @Min(1)
    private int maxTarget;
}
