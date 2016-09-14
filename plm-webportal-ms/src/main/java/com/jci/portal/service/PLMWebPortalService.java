package com.jci.portal.service;

import com.jci.portal.domain.MiscTableEntity;
import com.jci.portal.domain.PLMDetailsResponse;
import com.jci.portal.domain.PLMPayloadTableEntity;
import com.jci.portal.domain.PLMRequestParam;
import com.jci.portal.domain.PLMUserTableEntity;

public interface PLMWebPortalService {

	MiscTableEntity getMiscData();
	
	PLMDetailsResponse getPayloadDetails(PLMRequestParam reqParam);
	
	PLMUserTableEntity getUserData();
	
	String readBlobXML(String ecnNo);
	
	boolean updatePayloadProcess(PLMPayloadTableEntity payloadTableEntity);
}
