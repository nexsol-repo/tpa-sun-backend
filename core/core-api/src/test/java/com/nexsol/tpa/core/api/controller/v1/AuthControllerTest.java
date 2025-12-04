package com.nexsol.tpa.core.api.controller.v1;


import com.nexsol.tpa.core.api.controller.v1.request.SignInRequest;
import com.nexsol.tpa.core.domain.AuthService;
import com.nexsol.tpa.core.domain.AuthToken;
import com.nexsol.tpa.core.domain.EmailVerifiedService;
import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.nexsol.tpa.test.api.RestDocsUtils.requestPreprocessor;
import static com.nexsol.tpa.test.api.RestDocsUtils.responsePreprocessor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class AuthControllerTest extends RestDocsTest {

    private final AuthService authService = mock(AuthService.class);
    private final EmailVerifiedService emailVerifiedService = mock(EmailVerifiedService.class);

    // [변경] WebTestClient는 내부 Codec이 자동으로 변환하므로 ObjectMapper 필드 제거 가능
    // 만약 날짜 포맷팅 등 커스텀 설정이 필요하다면 Jackson 3 JsonMapper를 빈으로 등록하거나 설정을 추가해야 함

    @BeforeEach
    void setUp() {
        // [변경] 부모 클래스(RestDocsTest)의 mockController 메서드를 사용해 webTestClient 초기화
        // 기존 initController() 오버라이드 방식 대신 명시적으로 호출하는 방식을 추천 (이전 답변 기준)
        this.webTestClient = mockController(new AuthController(authService, emailVerifiedService));
    }

    @Test
    @DisplayName("로그인 API 문서화")
    public void login() {
        // given
        AuthToken mockToken = AuthToken.builder()
                .accessToken("access-token-sample")
                .refreshToken("refresh-token-sample")
                .accessTokenExpiration(3600)
                .refreshTokenExpiration(1209600)
                .build();

        when(authService.signIn(any(), any(), any())).thenReturn(mockToken);

        SignInRequest request = new SignInRequest("123-45-67890", "test@nexsol.com", "123456");

        // when & then (WebTestClient Style)
        webTestClient.post().uri("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                // [변경] 객체를 바로 Body로 설정 (자동 JSON 변환)
                .bodyValue(request)
                .exchange() // 요청 실행

                // [검증]
                .expectStatus().isOk()
                .expectCookie().exists("refreshToken") // 쿠키 존재 여부 검증 (선택 사항)

                // [문서화]
                .expectBody()
                .consumeWith(document("auth-login",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        requestFields(
                                fieldWithPath("companyCode").type(JsonFieldType.STRING).description("사업자 번호"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일 주소"),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("인증 코드")
                        ),
                        responseCookies(
                                cookieWithName("refreshToken").description("리프레시 토큰 (HttpOnly, Secure)")
                        ),
                        responseFields(
                                fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보"),
                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                                fieldWithPath("data.expiresIn").type(JsonFieldType.NUMBER).description("만료 시간")
                        )
                ));
    }


}