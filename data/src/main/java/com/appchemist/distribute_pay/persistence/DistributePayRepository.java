package com.appchemist.distribute_pay.persistence;

import com.appchemist.distribute_pay.common.ComponentObject;
import com.appchemist.distribute_pay.domain.DistributePayID;
import com.appchemist.distribute_pay.exception.NotExistTokenException;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;

@ComponentObject
@RequiredArgsConstructor
class DistributePayRepository {
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<DistributePayEntity> save(DistributePayEntity distributePay) {
        return reactiveMongoTemplate.save(distributePay);
    }

    public Mono<DistributePayEntity> load(DistributePayID distributePayID, long userId) {
        Query query = new Query(Criteria.where("token").is(distributePayID.getToken()).and("roomId").is(distributePayID.getRoomId()).and("ownerId").is(userId));

        return reactiveMongoTemplate.findOne(query, DistributePayEntity.class).switchIfEmpty(Mono.error(new NotExistTokenException(distributePayID.getToken(), distributePayID.getRoomId())));
    }

    public Mono<UpdateResult> addPickUp(DistributePayID distributePayID, PickUpEntity pickUpEntity) {
        Query query = new Query(Criteria.where("token").is(distributePayID.getToken()).and("roomId").is(distributePayID.getRoomId()));

        return reactiveMongoTemplate.updateFirst(query, new Update().push("pickUps", pickUpEntity), DistributePayEntity.class);
    }
}
