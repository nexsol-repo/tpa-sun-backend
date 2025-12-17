package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InsuranceApplicationService {

	private final UserReader userReader;

	private final InsuranceApplicationReader applicationReader;

	private final InsuranceApplicationWriter applicationWriter;

	private final InsuranceApplicationValidator applicationValidator;

	private final InsuranceInspector insuranceInspector;

	private final InsuranceConditionPolicy insuranceConditionPolicy;

	private final InsurancePremiumCalculator premiumCalculator;

	public InsuranceApplication getInsuranceApplication(Long userId, Long applicationId) {
		InsuranceApplication application = applicationReader.read(applicationId);
		application.validateOwner(userId);
		return application;
	}

	public PageResult<InsuranceApplication> getList(Long userId, SortPage sortPage) {
		return applicationReader.readAll(userId, sortPage);
	}

	public List<InsuranceApplication> getCompletedList(Long userId) {
		return applicationReader.readAllCompleted(userId);
	}

	public InsuranceApplication saveInit(Long userId, Agreement agreement) {
		User user = userReader.read(userId);
		Applicant applicant = Applicant.toApplicant(user);
		// TODO SUN: 추후 바뀜 현재는 하드코딩해야함
		String applicationNumber = "2025-NO-TEST-" + System.currentTimeMillis();

		InsuranceApplication newApplication = InsuranceApplication.create(userId, applicationNumber, applicant,
				agreement);

		return applicationWriter.writer(newApplication);

	}

	public InsuranceApplication savePlantInfo(Long userId, Long applicationId, InsurancePlant plant) {
		InsuranceApplication application = applicationReader.read(applicationId);

		application.validateOwner(userId);

		String companyCode = application.applicant().companyCode();

		applicationValidator.checkDuplicatePlantName(companyCode, plant.name(), applicationId);

		InsuranceApplication updated = application.toBuilder().plant(plant).build();

		return applicationWriter.writer(updated);
	}

	public InsuranceApplication saveCondition(Long userId, Long applicationId, InsuranceCondition condition,
			InsuranceDocument documents) {
		InsuranceApplication application = applicationReader.read(applicationId);

		application.validateOwner(userId);
		insuranceInspector.inspectCondition(condition);
		insuranceInspector.inspectDocuments(documents);

		InsuranceCondition policyAppliedCondition = insuranceConditionPolicy.enforceDuration(condition);

		InsuranceApplication withCondition = application.toBuilder()
			.condition(policyAppliedCondition)
			.documents(documents)
			.build();

		if (withCondition.plant() != null) {

			PremiumQuote quote = premiumCalculator.calculate(withCondition.plant(), condition);

			withCondition = withCondition.toBuilder().quote(quote).build();
		}

		return applicationWriter.writer(withCondition);
	}

	public InsuranceApplication completeApplication(Long userId, Long applicationId, DocumentFile signatureFile) {
		InsuranceApplication application = applicationReader.read(applicationId);
		application.validateOwner(userId);

		InsuranceApplication completed = application.complete();

		InsuranceDocument signedDocs = application.documents().addSignature(signatureFile);

		completed = completed.toBuilder().documents(signedDocs).build();

		return applicationWriter.writer(completed);
	}

	public boolean isPlantNameDuplicated(Long userId, String plantName, Long applicationId) {

		User user = userReader.read(userId);
		String companyCode = user.companyCode();

		Long excludeId = (applicationId == null) ? -1L : applicationId;

		return applicationValidator.exists(companyCode, plantName, excludeId);
	}

}
