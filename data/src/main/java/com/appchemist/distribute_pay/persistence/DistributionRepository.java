package com.appchemist.distribute_pay.persistence;

import com.appchemist.distribute_pay.application.port.out.PickUpDistributePayPort;
import com.appchemist.distribute_pay.common.ComponentObject;
import com.appchemist.distribute_pay.domain.DistributePayID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.stream.Collectors;

@ComponentObject
@RequiredArgsConstructor
class DistributionRepository {
    private final static String MARK = "o";
    public static final int TIME_OUT = 10;
    public static final String QUEU_EMPTY_VALUE = "empty";

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public Mono<DistributePayEntity> saveDistribution(DistributePayEntity distributePay) {
        String queueKey = getQueueKey(distributePay.getToken(), distributePay.getRoomId());
        String markPickUpKey = getMarkPickUpKey(distributePay.getToken(), distributePay.getRoomId(), distributePay.getOwnerId());

        return saveDistributionQueue(queueKey, distributePay)
                .flatMap(o -> markOwner(markPickUpKey))
                .flatMap(o -> setExpiretime(queueKey))
                .map(o -> distributePay);
    }

    public Mono<Long> pickUpPay(DistributePayID distributePayID, long targetId) {
        String queueKey = getQueueKey(distributePayID.getToken(), distributePayID.getRoomId());
        String markPickUpKey = getMarkPickUpKey(distributePayID.getToken(), distributePayID.getRoomId(), targetId);

        return isAvailablePickup(queueKey)
                .flatMap(o -> isAvailablePickupUser(markPickUpKey, distributePayID, targetId))
                .flatMap(o -> pickUp(queueKey));
    }

    private Mono<Boolean> setExpiretime(String key) {
        return redisTemplate.expire(key, Duration.ofMinutes(TIME_OUT));
    }

    private Mono<Boolean> markOwner(String markPickUpKey) {
        return redisTemplate.opsForValue().setIfAbsent(markPickUpKey, MARK, Duration.ofMinutes(TIME_OUT));
    }

    private Mono<Long> saveDistributionQueue(String queueKey, DistributePayEntity distributePay) {
        return redisTemplate.opsForList().rightPushAll(queueKey, distributePay.getPickUps().stream().map(o -> String.valueOf(o.getAmount())).collect(Collectors.toList()));
    }

    private Mono<Long> pickUp(String queueKey) {
        return redisTemplate.opsForList().leftPop(queueKey).defaultIfEmpty(QUEU_EMPTY_VALUE)
                .flatMap(v -> {
                    if (v.equalsIgnoreCase(QUEU_EMPTY_VALUE)) {
                        return Mono.error(new PickUpDistributePayPort.NotAllowedPickUpException("Exhaust Pay"));
                    }
                    return Mono.just(Long.valueOf(v));
                });
    }

    private Mono<Boolean> isAvailablePickupUser(String markPickUpKey, DistributePayID distributePayID, long targetId) {
        return redisTemplate.opsForValue().setIfAbsent(markPickUpKey, MARK, Duration.ofMinutes(TIME_OUT))
                .flatMap(isAvailable -> {
                    if (!isAvailable) {
                        return Mono.error(new PickUpDistributePayPort.AlreadyPickUpException(distributePayID, targetId));
                    }
                    return Mono.just(true);
                });
    }

    private Mono<Boolean> isAvailablePickup(String queueKey) {
        return redisTemplate.opsForList().size(queueKey)
                .flatMap(size -> {
                    if (size == 0) {
                        return Mono.error(new PickUpDistributePayPort.NotAllowedPickUpException("Invalid Token"));
                    }
                    return Mono.just(true);
                });
    }


    private String getMarkPickUpKey(String token, String roomId, long ownerId) {
        StringBuilder sb = new StringBuilder(token);
        sb.append(":");
        sb.append(roomId);
        sb.append(":");
        sb.append(ownerId);

        return sb.toString();
    }

    public String getQueueKey(String token, String roomId) {
        StringBuilder sb = new StringBuilder(token);
        sb.append(":");
        sb.append(roomId);

        return sb.toString();
    }
}
