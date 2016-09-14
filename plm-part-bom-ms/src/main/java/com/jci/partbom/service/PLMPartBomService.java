package com.jci.partbom.service;

import java.util.HashMap;

public interface PLMPartBomService {

	public boolean insertPayloadEntity(HashMap<String, Object> payloadMap);

	public boolean hystrixCircuitBreaker();

}
