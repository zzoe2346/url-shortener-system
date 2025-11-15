package com.jeongseonghun.urlshortener.infrastructure;

import com.jeongseonghun.urlshortener.domain.OriginalUrl;
import com.jeongseonghun.urlshortener.domain.ShortUrlCreationPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ShortUrlCreationPolicyManager {

    private final List<ShortUrlCreationPolicy> policies;

    public void execute(OriginalUrl originalUrl) {
        for (ShortUrlCreationPolicy policy : policies) {
            policy.validate(originalUrl);
        }
    }
}
