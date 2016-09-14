package com.jci.portal.domain;

public class Pagination {
	
	private String nextRow;
	private String lastRow;
	private String nextPartition;
	private String lastPartition;
	
	public String getNextRow() {
		return nextRow;
	}
	public void setNextRow(String nextRow) {
		this.nextRow = nextRow;
	}
	public String getLastRow() {
		return lastRow;
	}
	public void setLastRow(String lastRow) {
		this.lastRow = lastRow;
	}
	public String getNextPartition() {
		return nextPartition;
	}
	public void setNextPartition(String nextPartition) {
		this.nextPartition = nextPartition;
	}
	public String getLastPartition() {
		return lastPartition;
	}
	public void setLastPartition(String lastPartition) {
		this.lastPartition = lastPartition;
	}
}
