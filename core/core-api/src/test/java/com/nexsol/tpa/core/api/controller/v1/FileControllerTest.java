package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.domain.DocumentFile;
import com.nexsol.tpa.core.domain.FileService;
import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.nexsol.tpa.test.api.RestDocsUtils.requestPreprocessor;
import static com.nexsol.tpa.test.api.RestDocsUtils.responsePreprocessor;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;
import static org.springframework.web.reactive.function.BodyInserters.fromMultipartData;

public class FileControllerTest extends RestDocsTest {

	private final FileService fileService = mock(FileService.class);

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {
		// 1. @AuthenticationPrincipal 처리를 위한 커스텀 리졸버
		HandlerMethodArgumentResolver authPrincipalResolver = new HandlerMethodArgumentResolver() {
			@Override
			public boolean supportsParameter(MethodParameter parameter) {
				return parameter.getParameterType().equals(Long.class)
						&& parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
			}

			@Override
			public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
					NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
				return 1L; // 테스트용 User ID
			}
		};

		// 2. MockController 직접 빌드하여 부모 클래스(RestDocsTest)의 webTestClient 필드에 할당
		this.webTestClient = MockMvcWebTestClient.bindToController(new FileController(fileService))
			.customArgumentResolvers(authPrincipalResolver) // 리졸버 등록
			.configureClient()
			.filter(documentationConfiguration(restDocumentation)) // RestDocs 설정 적용
			.build();
	}

	@Test
	@DisplayName("보험 증빙 서류 업로드 (PDF)")
	void uploadInsurance() {
		// given
		Long userId = 1L;
		String originalFilename = "business_license.pdf";
		String contentType = "application/pdf";
		byte[] content = "dummy pdf content".getBytes();

		// Service Mocking
		DocumentFile mockFile = DocumentFile.builder()
			.fileKey("insurance/1/2025/uuid.pdf")
			.originalFileName(originalFilename)
			.size((long) content.length)
			.extension("pdf")
			.build();

		given(fileService.uploadInsurance(eq(userId), any(), eq(originalFilename), anyLong(), eq(contentType)))
			.willReturn(mockFile);

		// Multipart Body 생성
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("file", new ByteArrayResource(content))
			.header("Content-Disposition", "form-data; name=file; filename=" + originalFilename)
			.header("Content-Type", contentType);

		// when & then
		webTestClient.post()
			.uri("/v1/file/insurance")
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.body(fromMultipartData(builder.build()))
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("files-upload-insurance", requestPreprocessor(), responsePreprocessor(),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token (필수)")),
					requestParts(partWithName("file").description("업로드할 보험 증빙 파일 (PDF 권장)")),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS)"),
							fieldWithPath("data").type(JsonFieldType.OBJECT).description("업로드 된 파일 정보"),
							fieldWithPath("data.key").type(JsonFieldType.STRING).description("저장된 파일 식별자 (Key)"),
							fieldWithPath("data.originalFileName").type(JsonFieldType.STRING).description("원본 파일명"),
							fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("파일 크기 (Byte)"),
							fieldWithPath("data.extension").type(JsonFieldType.STRING).description("파일 확장자"),
							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보"))));
	}

	@Test
	@DisplayName("사고 접수 업로드")
	void uploadAccident() {
		// given
		Long userId = 1L;
		String originalFilename = "accident_picture.png";
		String contentType = "image/png";
		byte[] content = "dummy image content".getBytes();

		// Service Mocking
		DocumentFile mockFile = DocumentFile.builder()
			.fileKey("accident/1/2025/accident_picture.png")
			.originalFileName(originalFilename)
			.size((long) content.length)
			.extension("png")
			.build();

		given(fileService.uploadAccident(eq(userId), any(), eq(originalFilename), anyLong(), eq(contentType)))
			.willReturn(mockFile);

		// Multipart Body 생성
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("file", new ByteArrayResource(content))
			.header("Content-Disposition", "form-data; name=file; filename=" + originalFilename)
			.header("Content-Type", contentType);

		// when & then
		webTestClient.post()
			.uri("/v1/file/accident")
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.body(fromMultipartData(builder.build()))
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("files-upload-accident", requestPreprocessor(), responsePreprocessor(),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token (필수)")),
					requestParts(partWithName("file").description("업로드 현장사진 등등")),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS)"),
							fieldWithPath("data").type(JsonFieldType.OBJECT).description("업로드 된 파일 정보"),
							fieldWithPath("data.key").type(JsonFieldType.STRING).description("저장된 파일 식별자 (Key)"),
							fieldWithPath("data.originalFileName").type(JsonFieldType.STRING).description("원본 파일명"),
							fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("파일 크기 (Byte)"),
							fieldWithPath("data.extension").type(JsonFieldType.STRING).description("파일 확장자"),
							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보"))));
	}

	@Test
	@DisplayName("전자 서명 이미지 업로드")
	void uploadSignature() {
		// given
		Long userId = 1L;
		String originalFilename = "signature.png";
		String contentType = "image/png";
		byte[] content = "dummy image content".getBytes();

		// Service Mocking
		DocumentFile mockFile = DocumentFile.builder()
			.fileKey("signatures/1/2025/uuid.png")
			.originalFileName(originalFilename)
			.size((long) content.length)
			.extension("png")
			.build();

		given(fileService.uploadSignature(eq(userId), any(), eq(originalFilename), anyLong(), eq(contentType)))
			.willReturn(mockFile);

		// Multipart Body 생성
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("file", new ByteArrayResource(content))
			.header("Content-Disposition", "form-data; name=file; filename=" + originalFilename)
			.header("Content-Type", contentType);

		// when & then
		webTestClient.post()
			.uri("/v1/file/signature")
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.body(fromMultipartData(builder.build()))
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("files-upload-signature", requestPreprocessor(), responsePreprocessor(),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token (필수)")),
					requestParts(partWithName("file").description("업로드할 전자 서명 이미지 (PNG, JPG 등)")),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS)"),
							fieldWithPath("data").type(JsonFieldType.OBJECT).description("업로드 된 파일 정보"),
							fieldWithPath("data.key").type(JsonFieldType.STRING).description("저장된 파일 식별자 (Key)"),
							fieldWithPath("data.originalFileName").type(JsonFieldType.STRING).description("원본 파일명"),
							fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("파일 크기 (Byte)"),
							fieldWithPath("data.extension").type(JsonFieldType.STRING).description("파일 확장자"),
							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보"))));
	}

	@Test
	@DisplayName("파일 삭제")
	void deleteFile() {
		// given
		Long userId = 1L;
		String fileKey = "insurance/2025/uuid.pdf";

		// Service Mocking (void 메서드는 doNothing 사용)
		doNothing().when(fileService).deleteFile(userId, fileKey);

		// when & then
		webTestClient.delete()
			.uri(uriBuilder -> uriBuilder.path("/v1/file").queryParam("key", fileKey).build())
			.header("Authorization", "Bearer {ACCESS_TOKEN}")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("files-delete", requestPreprocessor(), responsePreprocessor(),

					queryParameters(parameterWithName("key").description("삭제할 파일의 식별자 Key (예: insurance/...)")),
					requestHeaders(headerWithName("Authorization").description("Bearer Access Token (필수)")),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS)"),
							fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 (없음)"),
							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보"))));
	}

}