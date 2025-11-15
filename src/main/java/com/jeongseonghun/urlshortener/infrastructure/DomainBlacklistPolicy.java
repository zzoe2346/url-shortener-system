package com.jeongseonghun.urlshortener.infrastructure;

import com.jeongseonghun.urlshortener.domain.OriginalUrl;
import com.jeongseonghun.urlshortener.domain.ShortUrlCreationPolicy;
import com.jeongseonghun.urlshortener.domain.ValidationException;
import com.jeongseonghun.urlshortener.support.Message;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DomainBlacklistPolicy implements ShortUrlCreationPolicy {
    //TODO DB로 초기화 및 재설정 되도록 할 것.
    private final Set<String> blacklist = Set.of("BAD.com", "불법.net");

    @Override
    public void validate(OriginalUrl originalUrl) {
        for (String blacklisted : blacklist) {
            if (blacklisted.equals(originalUrl.getDomain())) {
                throw new ValidationException(Message.URL_BLACKLISTED);
            }
        }
    }

}