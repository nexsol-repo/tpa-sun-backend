package com.nexsol.tpa.test.api;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@Tag("restdocs")
@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsTest {

    protected WebTestClient webTestClient;

    private RestDocumentationContextProvider restDocumentation;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        this.restDocumentation = restDocumentation;
    }

    protected WebTestClient mockController(Object controller) {
        // MockMvcWebTestClient를 사용해 서버 없이 Controller 테스트
        return MockMvcWebTestClient.bindToController(controller)
                .configureClient()
                .filter(documentationConfiguration(restDocumentation))
                .build();
    }
}