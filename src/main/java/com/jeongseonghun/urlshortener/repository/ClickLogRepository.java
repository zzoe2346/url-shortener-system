package com.jeongseonghun.urlshortener.repository;

import com.jeongseonghun.urlshortener.domain.ClickLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClickLogRepository extends JpaRepository<ClickLog, Long> {
}
