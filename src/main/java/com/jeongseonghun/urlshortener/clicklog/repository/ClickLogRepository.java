package com.jeongseonghun.urlshortener.clicklog.repository;

import com.jeongseonghun.urlshortener.clicklog.model.ClickLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClickLogRepository extends JpaRepository<ClickLog, Long> {
}
