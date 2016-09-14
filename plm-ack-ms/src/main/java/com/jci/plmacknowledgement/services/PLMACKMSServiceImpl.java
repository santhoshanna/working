package com.jci.plmacknowledgement.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jci.plmacknowledgement.dao.PLMACKMSDao;

@Service
public class PLMACKMSServiceImpl implements PLMACKMSService {

	private static final Logger LOG = LoggerFactory.getLogger(PLMACKMSServiceImpl.class);

	@Autowired
	PLMACKMSDao plmackDao;

	@Override
	public void readPayloadTableEntityList() {
		LOG.info("#####Starting PLMAckMSServiceImpl.getAzureStorageEntity #####");
		plmackDao.readPayloadTableEntityList();
		LOG.info("#####Ending PLMAckMSServiceImpl.retrieveAzureTableEntity #####");
	}
}
