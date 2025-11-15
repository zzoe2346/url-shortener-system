package com.jeongseonghun.urlshortener.infrastructure;

import com.jeongseonghun.urlshortener.config.AppProperties;
import com.jeongseonghun.urlshortener.domain.OriginalUrl;
import com.jeongseonghun.urlshortener.domain.ShortUrlCreationPolicy;
import com.jeongseonghun.urlshortener.domain.ValidationException;
import com.jeongseonghun.urlshortener.support.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SelfReferencingPolicy implements ShortUrlCreationPolicy {

    private final AppProperties appProperties;

    @Override
    public void validate(OriginalUrl originalUrl) {
        if (appProperties.getDomain().equals(originalUrl.getDomain())) {
            throw new ValidationException(Message.DUPLICATE_URL);
        }
    }

}
