package com.jeongseonghun.urlshortener.infrastructure;

import com.jeongseonghun.urlshortener.domain.ClickLog;
import com.jeongseonghun.urlshortener.domain.ClickLogWriter;
import com.jeongseonghun.urlshortener.repository.ClickLogRepository;
import org.springframework.stereotype.Component;

@Component
public class DefaultClickLogWriter implements ClickLogWriter {
    private final ClickLogRepository clickLogRepository;

    public DefaultClickLogWriter(ClickLogRepository clickLogRepository) {
        this.clickLogRepository = clickLogRepository;
    }

    @Override
    public void save(ClickLog clickLog) {
        clickLogRepository.save(clickLog);
    }

}
