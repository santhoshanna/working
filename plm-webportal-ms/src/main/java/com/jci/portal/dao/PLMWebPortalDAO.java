package com.jci.portal.dao;

import com.jci.portal.domain.MiscTableEntity;
import com.jci.portal.domain.PLMDetailsResponse;
import com.jci.portal.domain.PLMPayloadTableEntity;
import com.jci.portal.domain.PLMRequestParam;
import com.jci.portal.domain.PLMUserTableEntity;

public interface PLMWebPortalDAO {

	MiscTableEntity getMiscData();
	
	PLMDetailsResponse getPayloadDetails(PLMRequestParam reqParam);
	
	PLMUserTableEntity getUserData();
	
	String readBlobXML(String ecnNo);
	
	public boolean updatePayloadProcess(PLMPayloadTableEntity payloadTableEntity);
}
