package com.jci.portal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jci.portal.dao.PLMWebPortalDAO;
import com.jci.portal.domain.MiscTableEntity;
import com.jci.portal.domain.PLMDetailsResponse;
import com.jci.portal.domain.PLMPayloadTableEntity;
import com.jci.portal.domain.PLMRequestParam;
import com.jci.portal.domain.PLMUserTableEntity;

@Service
public class PLMWebPortalServiceImpl implements PLMWebPortalService {

	@Autowired
	PLMWebPortalDAO plmWebPortalDAO;

	@Override
	public MiscTableEntity getMiscData() {
		return plmWebPortalDAO.getMiscData();
	}
	
	@Override
	public PLMDetailsResponse getPayloadDetails(PLMRequestParam reqParam){
		return plmWebPortalDAO.getPayloadDetails(reqParam);
	}
	
	@Override
	public PLMUserTableEntity getUserData(){
		return plmWebPortalDAO.getUserData();
	}
	
	@Override
	public String readBlobXML(String ecnNo) {
		return plmWebPortalDAO.readBlobXML(ecnNo);
	}
	
	@Override
	public boolean updatePayloadProcess(PLMPayloadTableEntity payloadTableEntity){
		return plmWebPortalDAO.updatePayloadProcess(payloadTableEntity);
	}
}
