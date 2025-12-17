package com.nexsol.tpa.core.api.controller.v1.request;

import jakarta.validation.constraints.NotBlank;

public record CheckPlantNameRequest(@NotBlank(message = "발전소명은 필수입니다.") String plantName,

		Long applicationId // 수정 시 본인 제외용 (없으면 null)
) {
}