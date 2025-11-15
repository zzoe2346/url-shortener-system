package com.jeongseonghun.urlshortener.infrastructure;

import com.jeongseonghun.urlshortener.domain.ClickLog;
import com.jeongseonghun.urlshortener.domain.ClickLogWriter;
import com.jeongseonghun.urlshortener.repository.ClickLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultClickLogWriter implements ClickLogWriter {

    private final ClickLogRepository clickLogRepository;

    @Override
    public void save(ClickLog clickLog) {
        clickLogRepository.save(clickLog);
    }

}
