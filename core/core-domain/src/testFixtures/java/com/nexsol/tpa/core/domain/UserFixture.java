package com.nexsol.tpa.core.domain;

public class UserFixture {

	public static User.UserBuilder aUser() {
		return User.builder()
			.id(1L) // 테스트용 ID
			.companyCode("123-45-67890")
			.companyName("(주)넥솔")
			.name("테스트마스터")
			.phoneNumber("010-1234-5678")
			.applicantName("테스트신청자")
			.applicantEmail("applicant@nexsol.com")
			.applicantPhoneNumber("010-2222-2222");
	}

}
