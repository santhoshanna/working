package com.jci.payloadprocess.service;

public interface PLMProcessPayloadService {

	public boolean processPayload(String completeXml, String ecnNo, String transactionId, String plant,
			String description, String type, String createdBy);

	public boolean hystrixCircuitBreaker();

}
