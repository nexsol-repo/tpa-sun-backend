package com.nexsol.tpa.core.support;

public record DomainPageRequest(int page, int size) {
	public DomainPageRequest {
		if (page < 0)
			throw new IllegalArgumentException("페이지는 0보다 작을 수 없습니다.");
		if (size < 1)
			throw new IllegalArgumentException("페이지 크기는 1보다 작을 수 없습니다.");
	}

	public long getOffset() {
		return (long) page * size;
	}
}
