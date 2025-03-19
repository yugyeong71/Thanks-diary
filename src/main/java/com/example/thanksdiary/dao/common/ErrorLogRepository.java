package com.example.thanksdiary.dao.common;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.thanksdiary.domain.common.ErrorLog;

import lombok.extern.java.Log;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Log> {
}
