package com.jeongseonghun.urlshortener.idsupplier.impl;

import com.jeongseonghun.urlshortener.idsupplier.IdSupplier;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class NonDistributedIdSupplier implements IdSupplier {

    private static final AtomicLong atomicLong = new AtomicLong(1);

    @Override
    public Long getId() {
        return atomicLong.getAndIncrement();
    }

}
