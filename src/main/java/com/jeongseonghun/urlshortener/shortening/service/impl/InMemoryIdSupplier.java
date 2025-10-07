package com.jeongseonghun.urlshortener.shortening.service.impl;

import com.jeongseonghun.urlshortener.shortening.service.IdSupplier;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryIdSupplier implements IdSupplier {

    private static final AtomicLong atomicLong = new AtomicLong(1);

    @Override
    public Long getId() {
        return atomicLong.getAndIncrement();
    }

}
