package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.domain.DocumentFile;
import com.nexsol.tpa.core.domain.FileService;
import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.nexsol.tpa.test.api.RestDocsUtils.requestPreprocessor;
import static com.nexsol.tpa.test.api.RestDocsUtils.responsePreprocessor;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.web.reactive.function.BodyInserters.fromMultipartData;

public class FileControllerTest extends RestDocsTest {

	private final FileService fileService = mock(FileService.class);

	@BeforeEach
	void setUp() {
		this.webTestClient = mockController(new FileController(fileService));
	}

	@Test
	@DisplayName("보험 증빙 서류 업로드 (PDF)")
	void uploadInsurance() {
		// given
		String originalFilename = "business_license.pdf";
		String contentType = "application/pdf";
		byte[] content = "dummy pdf content".getBytes();

		// Service Mocking
		DocumentFile mockFile = DocumentFile.builder()
			.fileKey("insurance/2025/uuid.pdf")
			.originalFileName(originalFilename)
			.size((long) content.length)
			.extension("pdf")
			.build();

		given(fileService.uploadInsurance(any(), eq(originalFilename), anyLong(), eq(contentType)))
			.willReturn(mockFile);

		// Multipart Body 생성
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("file", new ByteArrayResource(content))
			.header("Content-Disposition", "form-data; name=file; filename=" + originalFilename)
			.header("Content-Type", contentType);

		// when & then
		webTestClient.post()
			.uri("/v1/file/insurance")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.body(fromMultipartData(builder.build()))
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("files-upload-insurance", requestPreprocessor(), responsePreprocessor(),
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
	@DisplayName("전자 서명 이미지 업로드")
	void uploadSignature() {
		// given
		String originalFilename = "signature.png";
		String contentType = "image/png";
		byte[] content = "dummy image content".getBytes();

		// Service Mocking
		DocumentFile mockFile = DocumentFile.builder()
			.fileKey("signatures/2025/uuid.png")
			.originalFileName(originalFilename)
			.size((long) content.length)
			.extension("png")
			.build();

		given(fileService.uploadSignature(any(), eq(originalFilename), anyLong(), eq(contentType)))
			.willReturn(mockFile);

		// Multipart Body 생성
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("file", new ByteArrayResource(content))
			.header("Content-Disposition", "form-data; name=file; filename=" + originalFilename)
			.header("Content-Type", contentType);

		// when & then
		webTestClient.post()
			.uri("/v1/file/signature")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.body(fromMultipartData(builder.build()))
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("files-upload-signature", requestPreprocessor(), responsePreprocessor(),
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
		String fileKey = "insurance/2025/uuid.pdf";

		// Service Mocking (void 메서드는 doNothing 사용)
		doNothing().when(fileService).deleteFile(fileKey);

		// when & then
		webTestClient.delete()
			.uri(uriBuilder -> uriBuilder.path("/v1/file").queryParam("key", fileKey).build())
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("files-delete", requestPreprocessor(), responsePreprocessor(),
					queryParameters(parameterWithName("key").description("삭제할 파일의 식별자 Key (예: insurance/...)")),
					responseFields(fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS)"),
							fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 (없음)"),
							fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보"))));
	}

}