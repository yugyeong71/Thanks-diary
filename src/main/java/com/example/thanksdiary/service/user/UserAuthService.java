package com.example.thanksdiary.service.user;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.thanksdiary.common.exception.UnauthorizedException;
import com.example.thanksdiary.common.jwt.JwtTokenUtil;
import com.example.thanksdiary.dao.user.UserRepository;
import com.example.thanksdiary.domain.common.enums.TokenType;
import com.example.thanksdiary.domain.user.User;
import com.example.thanksdiary.dto.user.request.UserSignUpRequest;
import com.example.thanksdiary.dto.user.response.UserSignUpResponse;
import com.example.thanksdiary.service.common.RedisService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAuthService {

	private final UserRepository userRepository;

	private final EmailService emailService;

	private final PasswordEncoder passwordEncoder;

	private final JwtTokenUtil jwtTokenUtil;

	private final RedisService redisService;


	/**
	 * 사용자 회원가입
	 */
	@Transactional
	public UserSignUpResponse userSignUp(UserSignUpRequest userSignUpRequest) {
		emailService.emailCheck(userSignUpRequest.getEmail());

		String code = Optional.ofNullable(redisService.getData(userSignUpRequest.getEmail()))
			.orElseThrow(() -> new UnauthorizedException("인증 코드가 존재하지 않습니다."));

		if (!userSignUpRequest.getCode().equals(code)) {
			throw new UnauthorizedException("인증 코드가 일치하지 않습니다.");
		}

		User user = userRepository.save(User.builder()
				.email(userSignUpRequest.getEmail())
				.password(passwordEncoder.encode(userSignUpRequest.getPassword()))
			.build());

		Long userId = user.getId();

		String accessToken = jwtTokenUtil.createToken(user, TokenType.ACCESS_TOKEN);
		String refreshToken = jwtTokenUtil.createToken(user, TokenType.REFRESH_TOKEN);

		user.updateLogin(refreshToken);

		return UserSignUpResponse.builder()
			.id(userId)
			.email(user.getEmail())
			.accessToken(accessToken)
			.refreshToken(user.getRefreshToken())
			.build();
	}

}
