package com.nexsol.tpa.core.domain;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InsuranceEventListener {

	private final InsuranceApplicationReader applicationReader;

	private final InsuranceApplicationWriter applicationWriter;

	private final InsurancePremiumCalculator premiumCalculator;

	@EventListener
	@Transactional
	public void handlePaymentCompleted(PaymentCompletedEvent event) {
		// 1. 청약서 조회
		InsuranceApplication application = applicationReader.read(event.applicationId());

		// 2. 상태 변경 (COMPLETED)
		// 도메인 객체 내에 changeStatus 같은 비즈니스 메서드를 사용하는 것이 좋습니다.
		InsuranceApplication completedApp = application.complete();

		// 3. 저장
		applicationWriter.writer(completedApp);
	}

	@EventListener
	@Transactional
	public void handleConditionChanged(InsuranceConditionChangedEvent event) {
		// 1. 최신 청약 정보 조회
		InsuranceApplication application = applicationReader.read(event.applicationId());

		// 2. 산출 가능한 상태일 때만 계산기 도구 실행
		if (application.canCalculatePremium()) {
			// 산출 개념 수행
			PremiumQuote quote = premiumCalculator.calculate(application.plant(), application.condition());

			// 결과 영속화 (구현 레이어의 도구 사용)
			InsuranceApplication withQuote = application.toBuilder().quote(quote).build();
			applicationWriter.writer(withQuote);
		}
		else {
			// 용량이 0이거나 없는 경우, 기존 견적이 있다면 제거하여 데이터 무결성 유지
			if (application.quote() != null) {
				InsuranceApplication clearedApp = application.toBuilder().quote(null).build();
				applicationWriter.writer(clearedApp);
			}
		}
	}

}
