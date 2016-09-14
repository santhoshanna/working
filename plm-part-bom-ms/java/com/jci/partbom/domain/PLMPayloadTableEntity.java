package com.jci.partbom.domain;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class PLMPayloadTableEntity extends TableServiceEntity {

	public PLMPayloadTableEntity(String partitionKey, String rowKey) {
		this.partitionKey = partitionKey;
		this.rowKey = rowKey;
	}

	public PLMPayloadTableEntity() {
	}

	private Integer IsProcessed;
	private Integer IsErrored;
	private String Message;
	private Integer Code;
	private String Status;
	private String ECNNumber;
	private String TransactionID;
	private String Erp;
	private String Region;
	private String Plant;
	private String CreatedBy;
	private String CreatedDate;
	private String ProcessedDate;
	private String ProcessedBy;
	private Integer IsAcknowledged;
	private String AcknowledgementStatus;
	private Integer AcknowledgementCode;
	private String AcknowledgementMessage;
	private String AcknowledgementDate;
	private Integer UIProcessed;
	private String UIProcessedBy;
	private String UIProcessedDate;
	private String UIProcessingComments;
	private String ECNDescription;
	private String ECNType;

	public String getCreatedBy() {
		return CreatedBy;
	}

	public void setCreatedBy(String createdBy) {
		CreatedBy = createdBy;
	}

	public String getUIProcessingComments() {
		return UIProcessingComments;
	}

	public void setUIProcessingComments(String uIProcessingComments) {
		UIProcessingComments = uIProcessingComments;
	}

	public String getECNDescription() {
		return ECNDescription;
	}

	public void setECNDescription(String eCNDescription) {
		ECNDescription = eCNDescription;
	}

	public String getECNType() {
		return ECNType;
	}

	public void setECNType(String eCNType) {
		ECNType = eCNType;
	}

	public String getECNNumber() {
		return ECNNumber;
	}

	public void setECNNumber(String eCNNumber) {
		ECNNumber = eCNNumber;
	}

	public String getTransactionID() {
		return TransactionID;
	}

	public void setTransactionID(String transactionID) {
		TransactionID = transactionID;
	}

	public Integer getIsProcessed() {
		return IsProcessed;
	}

	public void setIsProcessed(Integer isProcessed) {
		IsProcessed = isProcessed;
	}

	public Integer getIsErrored() {
		return IsErrored;
	}

	public void setIsErrored(Integer isErrored) {
		IsErrored = isErrored;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public Integer getCode() {
		return Code;
	}

	public void setCode(Integer code) {
		Code = code;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getErp() {
		return Erp;
	}

	public void setErp(String erp) {
		Erp = erp;
	}

	public String getRegion() {
		return Region;
	}

	public void setRegion(String region) {
		Region = region;
	}

	public String getPlant() {
		return Plant;
	}

	public void setPlant(String plant) {
		Plant = plant;
	}

	public String getCreatedDate() {
		return CreatedDate;
	}

	public void setCreatedDate(String createdDate) {
		CreatedDate = createdDate;
	}

	public String getProcessedDate() {
		return ProcessedDate;
	}

	public void setProcessedDate(String processedDate) {
		ProcessedDate = processedDate;
	}

	public String getProcessedBy() {
		return ProcessedBy;
	}

	public void setProcessedBy(String processedBy) {
		ProcessedBy = processedBy;
	}

	public Integer getIsAcknowledged() {
		return IsAcknowledged;
	}

	public void setIsAcknowledged(Integer isAcknowledged) {
		IsAcknowledged = isAcknowledged;
	}

	public String getAcknowledgementStatus() {
		return AcknowledgementStatus;
	}

	public void setAcknowledgementStatus(String acknowledgementStatus) {
		AcknowledgementStatus = acknowledgementStatus;
	}

	public Integer getAcknowledgementCode() {
		return AcknowledgementCode;
	}

	public void setAcknowledgementCode(Integer acknowledgementCode) {
		AcknowledgementCode = acknowledgementCode;
	}

	public String getAcknowledgementMessage() {
		return AcknowledgementMessage;
	}

	public void setAcknowledgementMessage(String acknowledgementMessage) {
		AcknowledgementMessage = acknowledgementMessage;
	}

	public String getAcknowledgementDate() {
		return AcknowledgementDate;
	}

	public void setAcknowledgementDate(String acknowledgementDate) {
		AcknowledgementDate = acknowledgementDate;
	}

	public Integer getUIProcessed() {
		return UIProcessed;
	}

	public void setUIProcessed(Integer uIProcessed) {
		UIProcessed = uIProcessed;
	}

	public String getUIProcessedBy() {
		return UIProcessedBy;
	}

	public void setUIProcessedBy(String uIProcessedBy) {
		UIProcessedBy = uIProcessedBy;
	}

	public String getUIProcessedDate() {
		return UIProcessedDate;
	}

	public void setUIProcessedDate(String uIProcessedDate) {
		UIProcessedDate = uIProcessedDate;
	}

}
