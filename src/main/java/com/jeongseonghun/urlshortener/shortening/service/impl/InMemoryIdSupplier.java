package com.jeongseonghun.urlshortener.shortening.service.impl;

import com.jeongseonghun.urlshortener.shortening.service.IdSupplier;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 애플리케이션 메모리 상에서 {@link java.util.concurrent.atomic.AtomicLong}을 사용하여 ID를 발급하는 가장 단순한 구현체.
 * <p>
 * ID는 1부터 시작하여 {@link #getId()}가 호출될 때마다 1씩 증가한다.
 * 별도의 외부 시스템(DB, Redis 등)에 의존하지 않아 매우 빠르고 설정이 간단한 장점이 있다.
 *
 * <p> 사용 시 제약사항:
 * <ul>
 *     <li>분산 환경 사용 불가: 상태를 메모리에서만 관리하므로, 여러 서버 인스턴스가 동작하는
 *         분산 환경에서는 ID 충돌이 발생하여 유일성을 보장할 수 없습니다.</li>
 *     <li>영속성 없음: 애플리케이션이 재시작되면 카운터는 다시 1로 초기화됩니다.</li>
 * </ul>
 *
 * 주로 로컬 개발 환경, 통합 테스트, 또는 서버가 1대만 운영되는 것이 보장된 단순한 환경에서 사용하기에 적합.
 *
 * @see IdSupplier
 */
@Component
public class InMemoryIdSupplier implements IdSupplier {

    private static final AtomicLong atomicLong = new AtomicLong(1);

    @Override
    public Long getId() {
        return atomicLong.getAndIncrement();
    }

}
