package com.nexsol.tpa.core.support;

import java.util.Collections;
import java.util.List;

public record DomainPage<T>(List<T> content, long totalElements, int totalPages, int currentPage, Boolean hasNext) {

	public static <T> DomainPage<T> empty() {
		return new DomainPage<>(Collections.emptyList(), 0, 0, 0, false);
	}

	public static <T> DomainPage<T> empty(DomainPage<?> page) {
		return empty();
	}
}
