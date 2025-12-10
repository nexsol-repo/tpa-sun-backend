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
			.collect(Collectors.toMap(CoreErrorType::name, // Key: JSON 필드명 (에러 코드)
					CoreErrorType::getMessage // Value: 설명
			));
	}

}