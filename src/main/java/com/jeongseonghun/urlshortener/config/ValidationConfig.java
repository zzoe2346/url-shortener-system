package com.jeongseonghun.urlshortener.config;

import com.jeongseonghun.urlshortener.domain.validator.*;
import com.jeongseonghun.urlshortener.domain.validator.ValidationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {

    @Bean("shortenValidationChain")
    public ValidationHandler shortenValidationChain(
            UrlFormatHandler urlFormatHandler,
            CircularShortenHandler circularShortenHandler) {
        urlFormatHandler.setNext(circularShortenHandler);
        return urlFormatHandler;
    }

    @Bean("redirectValidationChain")
    public ValidationHandler redirectValidationChain(
            ShortCodeFormatHandler shortCodeFormatHandler,
            DomainBlacklistHandler domainBlacklistHandler,
            IpBlacklistHandler ipBlacklistHandler) {
        shortCodeFormatHandler.setNext(domainBlacklistHandler);
        domainBlacklistHandler.setNext(ipBlacklistHandler);
        return shortCodeFormatHandler;
    }
}
