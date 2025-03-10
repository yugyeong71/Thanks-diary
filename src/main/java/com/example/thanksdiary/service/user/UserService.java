package com.example.thanksdiary.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.thanksdiary.common.exception.NotFoundDataException;
import com.example.thanksdiary.common.exception.UnauthorizedException;
import com.example.thanksdiary.common.jwt.JwtTokenUtil;
import com.example.thanksdiary.dao.user.UserRepository;
import com.example.thanksdiary.domain.common.enums.TokenType;
import com.example.thanksdiary.domain.common.enums.UserRole;
import com.example.thanksdiary.domain.user.User;
import com.example.thanksdiary.dto.user.response.UserAutoLoginResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final JwtTokenUtil jwtTokenUtil;

	/**
	 * 토큰에서 User 정보 추출
	 */
	public User getUser(HttpServletRequest servletRequest) {
		String token = jwtTokenUtil.getAccessToken(servletRequest);

		UserRole userRole = jwtTokenUtil.getRoles(token, TokenType.ACCESS_TOKEN);

		if (!userRole.equals(UserRole.ROLE_USER)) {
			throw new UnauthorizedException("권한이 없는 사용자입니다.");
		}

		Long userId = jwtTokenUtil.getUserId(token, TokenType.ACCESS_TOKEN);

		return userRepository.findById(userId).orElseThrow(() -> new NotFoundDataException("존재하지 않는 사용자입니다."));
	}

	/**
	 * 사용자 회원탈퇴
	 */
	@Transactional
	public void userRevoke(HttpServletRequest httpServletRequest) {
		User user = this.getUser(httpServletRequest);
		user.revoke();
	}

	/**
	 * 사용자 자동 로그인
	 */
	@Transactional
	public UserAutoLoginResponse userAutoLogin(HttpServletRequest httpServletRequest) {
		User user = this.getUser(httpServletRequest);

		String accessToken = jwtTokenUtil.createToken(user, TokenType.ACCESS_TOKEN);
		String refreshToken = jwtTokenUtil.createToken(user, TokenType.REFRESH_TOKEN);

		user.updateLogin(refreshToken);

		return UserAutoLoginResponse.builder()
			.id(user.getId())
			.email(user.getEmail())
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}
