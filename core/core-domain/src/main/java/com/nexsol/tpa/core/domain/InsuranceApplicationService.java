package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InsuranceApplicationService {

	private final UserReader userReader;

	private final InsuranceApplicationReader applicationReader;

	private final InsuranceApplicationWriter applicationWriter;

	private final InsuranceInspector insuranceInspector;

	private final InsurancePremiumCalculator premiumCalculator;

	public InsuranceApplication getInsuranceApplication(Long userId, Long applicationId) {
		InsuranceApplication application = applicationReader.read(applicationId);
		application.validateOwner(userId);
		return application;
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

		InsuranceApplication updated = application.toBuilder().plant(plant).build();

		return applicationWriter.writer(updated);
	}

	public InsuranceApplication saveCondition(Long userId, Long applicationId, JoinCondition condition,
			InsuranceDocument documents) {
		InsuranceApplication application = applicationReader.read(applicationId);

		application.validateOwner(userId);
		insuranceInspector.inspectCondition(condition);
		insuranceInspector.inspectDocuments(documents);

		InsuranceApplication withCondition = application.toBuilder().condition(condition).documents(documents).build();

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

}
