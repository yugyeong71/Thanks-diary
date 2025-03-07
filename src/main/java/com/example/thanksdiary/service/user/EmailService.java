package com.example.thanksdiary.service.user;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.thanksdiary.common.exception.AlreadyDataException;
import com.example.thanksdiary.common.exception.BadRequestException;
import com.example.thanksdiary.common.exception.UnauthorizedException;
import com.example.thanksdiary.dao.user.UserRepository;
import com.example.thanksdiary.dto.common.SuccessResponse;
import com.example.thanksdiary.dto.user.request.SendVerificationCodeRequest;
import com.example.thanksdiary.dto.user.request.VerifyEmailCodeRequest;
import com.example.thanksdiary.service.common.RedisService;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

	private final UserRepository userRepository;

	private final JavaMailSender javaMailSender;

	private final RedisService redisService;

	/**
	 * 이메일 중복 확인
	 */
	@Transactional(readOnly = true)
	public void emailCheck(String email) {
		if (userRepository.findByEmail(email).isPresent()){
			throw new AlreadyDataException("이미 사용 중인 이메일입니다.");
		}
	}

	/**
	 * 이메일 인증 코드 요청
	 */
	@Transactional
	public SuccessResponse sendVerificationCode(SendVerificationCodeRequest sendVerificationCodeRequest) {
		try {
			String emailCode = createVerificationCode();
			MimeMessage message = createMessage(sendVerificationCodeRequest.getEmail(), emailCode);

			if (redisService.existData(sendVerificationCodeRequest.getEmail())) { // 기존에 발급 받았던 인증 코드 삭제
				redisService.deleteData(sendVerificationCodeRequest.getEmail());
			}

			javaMailSender.send(message);

			redisService.setDataExpire(sendVerificationCodeRequest.getEmail(), emailCode, 5 * 60 * 1000L);

			return new SuccessResponse();
		} catch (MessagingException | UnsupportedEncodingException | MailException e) {
			log.error("메일 전송 실패: {}", e.getMessage(), e);
			throw new BadRequestException("메일 전송에 실패했습니다.");
		}
	}

	/**
	 * 메일 내용 작성
	 */
	public MimeMessage createMessage(String receiver, String emailCode) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = javaMailSender.createMimeMessage();

		message.addRecipients(Message.RecipientType.TO, receiver);
		message.setSubject("[감사일기] 인증 코드 안내");

		String content = "<div>"
			+ "인증 코드를 확인해주세요.<br><strong style=\"font-size: 30px;\">"
			+ emailCode
			+ "</strong><br>이메일 인증 절차에 따라 이메일 인증 코드를 발급해드립니다.<br>인증 코드는 이메일 발송 시점으로부터 5분 동안 유효합니다.</div>";

		message.setText(content, "utf-8", "html");
		message.setFrom(new InternetAddress("ThanksDiary", "ThanksDiary Official"));

		return message;
	}

	/**
	 * 이메일 인증 코드 생성
	 */
	public String createVerificationCode() {
		StringBuilder key = new StringBuilder();
		Random random = new Random();

		for (int index = 0; index < 6; index++) {
			int code = random.nextInt(10);
			key.append(code);
		}

		return key.toString();
	}

	/**
	 * 이메일 인증 코드 검증
	 */
	@Transactional(readOnly = true)
	public SuccessResponse verifyEmailCode(VerifyEmailCodeRequest verifyEmailCodeRequest) {
		String code = redisService.getData(verifyEmailCodeRequest.getEmail());

		if (code == null) {
			throw new UnauthorizedException("인증 코드가 만료되었습니다.");
		}

		if (code.equals(verifyEmailCodeRequest.getCode())) {
			return new SuccessResponse();
		} else {
			throw new UnauthorizedException("인증 코드가 일치하지 않습니다.");
		}
	}
}
