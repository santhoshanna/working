package com.jci.portal.domain;

public class PLMRequestParam {

	private boolean firstRequest;
	private int size;
	private String erpName;
	private Pagination paginationParam;
	
	
	public boolean isFirstRequest() {
		return firstRequest;
	}
	public void setFirstRequest(boolean firstRequest) {
		this.firstRequest = firstRequest;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getErpName() {
		return erpName;
	}
	public void setErpName(String erpName) {
		this.erpName = erpName;
	}
	public Pagination getPaginationParam() {
		return paginationParam;
	}
	public void setPaginationParam(Pagination paginationParam) {
		this.paginationParam = paginationParam;
	}
}
