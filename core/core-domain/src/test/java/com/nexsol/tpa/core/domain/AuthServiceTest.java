package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.EmailVerifiedType;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.nexsol.tpa.core.domain.EmailVerificationFixture.aVerification;
import static com.nexsol.tpa.core.domain.UserFixture.aUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@InjectMocks
	private AuthService authService;

	@Mock
	private UserReader userReader;

	@Mock
	private EmailVerificationReader emailVerificationReader;

	@Mock
	private TokenIssuer tokenIssuer;

	@Mock
	private TokenReader tokenReader;

	@Mock
	private TokenRemover tokenRemover;

	@Mock
	private TokenAppender tokenAppender;

	@Test
	@DisplayName("로그인 성공: 인증 코드가 일치하면 토큰이 발급된다")
	void login_success() {
		// given
		String companyCode = "123-45-67890";
		String email = "test@nexsol.com";
		String code = "123456";

		User user = aUser().build();
		EmailVerification verification = aVerification().code(code).build();
		AuthToken expectedToken = new AuthToken("access", "refresh", 3600, 12000);

		given(userReader.read(companyCode, email)).willReturn(user);
		given(emailVerificationReader.read(email, EmailVerifiedType.SIGNIN)).willReturn(verification);
		given(tokenIssuer.issue(user.id(), user.email())).willReturn(expectedToken);

		// when
		AuthToken token = authService.signIn(companyCode, email, code);

		// then
		assertThat(token).isNotNull();
		assertThat(token.accessToken()).isEqualTo("access");

		ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
		verify(tokenAppender).append(captor.capture());

		RefreshToken savedRefreshToken = captor.getValue();
		assertThat(savedRefreshToken.userId()).isEqualTo(user.id());
		assertThat(savedRefreshToken.token()).isEqualTo("refresh");
	}

	@Test
	@DisplayName("로그인 실패: 인증 코드가 틀리면 예외 발생")
	void login_fail_code_mismatch() {
		// given
		String code = "123456";
		String wrongCode = "000000";

		given(userReader.read(any(), any())).willReturn(aUser().build());

		EmailVerification verification = aVerification().code(code).build();
		given(emailVerificationReader.read(any(), any())).willReturn(verification);

		// when & then
		assertThatThrownBy(() -> authService.signIn("122-22-2222", "email", wrongCode))
			.isInstanceOf(CoreException.class)
			.extracting("errorType")
			.isEqualTo(CoreErrorType.EMAIL_VERIFIED_INVALID);
	}

}
