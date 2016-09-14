package com.jci.portal.domain;

import java.util.List;

public class SymixData {

	private List<PLMPayloadTableEntity> series;
	private Pagination pagination;

	public List<PLMPayloadTableEntity> getSeries() {
		return series;
	}
	public void setSeries(List<PLMPayloadTableEntity> series) {
		this.series = series;
	}
	public Pagination getPagination() {
		return pagination;
	}
	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}	
}
