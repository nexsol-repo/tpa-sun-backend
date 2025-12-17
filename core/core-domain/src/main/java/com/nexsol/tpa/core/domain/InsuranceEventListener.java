package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InsuranceEventListener {

	private final InsuranceApplicationRepository insuranceApplicationRepository;

	@EventListener
	@Transactional
	public void handlePaymentCompleted(PaymentCompletedEvent event) {
		// 1. 청약서 조회
		InsuranceApplication application = insuranceApplicationRepository.findById(event.applicationId())
			.orElseThrow(() -> new CoreException(CoreErrorType.INSURANCE_NOT_FOUND_DATA));

		// 2. 상태 변경 (COMPLETED)
		// 도메인 객체 내에 changeStatus 같은 비즈니스 메서드를 사용하는 것이 좋습니다.
		InsuranceApplication completedApp = application.complete();

		// 3. 저장
		insuranceApplicationRepository.save(completedApp);
	}

}
