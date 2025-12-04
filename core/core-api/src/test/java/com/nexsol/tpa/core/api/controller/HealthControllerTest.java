package com.nexsol.tpa.core.api.controller;

import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.nexsol.tpa.test.api.RestDocsUtils.requestPreprocessor;
import static com.nexsol.tpa.test.api.RestDocsUtils.responsePreprocessor;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@Tag("restdocs")
public class HealthControllerTest extends RestDocsTest {

    @BeforeEach
    void setUp() {
        this.webTestClient = mockController(new HealthController());
    }

    @Test
    @DisplayName("헬스 체크 API 문서화")
    void health() {
        webTestClient.get().uri("/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("health-check",
                        requestPreprocessor(),
                        responsePreprocessor()
                        // 요청/응답 필드가 없으므로 fields() 생략 가능
                ));
    }
}