package com.appchemist.distribute_pay.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PickUp {
    private long targetId;
    private long amount;
}
