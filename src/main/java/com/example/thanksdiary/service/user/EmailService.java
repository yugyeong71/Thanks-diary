package com.example.thanksdiary.service.user;

import org.springframework.stereotype.Service;

import com.example.thanksdiary.common.exception.AlreadyDataException;
import com.example.thanksdiary.dao.user.UserRepository;
import com.example.thanksdiary.dto.common.SuccessResponse;
import com.example.thanksdiary.dto.user.request.EmailCheckRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final UserRepository userRepository;

	/**
	 * 이메일 중복 확인
	 */
	public SuccessResponse emailCheck(EmailCheckRequest emailCheckRequest) {
		if (userRepository.findByEmail(emailCheckRequest.getEmail()).isPresent()){
			throw new AlreadyDataException("이미 사용 중인 이메일입니다.");
		}

		return new SuccessResponse();
	}
}
