package com.jeongseonghun.urlshortener.domain;

public interface ClickLogWriter {

    void save(ClickLog clickLog);
}
