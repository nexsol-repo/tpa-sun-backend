package com.nexsol.tpa.test.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Tag("restdocs")
@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsTest {

	protected MockMvc mockMvc;

	// 자식 클래스에서 테스트 대상 컨트롤러를 제공하도록 강제
	protected abstract Object initController();

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {
		Object controller = initController();

		this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
			.apply(documentationConfiguration(restDocumentation)) // REST Docs 설정
			.addFilters(new CharacterEncodingFilter("UTF-8", true)) // 한글 깨짐 방지
			.alwaysDo(print()) // 테스트 로그 출력
			.build();
	}

}