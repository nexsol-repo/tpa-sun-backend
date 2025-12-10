package com.nexsol.tpa.core;

import com.nexsol.tpa.core.error.CoreErrorType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DocsController {

	@GetMapping("/docs/error-codes")
	public Map<String, String> getErrorCodes() {
		return Arrays.stream(CoreErrorType.values())
			.collect(Collectors.toMap(type -> type.getCode().name(), // [수정] Key를 Enum 이름
																		// 대신 에러 코드(T1000
																		// 등)로 변경
					CoreErrorType::getMessage // Value: 설명
			));
	}

}