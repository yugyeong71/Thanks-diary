package com.example.thanksdiary.dao.common;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.thanksdiary.domain.common.AccessLog;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
}
