package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.support.SortPage;

public record InsuranceSearchRequest(Integer page, Integer size, String sort, String direction) {
	public SortPage toSortPage() {

		int pageNum = (this.page == null) ? 0 : this.page;
		int pageSize = (this.size == null) ? 10 : this.size;

		if (this.sort == null || this.sort.isBlank()) {
			return SortPage.of(pageNum, pageSize);
		}

		SortPage.Direction dir = "ASC".equalsIgnoreCase(this.direction) ? SortPage.Direction.ASC
				: SortPage.Direction.DESC;

		return SortPage.of(pageNum, pageSize, this.sort, dir);
	}
}