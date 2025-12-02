package com.nexsol.tpa.support.mailer;

import com.nexsol.tpa.core.domain.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

	private final JavaMailSender javaMailSender;

	@Override
	public void send(String toEmail, String authCode) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(toEmail);
			message.setSubject("[TPA] 인증 번호 안내");
			message.setText("인증 번호는 [" + authCode + "] 입니다.\n5분 안에 입력해주세요.");

			javaMailSender.send(message);
			log.info("인증 메일 발송 성공: {}", toEmail);
		}
		catch (Exception e) {
			log.error("메일 발송 실패: {}", toEmail, e);
			throw new RuntimeException("메일 발송에 실패했습니다.", e);
		}
	}

}
