package com.example.thanksdiary.common.aop;

import java.lang.annotation.Annotation;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.thanksdiary.common.jwt.JwtTokenUtil;
import com.example.thanksdiary.dao.common.AccessLogRepository;
import com.example.thanksdiary.dao.common.ErrorLogRepository;
import com.example.thanksdiary.domain.common.AccessLog;
import com.example.thanksdiary.domain.common.ErrorLog;
import com.example.thanksdiary.domain.common.enums.TokenType;
import com.example.thanksdiary.domain.common.enums.UserRole;
import com.example.thanksdiary.dto.common.LoggerDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggerAspect {

	private final ObjectMapper objectMapper;

	private final JwtTokenUtil jwtTokenUtil;

	private final AccessLogRepository accessLogRepository;

	private final ErrorLogRepository errorLogRepository;

	@Pointcut("execution(* com.example.thanksdiary.controller..*(..))")
	private void cut() {}

	@AfterReturning(value = "cut()", returning = "returnObj")
	public void writeSuccessLog(JoinPoint joinPoint, Object returnObj) throws JsonProcessingException {
		log.info(" === AOP Success Logging Start === ");

		LoggerDto loggerDto = getLog(joinPoint);

		accessLogRepository.save(AccessLog.builder()
			.userId(loggerDto.getUserId())
			.role(loggerDto.getRole())
			.requestName(loggerDto.getRequestName())
			.requestUrl(loggerDto.getRequestUrl())
			.header(loggerDto.getHeader())
			.requestBody(loggerDto.getRequestBody())
			.responseBody(objectMapper.writeValueAsString(returnObj))
			.build());

		log.info(" === AOP Success Logging End === ");
	}

	@AfterThrowing(value = "cut()", throwing = "exception")
	public void writeFailLog(JoinPoint joinPoint, Exception exception) throws JsonProcessingException {
		log.info(" === AOP Fail Logging Start === ");

		LoggerDto loggerDto = getLog(joinPoint);

		errorLogRepository.save(ErrorLog.builder()
			.userId(loggerDto.getUserId())
			.role(loggerDto.getRole())
			.requestName(loggerDto.getRequestName())
			.requestUrl(loggerDto.getRequestUrl())
			.header(loggerDto.getHeader())
			.requestBody(loggerDto.getRequestBody())
			.errorMessage(exception.toString())
			.build());

		log.info(" === AOP Fail Logging End === ");
	}

	public LoggerDto getLog(JoinPoint joinPoint) throws JsonProcessingException {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

		Map<String, String> headers = new HashMap<>();
		Enumeration<String> headerNames = request.getHeaderNames();

		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			headers.put(headerName, request.getHeader(headerName));
		}

		Long userId = null;

		UserRole role = null;

		try {
			if (headers.containsKey("authorization")) {
				String header = headers.get("authorization");
				userId = jwtTokenUtil.getUserId(header, TokenType.ACCESS_TOKEN);
				role = jwtTokenUtil.getRoles(header, TokenType.ACCESS_TOKEN);
			} else if (headers.containsKey("refreshtoken")) {
				String header = headers.get("refreshtoken");
				userId = jwtTokenUtil.getUserId(header, TokenType.REFRESH_TOKEN);
				role = jwtTokenUtil.getRoles(header, TokenType.REFRESH_TOKEN);
			}
		} catch (Exception ignored) {}

		String requestName = null;

		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		for (Annotation annotation : methodSignature.getMethod().getAnnotations()) {
			if (annotation instanceof GetMapping) {
				requestName = ((GetMapping) annotation).name(); break;
			} else if (annotation instanceof PostMapping) {
				requestName = ((PostMapping) annotation).name(); break;
			} else if (annotation instanceof PutMapping) {
				requestName = ((PutMapping) annotation).name(); break;
			} else if (annotation instanceof DeleteMapping) {
				requestName = ((DeleteMapping) annotation).name(); break;
			} else if (annotation instanceof PatchMapping) {
				requestName = ((PatchMapping) annotation).name(); break;
			} else if (annotation instanceof RequestMapping) {
				requestName = ((RequestMapping) annotation).name(); break;
			}
		}

		CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
		String[] parameterNames = codeSignature.getParameterNames();
		Object[] args = joinPoint.getArgs();
		Map<String, Object> requestBody = new HashMap<>();
		for (int i = 0; i < parameterNames.length; i++) {
			if (codeSignature.getParameterTypes()[i].getSimpleName().equals("HttpServletRequest")) continue;

			if (args[i] != null) {
				String str = args[i].toString();
				if (str.contains("org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile")) {
					requestBody.put(parameterNames[i], str.replace("org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$Standard", ""));
					continue;
				}
			}

			requestBody.put(parameterNames[i], args[i]);
		}

		return LoggerDto.builder()
			.userId(userId)
			.role(role)
			.requestName(requestName)
			.requestUrl(request.getRequestURL().toString())
			.header(objectMapper.writeValueAsString(headers))
			.requestBody(objectMapper.writeValueAsString(requestBody))
			.build();
	}

}
