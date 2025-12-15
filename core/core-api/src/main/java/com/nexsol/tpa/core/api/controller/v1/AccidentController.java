package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.AccidentReportRequest;
import com.nexsol.tpa.core.api.controller.v1.response.AccidentReportResponse;
import com.nexsol.tpa.core.api.support.response.ApiResponse;
import com.nexsol.tpa.core.domain.AccidentReport;
import com.nexsol.tpa.core.domain.AccidentReportService;
import com.nexsol.tpa.core.domain.NewAccidentReport;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accident")
@RequiredArgsConstructor
public class AccidentController {

	private final AccidentReportService accidentReportService;

	@PostMapping("/report")
	public ApiResponse<AccidentReportResponse> reportAccident(@AuthenticationPrincipal Long userId,
			@RequestBody @Valid AccidentReportRequest request) {
		NewAccidentReport newAccidentReport = request.toNewAccidentReport(userId);

		AccidentReport savedReport = accidentReportService.reportAccident(newAccidentReport);

		return ApiResponse.success(AccidentReportResponse.of(savedReport));
	}

}
