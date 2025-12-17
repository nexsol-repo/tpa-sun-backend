package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentReader {

	private final PaymentRepository paymentRepository;

	public Payment read(Long paymentId) {
		return paymentRepository.findById(paymentId).orElseThrow(() -> new CoreException(CoreErrorType.NOT_FOUND_DATA));
	}

	public Payment readByApplicationId(Long applicationId) {
		return paymentRepository.findByApplicationId(applicationId).orElse(null); // 결제가
																					// 없을
																					// 수도
																					// 있음
																					// (작성
																					// 중인
																					// 경우)
	}

}
