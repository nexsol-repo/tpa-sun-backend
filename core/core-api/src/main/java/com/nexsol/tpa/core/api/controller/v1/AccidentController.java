package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.AccidentReportRequest;
import com.nexsol.tpa.core.api.controller.v1.request.InsuranceSearchRequest;
import com.nexsol.tpa.core.api.controller.v1.response.AccidentReportDetailResponse;
import com.nexsol.tpa.core.api.controller.v1.response.AccidentReportListResponse;
import com.nexsol.tpa.core.api.controller.v1.response.AccidentReportResponse;
import com.nexsol.tpa.core.api.support.response.ApiResponse;
import com.nexsol.tpa.core.api.support.response.PageResponse;
import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/accident")
@RequiredArgsConstructor
public class AccidentController {

	private final AccidentReportService accidentReportService;

	private final FileService fileService;

	@PostMapping("/report")
	public ApiResponse<AccidentReportResponse> reportAccident(@AuthenticationPrincipal Long userId,
			@RequestBody @Valid AccidentReportRequest request) {
		NewAccidentReport newAccidentReport = request.toNewAccidentReport(userId);

		AccidentReport savedReport = accidentReportService.reportAccident(newAccidentReport);

		return ApiResponse.success(AccidentReportResponse.of(savedReport));
	}

	@GetMapping("/me")
	public ApiResponse<PageResponse<AccidentReportListResponse>> getMyList(@AuthenticationPrincipal Long userId,
			@ModelAttribute InsuranceSearchRequest request) {

		SortPage sortPage = request.toSortPage();

		PageResult<AccidentReport> result = accidentReportService.getList(userId, sortPage);

		List<AccidentReportListResponse> responseList = result.getContent()
			.stream()
			.map(AccidentReportListResponse::of)
			.toList();

		return ApiResponse.success(PageResponse.of(result, responseList));
	}

	@GetMapping("/{reportId}")
	public ApiResponse<AccidentReportDetailResponse> getDetail(@AuthenticationPrincipal Long userId,
			@PathVariable Long reportId) {

		AccidentReportDetail detail = accidentReportService.getDetail(userId, reportId);

		return ApiResponse.success(AccidentReportDetailResponse.of(detail, fileService::generatePresignedUrl));
	}

}
