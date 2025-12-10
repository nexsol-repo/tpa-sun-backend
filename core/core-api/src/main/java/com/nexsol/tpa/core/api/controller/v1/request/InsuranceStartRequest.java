package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.Agreement;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record InsuranceStartRequest(@NotNull Boolean re100Interest,

		// [필수] 개인정보 수집 이용 (진입 단계이므로 강제)
		@AssertTrue(message = "개인(신용)정보 수집 및 이용에 동의해야 합니다.") @NotNull Boolean personalInfoCollectionAgreed,

		// [필수] 제3자 제공
		@AssertTrue(message = "개인정보 제3자 제공에 동의해야 합니다.") @NotNull Boolean personalInfoThirdPartyAgreed,

		// [필수] 단체 규약
		@AssertTrue(message = "단체규약 및 가입 시 유의사항에 동의해야 합니다.") @NotNull Boolean groupRuleAgreed,

		// [선택] 마케팅 동의
		@NotNull Boolean marketingAgreed) {
	public Agreement toAgreementInfo() {
		return Agreement.create(re100Interest, personalInfoCollectionAgreed, personalInfoThirdPartyAgreed,
				groupRuleAgreed, marketingAgreed);
	}
}
