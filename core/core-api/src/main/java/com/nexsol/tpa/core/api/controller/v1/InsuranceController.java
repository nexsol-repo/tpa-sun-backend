package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.InsuranceConditionRequest;
import com.nexsol.tpa.core.api.controller.v1.request.InsurancePlantRequest;
import com.nexsol.tpa.core.api.controller.v1.request.InsuranceStartRequest;
import com.nexsol.tpa.core.api.controller.v1.response.InsuranceResponse;
import com.nexsol.tpa.core.api.support.response.ApiResponse;
import com.nexsol.tpa.core.domain.InsuranceApplication;
import com.nexsol.tpa.core.domain.InsuranceApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/insurance")
@RequiredArgsConstructor
public class InsuranceController {

	private final InsuranceApplicationService insuranceApplicationService;

	@GetMapping("/{applicationId}")
	public ApiResponse<InsuranceResponse> getApplication(@PathVariable Long applicationId) {

		// Service를 통해 조회 (건너뛰기 금지)
		InsuranceApplication app = insuranceApplicationService.getInsuranceApplication(applicationId);
		return ApiResponse.success(InsuranceResponse.of(app));
	}

	/**
	 * [Step 1] 신규 가입 시작 (약관 동의) - 무조건 새로운 청약서 생성
	 */
	@PostMapping("/start")
	public ApiResponse<InsuranceResponse> start(@AuthenticationPrincipal Long userId,
			@RequestBody @Valid InsuranceStartRequest request) {

		InsuranceApplication app = insuranceApplicationService.savePlantInit(userId, request.toAgreementInfo());
		return ApiResponse.success(InsuranceResponse.of(app));
	}

	/**
	 * [Step 2] 발전소 정보 저장 (임시저장)
	 */
	@PostMapping("/{applicationId}/plant")
	public ApiResponse<InsuranceResponse> savePlant(@PathVariable Long applicationId,
			@RequestBody InsurancePlantRequest request) { // @Valid 없음 (임시저장)

		InsuranceApplication app = insuranceApplicationService.savePlantInfo(applicationId, request.toInsurancePlant());
		return ApiResponse.success(InsuranceResponse.of(app));
	}

	/**
	 * [Step 3] 가입 조건 저장 (심사 & 보험료 산출 포함)
	 */
	@PostMapping("/{applicationId}/condition")
	public ApiResponse<InsuranceResponse> saveCondition(@PathVariable Long applicationId,
			@RequestBody InsuranceConditionRequest request) { // @Valid 없음 (임시저장)

		InsuranceApplication app = insuranceApplicationService.saveCondition(applicationId,
				request.toInsuranceCondition(), request.toInsuranceDocument());
		return ApiResponse.success(InsuranceResponse.of(app));
	}

	/**
	 * [Step 4] 최종 가입 완료 - 필수 값 검증은 Service 내부 validateForCompletion()에서 수행
	 */
	@PostMapping("/{applicationId}/complete")
	public ApiResponse<InsuranceResponse> complete(@PathVariable Long applicationId) {

		// TODO SUN: 서명 파일 처리 등이 있다면 MultipartFile 추가 필요
		InsuranceApplication app = insuranceApplicationService.completeApplication(applicationId);
		return ApiResponse.success(InsuranceResponse.of(app));
	}

}
