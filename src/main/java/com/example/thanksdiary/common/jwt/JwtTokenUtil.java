package com.example.thanksdiary.common.jwt;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.example.thanksdiary.dao.user.UserRepository;
import com.example.thanksdiary.domain.common.enums.TokenType;
import com.example.thanksdiary.domain.common.enums.UserRole;
import com.example.thanksdiary.domain.user.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

	@Value("${jwt.accessTokenKey}")
	private String accessTokenKey;

	@Value("${jwt.refreshTokenKey}")
	private String refreshTokenKey;

	private final UserRepository userRepository;

	@PostConstruct
	protected void init() {
		accessTokenKey = Base64.getEncoder().encodeToString(accessTokenKey.getBytes());
		refreshTokenKey = Base64.getEncoder().encodeToString(refreshTokenKey.getBytes());
	}

	public String createToken(User user, TokenType tokenType) {
		return this.createToken(user.getId(), UserRole.ROLE_USER, tokenType);
	}

	private String createToken(Long userId, UserRole role, TokenType tokenType) {
		String secretKey = tokenType.equals(TokenType.ACCESS_TOKEN) ? accessTokenKey : refreshTokenKey;

		Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
		claims.put("roles", role);
		Date now = new Date();
		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + tokenType.getExpirationTime()))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	public Claims getClaims(String token, TokenType tokenType) {
		String secretKey = tokenType.equals(TokenType.ACCESS_TOKEN) ? accessTokenKey : refreshTokenKey;

		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
	}

	public UsernamePasswordAuthenticationToken getAuthentication(String token, TokenType tokenType) {
		Claims claims = this.getClaims(token, tokenType);
		List<String> roles = new ArrayList<>();
		roles.add(claims.get("roles", String.class));

		Collection<? extends GrantedAuthority> getAuthorities = roles.stream()
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());

		return new UsernamePasswordAuthenticationToken(claims, "", getAuthorities);
	}

	public String getAccessToken(HttpServletRequest request) {
		return request.getHeader(HttpHeaders.AUTHORIZATION);
	}

	public String getRefreshToken(HttpServletRequest request) {
		return request.getHeader("RefreshToken");
	}

	public boolean isExpired(String token, TokenType tokenType) {
		try {
			return this.getClaims(token, tokenType).getExpiration().before(new Date());
		} catch (Exception e) {
			return true;
		}
	}

	public Long getUserId(String token, TokenType tokenType) {
		String result = this.getClaims(token, tokenType).getSubject();
		return Long.parseLong(result);
	}

	public UserRole getRoles(String token, TokenType tokenType) {
		return UserRole.valueOf(String.valueOf(this.getClaims(token, tokenType).get("roles")));
	}

	public boolean isUnauthorized(String token, TokenType tokenType) {
		Long userId = this.getUserId(token, tokenType);

		UserRole roles = this.getRoles(token, tokenType);

		if (Objects.requireNonNull(roles) == UserRole.ROLE_USER) {
			User user = userRepository.findById(userId).orElse(null);
			return user == null;
		}

		return true;
	}

}
