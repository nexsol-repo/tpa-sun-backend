package com.nexsol.tpa.core.api.support.response;

import com.nexsol.tpa.core.support.DomainPage;

import java.util.List;

public record PageResponse<T>(List<T> content, long totalElements, int totalPages, int currentPage, boolean hasNext) {

	public static <T> PageResponse<T> of(DomainPage<?> domainPage, List<T> content) {
		return new PageResponse<>(content, domainPage.totalElements(), domainPage.totalPages(),
				domainPage.currentPage(), domainPage.hasNext());
	}
}