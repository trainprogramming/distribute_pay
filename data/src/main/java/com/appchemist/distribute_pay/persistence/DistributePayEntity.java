package com.appchemist.distribute_pay.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@Document("distributePay")
@CompoundIndexes({
        @CompoundIndex(name = "distribute_pay_id", def = "{'token': 1, 'roomId': 1}"),
        @CompoundIndex(name = "for_query", def = "{'token': 1, 'roomId': 1, 'ownerId': 1}")
})
class DistributePayEntity {
    private String token;
    private String roomId;
    private long ownerId;
    private long maxPay;
    private int maxTarget;
    private Date created;
    private List<PickUpEntity> pickUps;
}
