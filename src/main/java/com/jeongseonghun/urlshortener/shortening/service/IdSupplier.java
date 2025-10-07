package com.jeongseonghun.urlshortener.shortening.service;

/**
 * 시스템 전체에서 고유한 식별자(ID)를 제공하기 위한 인터페이스.
 *
 * 구현체는 멀티스레드 환경에서도 안전하게(thread-safe) ID를 발급할 수 있어야 하며,
 * 호출할 때마다 이전에 반환된 적 없는 새로운 ID를 반환하는 것을 보장해야 된다.
 */
public interface IdSupplier {

    /**
     * 고유한 Long 타입의 ID를 반환.
     *
     * @return 새로운 고유 ID
     */
    Long getId();

}
