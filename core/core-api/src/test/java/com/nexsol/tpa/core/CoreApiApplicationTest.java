package com.nexsol.tpa.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CoreApiApplicationTest extends ContextTest {

	@Test
	@DisplayName("Spring Context가 정상적으로 로드되어야 한다")
	void shouldBeLoadedContext() {
		// 이 테스트가 통과하면:
		// 1. 모든 Bean 설정(Component Scan)이 정상임
		// 2. application.yml 설정에 오타가 없음
		// 3. 모듈 간 의존성이 잘 연결됨
	}

}