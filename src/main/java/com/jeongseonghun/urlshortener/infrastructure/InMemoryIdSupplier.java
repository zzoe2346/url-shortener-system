package com.jeongseonghun.urlshortener.infrastructure;

import com.jeongseonghun.urlshortener.domain.IdSupplier;
import com.jeongseonghun.urlshortener.support.Base62;
import com.jeongseonghun.urlshortener.repository.ShortUrlRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 애플리케이션 메모리 상에서 {@link java.util.concurrent.atomic.AtomicLong}을 사용하여 ID를 발급하는 가장 단순한 구현체.
 * <p>
 * ID는 1부터 시작하여 {@link #getId()}가 호출될 때마다 1씩 증가한다.
 * 별도의 외부 시스템(DB, Redis 등)에 의존하지 않아 매우 빠르고 설정이 간단한 장점이 있다.
 *
 * <p> 사용 시 제약사항
 * <ul>
 *     <li>분산 환경 사용 불가: 상태를 메모리에서만 관리하므로, 여러 서버 인스턴스가 동작하는
 *         분산 환경에서는 ID 충돌이 발생하여 유일성을 보장할 수 없다.</li>
 * </ul>
 * <p>
 * 주로 로컬 개발 환경, 통합 테스트, 또는 서버가 1대만 운영되는 것이 보장된 단순한 환경에서 사용하기에 적합.
 *
 * @see IdSupplier
 */
@Component
@Profile("in-memory")
public class InMemoryIdSupplier implements IdSupplier {
    private final ShortUrlRepository shortUrlRepository;

    // 메모리 기반 카운터 역할
    private AtomicLong atomicLong;

    public InMemoryIdSupplier(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    /**
     * 카운터 초기화용 메서드.
     *
     * <p>
     * 애플리케이션 시작 시 DB에서 마지막으로 저장된 UrlMapping 엔티티를 가져와서 ShortCode 값을 Base62로 디코딩하여 그 값의 +1 값으로
     * 카운터를 초기화한다. 만약 없다면 1로 초기화한다.
     * </p>
     */
    @PostConstruct
    private void init() {
        long startId = shortUrlRepository.findTopByOrderByIdDesc()
                .map(mapping -> Base62.Decoder.decode(mapping.getShortKey()) + 1)
                .orElse(1L);
        atomicLong = new AtomicLong(startId);
    }

    @Override
    public Long getId() {
        return atomicLong.getAndIncrement();
    }

}
