package com.jeongseonghun.urlshortener.infrastructure;

import com.jeongseonghun.urlshortener.domain.IdSupplier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis의 INCR 명령어를 사용하여 전역적으로 유일한 ID를 발급하는 구현체.
 *
 * <p>
 * Redis의 INCR 명령어는 원자성(Atomic)을 보장하므로, 여러 서버 인스턴스가 동시에 ID를 요청하더라도
 * 충돌 없이 안전하게 유일한 값을 할당할 수 있다.
 * <p>
 *
 * @see IdSupplier
 */
@Component
@Profile("redis")
public class RedisIdSupplier implements IdSupplier {

    private final StringRedisTemplate redisTemplate;
    private static final String COUNTER_KEY = "ID_Counter";

    public RedisIdSupplier(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public Long getId() {
        return redisTemplate.opsForValue().increment(COUNTER_KEY);
    }
}
