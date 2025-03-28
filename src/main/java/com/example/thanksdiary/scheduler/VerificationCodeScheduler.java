package com.example.thanksdiary.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.thanksdiary.dao.verificationCode.VerificationCodeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationCodeScheduler {

	private final VerificationCodeRepository verificationCodeRepository;

	@Scheduled(fixedRate = 60000) // 1분마다 실행
	@Transactional
	public void deleteExpiredVerifications() {
		int deletedCount = verificationCodeRepository.deleteByExpiresAtBefore(LocalDateTime.now());
		log.info("삭제된 인증 코드 개수 : {}", deletedCount);
	}

}
