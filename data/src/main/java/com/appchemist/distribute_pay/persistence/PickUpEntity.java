package com.appchemist.distribute_pay.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class PickUpEntity {
    private long targetId;
    private long amount;
}
