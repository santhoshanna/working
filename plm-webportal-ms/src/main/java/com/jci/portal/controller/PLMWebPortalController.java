package com.jci.portal.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.jci.portal.domain.MiscTableEntity;
import com.jci.portal.domain.PLMDetailsResponse;
import com.jci.portal.domain.PLMRequestParam;
import com.jci.portal.domain.SymixData;
import com.jci.portal.service.PLMWebPortalService;


@RestController
public class PLMWebPortalController {
	
	
	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	} 
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	PLMWebPortalService plmWebPortalService;
	@Autowired
	private DiscoveryClient discoveryClient;

	public List<ServiceInstance> serviceInstancesByApplicationName(String applicationName) {
		return this.discoveryClient.getInstances(applicationName);
	} 
	

	
	@RequestMapping(value="/getMiscRecords", method = RequestMethod.POST)
	@ResponseBody
	public MiscTableEntity getMiscRecords(){
		return plmWebPortalService.getMiscData();
	}
	
	@RequestMapping(value="/getPLMDetails", method = RequestMethod.POST)
	@ResponseBody
	public PLMDetailsResponse getPLMDetails(@RequestBody PLMRequestParam param){
		return plmWebPortalService.getPayloadDetails(param);
	}
	
	
	
	@RequestMapping(value = "/downloadXML", method = RequestMethod.GET, produces = "text/xml")
	@ResponseBody
	public ResponseEntity<FileSystemResource> downloadXML(@RequestParam("filename") String fileName,
			@RequestParam(value = "ecnnumber") String ecnNo, HttpServletResponse response) throws IOException {
		response.setContentType("text/xml");
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		File file = null;
		if (plmWebPortalService.readBlobXML(ecnNo) != "") {
			file = new File("output.xml");
		} else {
			return new ResponseEntity<FileSystemResource>(new FileSystemResource(file), HttpStatus.NO_CONTENT);
		}
		return ResponseEntity.ok().contentLength(file.length()).contentType(MediaType.parseMediaType("text/xml"))
				.body(new FileSystemResource(file));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/reprocessWebportalXML", method = RequestMethod.POST)
	@ResponseBody
	public boolean reprocessWebportalXML(HttpServletRequest request){
		boolean isXMLReprocessed = false;
		ObjectMapper mapper = new ObjectMapper();
		try{
			mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			SymixData symixData =  mapper.readValue(request.getParameter("data"), SymixData.class);
			for(int i=0;i<symixData.getSeries().size();i++){
				String xmlData = plmWebPortalService.readBlobXML(symixData.getSeries().get(i).getECNNumber());
				Map<String, String> reprocessingMap = new HashMap<String, String>();
				reprocessingMap.put("xml", xmlData);
				reprocessingMap.put("ecnno", symixData.getSeries().get(i).getECNNumber());
				reprocessingMap.put("transactionid", symixData.getSeries().get(i).getTransactionID());
				reprocessingMap.put("plant", symixData.getSeries().get(i).getPlant());
				reprocessingMap.put("description", symixData.getSeries().get(i).getECNDescription());
				reprocessingMap.put("type", symixData.getSeries().get(i).getECNType());
				reprocessingMap.put("createdby", symixData.getSeries().get(i).getCreatedBy());
				HttpEntity entity = new HttpEntity(reprocessingMap, new HttpHeaders());
				restTemplate.exchange("http://localhost:8765/plm-payloadprocess-ms/reprocessXML", HttpMethod.POST, entity, String.class); 
				boolean isPayloadUpdated = plmWebPortalService.updatePayloadProcess(symixData.getSeries().get(i));
			}
			isXMLReprocessed = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isXMLReprocessed;
	}
	
	
	
}
