package com.jci.partbom.service;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jci.partbom.dao.PLMPartBomDao;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class PLMPartBomServiceImpl implements PLMPartBomService {

	private static final Logger LOG = LoggerFactory.getLogger(PLMPartBomServiceImpl.class);

	@Autowired
	PLMPartBomDao plmPartBomDao;

	@Override
	public boolean insertPayloadEntity(HashMap<String, Object> map) {
		LOG.info("#####Starting PLMPartBomServiceImpl.insertPayloadEntity #####");
		// Inserting into azure tables via dao
		try {
			plmPartBomDao.insertPayloadEntity(map);
		} catch (Exception e) {
			LOG.error("#####Exception in PLMPartBomServiceImpl.insertPayloadEntity#####", e);
			LOG.info("#####Ending PLMPartBomServiceImpl.insertPayloadEntity#####");
			return false;
		}
		LOG.info("#####Ending PLMPartBomServiceImpl.insertPayloadEntity#####");
		return true;
	}

	@Override
	@HystrixCommand(fallbackMethod = "error")
	public boolean hystrixCircuitBreaker() {
		LOG.info("#####Starting PLMPartBomServiceImpl.hystrixCircuitBreaker #####");
		LOG.info("#####Ending PLMPartBomServiceImpl.hystrixCircuitBreaker #####");
		return true;
	}

	public void error() {
		LOG.info("#####Starting PLMPartBomServiceImpl.error #####");
		LOG.info("#####Ending PLMPartBomServiceImpl.error #####");
	}

}
