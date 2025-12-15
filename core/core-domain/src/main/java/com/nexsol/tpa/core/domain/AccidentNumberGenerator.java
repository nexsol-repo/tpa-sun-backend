package com.nexsol.tpa.core.domain;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class AccidentNumberGenerator {

	private static final String PREFIX = "ACT";

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

	public String generate() {
		String datePart = LocalDate.now().format(DATE_FORMATTER);
		String randomPart = generateRandomString(6); // 6자리 랜덤 문자열

		// 예: ACT-20251215-X9Y2Z1
		return String.format("%s-%s-%s", PREFIX, datePart, randomPart);
	}

	private String generateRandomString(int length) {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder sb = new StringBuilder(length);
		ThreadLocalRandom random = ThreadLocalRandom.current();

		for (int i = 0; i < length; i++) {
			sb.append(characters.charAt(random.nextInt(characters.length())));
		}
		return sb.toString();
	}

}