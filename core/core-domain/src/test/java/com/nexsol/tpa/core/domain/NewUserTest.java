package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.nexsol.tpa.core.domain.NewUserBuilder.aNewUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class NewUserTest {

	@Test
	@DisplayName("생성 성공: 필수 약관에 동의하고 사업자 번호가 있으면 객체가 생성된다.")
	void crate_success() {
		NewUser newUser = aNewUser().build();

		assertThat(newUser).isNotNull();
		assertThat(newUser.companyCode()).isEqualTo("123-45-67890");
	}

	@Test
	@DisplayName("업데이트 성공: 특정 값만 바꿔서 테스트")
	void create_custom() {
		NewUser newUser = aNewUser().withCompanyCode("999-00-11111").build();

		assertThat(newUser.companyCode()).isEqualTo("999-00-11111");
	}

	@Test
	@DisplayName("생성 실패: 필수 약관에 동의하지 않으면 예외가 발생한다")
	void create_fail_terms() {
		assertThatThrownBy(() -> NewUserBuilder.aNewUser().withTerms(false, false).build())
			.isInstanceOf(CoreException.class)
			.hasMessage("필수 약관에 동의해야 합니다.");
	}

}
