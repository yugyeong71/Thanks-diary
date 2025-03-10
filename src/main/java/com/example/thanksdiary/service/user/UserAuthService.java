package com.example.thanksdiary.service.user;

import java.util.Objects;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.thanksdiary.common.exception.TokenExpiredException;
import com.example.thanksdiary.common.exception.UnauthorizedException;
import com.example.thanksdiary.common.jwt.JwtTokenUtil;
import com.example.thanksdiary.dao.user.UserRepository;
import com.example.thanksdiary.domain.common.enums.TokenType;
import com.example.thanksdiary.domain.common.enums.UserRole;
import com.example.thanksdiary.domain.user.User;
import com.example.thanksdiary.dto.user.request.UserLoginRequest;
import com.example.thanksdiary.dto.user.request.UserSignUpRequest;
import com.example.thanksdiary.dto.user.response.AccessTokenRefreshResponse;
import com.example.thanksdiary.dto.user.response.UserLoginResponse;
import com.example.thanksdiary.dto.user.response.UserSignUpResponse;
import com.example.thanksdiary.service.common.RedisService;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
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
			.refreshToken(refreshToken)
			.build();
	}

	/**
	 * 사용자 로그인
	 */
	@Transactional
	public UserLoginResponse userLogin(UserLoginRequest userLoginRequest) {
		User user = userRepository.findByEmail(userLoginRequest.getEmail()).orElseThrow(() -> new UnauthorizedException("일치하는 정보가 없습니다.\n이메일과 비밀번호를 다시 확인해 주세요."));

		if (!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())){
			throw new UnauthorizedException("일치하는 정보가 없습니다.\n이메일과 비밀번호를 다시 확인해 주세요.");
		}

		String accessToken = jwtTokenUtil.createToken(user, TokenType.ACCESS_TOKEN);
		String refreshToken = jwtTokenUtil.createToken(user, TokenType.REFRESH_TOKEN);

		user.updateLogin(refreshToken);

		return UserLoginResponse.builder()
			.id(user.getId())
			.email(user.getEmail())
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	/**
	 * AccessToken 재발급
	 */
	@Transactional(readOnly = true)
	public AccessTokenRefreshResponse accessTokenRefresh(HttpServletRequest httpServletRequest) {
		String refreshToken = jwtTokenUtil.getRefreshToken(httpServletRequest);

		if (jwtTokenUtil.isExpired(refreshToken, TokenType.REFRESH_TOKEN)) {
			throw new TokenExpiredException();
		}

		Long id = jwtTokenUtil.getUserId(refreshToken, TokenType.REFRESH_TOKEN);

		UserRole userRole = jwtTokenUtil.getRoles(refreshToken, TokenType.REFRESH_TOKEN);

		if (Objects.requireNonNull(userRole) == UserRole.ROLE_USER) {
			User user = userRepository.findById(id).orElseThrow(TokenExpiredException::new);

			if (StringUtils.isBlank(user.getRefreshToken()) || !user.getRefreshToken().equals(refreshToken)) {
				throw new UnauthorizedException("유효하지 않은 Refresh Token입니다.");
			}

			return AccessTokenRefreshResponse.builder()
				.accessToken(jwtTokenUtil.createToken(user, TokenType.ACCESS_TOKEN))
				.build();
		}

		throw new TokenExpiredException();
	}

}
