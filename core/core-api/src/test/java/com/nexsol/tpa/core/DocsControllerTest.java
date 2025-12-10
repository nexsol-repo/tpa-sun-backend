package com.nexsol.tpa.core;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class DocsControllerTest extends RestDocsTest {

	@BeforeEach
	void init() {
		// RestDocsTest의 mockController 메서드를 활용하여 DocsController 연결
		// DocsController는 의존성이 없으므로 new로 바로 주입
		this.webTestClient = mockController(new DocsController());
	}

	@Test
	@DisplayName("공통 에러 코드 문서화")
	void common_error_codes() {
		// Enum을 순회하며 문서화 필드 정의 (Key가 에러코드, Value가 설명이 됨)
		List<FieldDescriptor> descriptors = Arrays.stream(CoreErrorType.values())
			.map(type -> fieldWithPath(type.name()) // Key (에러 코드)
				.type(JsonFieldType.STRING)
				.description(type.getMessage())) // Value (설명)
			.collect(Collectors.toList());

		webTestClient.get()
			.uri("/docs/error-codes")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("common-error-codes", // 스니펫 폴더명
					responseFields(descriptors)));
	}

}
