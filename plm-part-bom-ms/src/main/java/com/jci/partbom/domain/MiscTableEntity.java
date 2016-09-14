package com.jci.partbom.domain;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class MiscTableEntity extends TableServiceEntity {

	private Integer errorCount;
	private Integer processedCount;

	public MiscTableEntity() {
	}

	public MiscTableEntity(String partitionKey, String rowKey) {
		this.partitionKey = partitionKey;
		this.rowKey = rowKey;
	}

	public Integer getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}

	public Integer getProcessedCount() {
		return processedCount;
	}

	public void setProcessedCount(Integer processedCount) {
		this.processedCount = processedCount;
	}
}
