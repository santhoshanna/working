package com.jci.portal.domain;

public class PLMDetailsResponse {

	private String message;
	private PLMSucessData resultSet;
	private PLMGraphData graphData;
	private PLMFailureData errorData;
	private PLMUserTableEntity userData;
	private boolean error;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public PLMSucessData getResultSet() {
		return resultSet;
	}
	public void setResultSet(PLMSucessData resultSet) {
		this.resultSet = resultSet;
	}
	public PLMGraphData getGraphData() {
		return graphData;
	}
	public void setGraphData(PLMGraphData graphData) {
		this.graphData = graphData;
	}
	public PLMFailureData getErrorData() {
		return errorData;
	}
	public void setErrorData(PLMFailureData errorData) {
		this.errorData = errorData;
	}
	public PLMUserTableEntity getUserData() {
		return userData;
	}
	public void setUserData(PLMUserTableEntity userData) {
		this.userData = userData;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
}
