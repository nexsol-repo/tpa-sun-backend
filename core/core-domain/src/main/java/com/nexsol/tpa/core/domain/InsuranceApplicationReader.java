package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.InsuranceStatus;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InsuranceApplicationReader {

	private final InsuranceApplicationRepository insuranceApplicationRepository;

	public InsuranceApplication read(Long applicationId) {
		return insuranceApplicationRepository.findById(applicationId)
			.orElseThrow(() -> new CoreException(CoreErrorType.INSURANCE_NOT_FOUND_DATA));
	}

	public List<InsuranceApplication> readAllByIds(List<Long> applicationIds) {
		if (applicationIds == null || applicationIds.isEmpty()) {
			return List.of();
		}
		// JPA의 findAllById는 내부적으로 WHERE ID IN (...) 쿼리를 생성합니다.
		return insuranceApplicationRepository.findAllById(applicationIds);
	}

	public PageResult<InsuranceApplication> readAll(Long userId, SortPage sortPage) {
		return insuranceApplicationRepository.findAllByUserId(userId, sortPage);
	}

	public List<InsuranceApplication> readAllCompleted(Long userId) {
		return insuranceApplicationRepository.findAllByUserIdAndStatus(userId, InsuranceStatus.COMPLETED);
	}

}
