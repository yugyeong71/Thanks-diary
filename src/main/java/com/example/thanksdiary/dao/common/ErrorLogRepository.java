package com.example.thanksdiary.dao.common;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.thanksdiary.domain.common.ErrorLog;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
}
