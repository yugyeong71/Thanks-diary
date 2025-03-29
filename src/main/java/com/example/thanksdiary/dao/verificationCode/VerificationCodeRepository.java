package com.example.thanksdiary.dao.verificationCode;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.thanksdiary.domain.verificationCode.VerificationCode;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

	Optional<VerificationCode> findByEmail(String email);

	void deleteByEmail(String email);

	int deleteByExpiresAtBefore(LocalDateTime now);

}
