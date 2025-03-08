package com.example.thanksdiary.common.jwt;

import static com.example.thanksdiary.config.WebSecurityConfig.*;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.thanksdiary.common.exception.TokenExpiredException;
import com.example.thanksdiary.domain.common.enums.ResponseMessage;
import com.example.thanksdiary.domain.common.enums.TokenType;
import com.example.thanksdiary.dto.common.ErrorResponse;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		ServletException,
		IOException {
		log.info(" === JWT Filter === ");

		String projectName = "/thanks-diary";
		String requestURI = request.getRequestURI().startsWith(projectName) ? request.getRequestURI().substring(projectName.length()) : request.getRequestURI();

		boolean permitAll = Arrays.stream(PERMIT_ALL)
			.anyMatch(url ->
				url.endsWith("*") ? requestURI.startsWith(url.substring(0, url.length() - 1)) : requestURI.equals(url)
			);

		if (!permitAll) {
			try {
				String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

				if (StringUtils.isBlank(accessToken)) throw new TokenExpiredException();

				if (jwtTokenUtil.isExpired(accessToken, TokenType.ACCESS_TOKEN)) throw new TokenExpiredException();

				if (jwtTokenUtil.isUnauthorized(accessToken, TokenType.ACCESS_TOKEN)) throw new TokenExpiredException();

				Authentication authentication = jwtTokenUtil.getAuthentication(accessToken, TokenType.ACCESS_TOKEN);

				if (authentication != null) {
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			} catch (Exception e) {
				ErrorResponse.of(response, HttpStatus.FORBIDDEN, ResponseMessage.FORBIDDEN_MESSAGE.getMessage());
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

}
