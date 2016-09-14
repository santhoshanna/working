package com.jci.partbom.domain;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class MiscTableEntity extends TableServiceEntity {

	private Integer ErrorCount;
	private Integer ProcessedCount;

	public MiscTableEntity() {
	}

	public MiscTableEntity(String partitionKey, String rowKey) {
		this.partitionKey = partitionKey;
		this.rowKey = rowKey;
	}

	public Integer getErrorCount() {
		return ErrorCount;
	}

	public void setErrorCount(Integer errorCount) {
		ErrorCount = errorCount;
	}

	public Integer getProcessedCount() {
		return ProcessedCount;
	}

	public void setProcessedCount(Integer processedCount) {
		ProcessedCount = processedCount;
	}

}
