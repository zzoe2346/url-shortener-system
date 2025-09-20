package com.jeongseonghun.urlshortener.util;

import java.util.concurrent.atomic.AtomicLong;

public class IDSupplier {
    private static final AtomicLong atomicLong = new AtomicLong(1);

    private IDSupplier() {
    }

    public static Long getId() {
        return atomicLong.getAndIncrement();
    }
}
