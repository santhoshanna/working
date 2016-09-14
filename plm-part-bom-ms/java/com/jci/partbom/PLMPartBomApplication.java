package com.jci.partbom;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.jci.partbom.service.PLMPartBomService;

@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
@EnableFeignClients
@RestController
@EnableHystrix
@EnableHystrixDashboard
@EnableCircuitBreaker
@Configuration
// @PropertySource("classpath:application.properties")
public class PLMPartBomApplication {
	public static void main(String[] args) {
		SpringApplication.run(PLMPartBomApplication.class, args);
	}

	private static final Logger LOG = LoggerFactory.getLogger(PLMPartBomApplication.class);

	@Autowired
	RestTemplate resttemplate;

	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired
	RestTemplate restTemplate;

	@Value("${apigee.part.url}")
	private String apigeePartUrl;
	@Value("${apigee.bom.url}")
	public String apigeeBomUrl;
	@Value("${apigee.part.parametername.erp}")
	private String erpParameter;
	@Value("${apigee.part.parametername.region}")
	public String regionParameter;
	@Value("${apigee.part.parametername.plant}")
	private String plantParameter;
	@Value("${azure.table.hashmap.ecnno}")
	private String ecnNumberKey;
	@Value("${azure.table.hashmap.erp}")
	private String erpKey;
	@Value("${azure.table.hashmap.region}")
	private String regionKey;
	@Value("${azure.table.hashmap.plant}")
	private String plantKey;
	@Value("${azure.table.hashmap.transactionid}")
	private String transactionIdKey;
	@Value("${azure.table.hashmap.isprocessed}")
	private String isprocessedKey;
	@Value("${azure.table.hashmap.iserrored}")
	private String iserroredKey;
	@Value("${azure.table.hashmap.message}")
	private String messageKey;
	@Value("${azure.table.hashmap.code}")
	private String codeKey;
	@Value("${azure.table.hashmap.status}")
	private String statusKey;
	@Value("${azure.table.hashmap.processeddate}")
	private String processedDateKey;
	@Value("${azure.table.hashmap.createddate}")
	private String createdDateKey;
	@Value("${azure.table.hashmap.processby}")
	private String processedByKey;
	@Value("${azure.table.hashmap.uiprocessed}")
	private String uiProcessedKey;
	@Value("${azure.table.hashmap.isacknowledged}")
	private String isAcknowledgedKey;
	@Value("${azure.table.hashmap.description}")
	private String descriptionKey;
	@Value("${azure.table.hashmap.type}")
	private String typeKey;
	@Value("${azure.table.hashmap.createdby}")
	private String createdbyKey;
	@Value("${plmpayload.hashmap.key.ecnno}")
	private String plmpayloadecnnoKey;
	@Value("${plmpayload.hashmap.key.transactionid}")
	private String plmpayloadtransactionidKey;
	@Value("${plmpayload.hashmap.key.erp}")
	private String plmpayloaderpKey;
	@Value("${plmpayload.hashmap.key.region}")
	private String plmpayloadregionKey;
	@Value("${plmpayload.hashmap.key.plant}")
	private String plmpayloadplantKey;
	@Value("${plmpayload.hashmap.key.description}")
	private String plmpayloaddescriptionKey;
	@Value("${plmpayload.hashmap.key.type}")
	private String plmpayloadtypeKey;
	@Value("${plmpayload.hashmap.key.createdby}")
	private String plmpayloadcreatedbyKey;
	@Value("${plmpayload.hashmap.key.part}")
	private String plmpayloadpartKey;
	@Value("${plmpayload.hashmap.key.bom}")
	private String plmpayloadbomKey;

	@RequestMapping("/service-instances/{applicationName}")
	public List<ServiceInstance> serviceInstancesByApplicationName(@PathVariable String applicationName) {
		return this.discoveryClient.getInstances(applicationName);
	}

	@Autowired
	private PLMPartBomService plmpartbomService;

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	@RequestMapping(value = "/processJSON", method = { RequestMethod.POST })
	public @ResponseBody ResponseEntity<String> processJSON(@RequestBody HashMap<String, Object> jsonPartBOMPayload)
			throws Exception {
		LOG.info("#####Starting PLMPartBomApplication.processJSON #####");
		try {
			LOG.info("Data reach at Bom ms from subcriber ms");
			LOG.info("===================PART=======================");
			LOG.info("Part: " + jsonPartBOMPayload.get("part"));
			LOG.info("===================BOM=======================");
			LOG.info("BOM: " + jsonPartBOMPayload.get("bom"));
			LOG.info("===================Others=======================");
			LOG.info("ecnNo: " + jsonPartBOMPayload.get("ecnno"));
			LOG.info("transactionId: " + jsonPartBOMPayload.get("transactionid"));
			LOG.info("erp: " + jsonPartBOMPayload.get("erp"));
			LOG.info("region: " + jsonPartBOMPayload.get("region"));
			LOG.info("plant: " + jsonPartBOMPayload.get("plant"));
			LOG.info("description: " + jsonPartBOMPayload.get("description"));
			LOG.info("type: " + jsonPartBOMPayload.get("type"));
			LOG.info("createdby: " + jsonPartBOMPayload.get("createdby"));

			Date date = new Date();
			DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

			LOG.info("Date   " + format.format(date));
			LOG.info("Apigee Part url   " + apigeePartUrl);
			LOG.info("Apigee Bom url    " + apigeeBomUrl);
			MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
			params.add(erpParameter, (String) jsonPartBOMPayload.get(plmpayloaderpKey));
			params.add(regionParameter, (String) jsonPartBOMPayload.get(plmpayloadregionKey));
			params.add(plantParameter, (String) jsonPartBOMPayload.get(plmpayloadplantKey));
			UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(apigeePartUrl).queryParams(params).build();
			URL apigeePartPostURL = new URL(uriComponents.toUriString());
			URL apigeeBomPostURL = null;
			HttpEntity entity = new HttpEntity(jsonPartBOMPayload.get(plmpayloadpartKey), new HttpHeaders());
			ResponseEntity<String> partResponse = null; // Remove this
		//	ResponseEntity<String> partResponse = restTemplate.exchange(apigeePartPostURL.toString(), HttpMethod.POST,
		//			entity, String.class);
			ResponseEntity<String> bomResponse = null;
			// if (partResponse!= null &&
			// partResponse.getStatusCode().is2xxSuccessful()) {
			if (true) {
				uriComponents = UriComponentsBuilder.fromHttpUrl(apigeeBomUrl).queryParams(params).build();
				apigeeBomPostURL = new URL(uriComponents.toUriString());
				// bomResponse =
				// restTemplate.postForEntity(apigeeBomPostURL.toString(),
				// jsonPartBOMPayload.get(plmpayloadbomKey),
				// null);
				// if (bomResponse!= null &&
				// bomResponse.getStatusCode().is2xxSuccessful()) {
				if (true) {
					jsonPartBOMPayload.put(isprocessedKey, 1);
					jsonPartBOMPayload.put(iserroredKey, 0);
					jsonPartBOMPayload.put(messageKey, "success from apigee");
					// jsonPartBOMPayload.put(messageKey,
					// bomResponse.getStatusCode().toString());
					jsonPartBOMPayload.put(codeKey, 200);
					jsonPartBOMPayload.put(statusKey, "success");
					jsonPartBOMPayload.put(processedDateKey, format.format(date));
					jsonPartBOMPayload.put(createdDateKey, format.format(date));
					jsonPartBOMPayload.put(processedByKey, "SYSTEM");
					jsonPartBOMPayload.put(ecnNumberKey, jsonPartBOMPayload.get(plmpayloadecnnoKey));
					jsonPartBOMPayload.put(transactionIdKey, jsonPartBOMPayload.get(plmpayloadtransactionidKey));
					jsonPartBOMPayload.put(erpKey, jsonPartBOMPayload.get(plmpayloaderpKey));
					jsonPartBOMPayload.put(regionKey, jsonPartBOMPayload.get(plmpayloadregionKey));
					jsonPartBOMPayload.put(plantKey, jsonPartBOMPayload.get(plmpayloadplantKey));
					jsonPartBOMPayload.put(descriptionKey, jsonPartBOMPayload.get(plmpayloaddescriptionKey));
					jsonPartBOMPayload.put(typeKey, jsonPartBOMPayload.get(plmpayloadtypeKey));
					jsonPartBOMPayload.put(uiProcessedKey, 0);
					jsonPartBOMPayload.put(isAcknowledgedKey, 0);
					jsonPartBOMPayload.put(createdbyKey, jsonPartBOMPayload.get(plmpayloadcreatedbyKey));

					// jsonPartBOMPayload.put("code",
					// bomResponse.getStatusCode().toString());
					// jsonPartBOMPayload.put("isprocessed", 1);
					// jsonPartBOMPayload.put("iserrored", 0);
					// jsonPartBOMPayload.put("message", "success from apigee");
					// jsonPartBOMPayload.put("code", 200);
					// jsonPartBOMPayload.put("status", "success");
					// jsonPartBOMPayload.put("processeddate",
					// format.format(date));
					// jsonPartBOMPayload.put("createddate",
					// format.format(date));
					// jsonPartBOMPayload.put("processedby", "SYSTEM");
					// jsonPartBOMPayload.put("ecnno",
					// jsonPartBOMPayload.get("ecnno"));
					// jsonPartBOMPayload.put("transactionid",
					// jsonPartBOMPayload.get("transactionid"));
					// jsonPartBOMPayload.put("erp",
					// jsonPartBOMPayload.get("erp"));
					// jsonPartBOMPayload.put("region",
					// jsonPartBOMPayload.get("region"));
					// jsonPartBOMPayload.put("plant",
					// jsonPartBOMPayload.get("plant"));
					// jsonPartBOMPayload.put("description",
					// jsonPartBOMPayload.get("description"));
					// jsonPartBOMPayload.put("type",
					// jsonPartBOMPayload.get("type"));
					// jsonPartBOMPayload.put("uiprocessed", 0);
					// jsonPartBOMPayload.put("isacknowledged", 0);
					// jsonPartBOMPayload.put("createdby",
					// jsonPartBOMPayload.get("createdby"));
					LOG.info("In loop 1");
				} else {

					jsonPartBOMPayload.put(isprocessedKey, 1);
					jsonPartBOMPayload.put(iserroredKey, 1);
					// jsonPartBOMPayload.put(messageKey,
					// bomResponse.getStatusCode().toString());
					// jsonPartBOMPayload.put(messageKey, "failure from
					// apigee");
					// jsonPartBOMPayload.put(codeKey, 200);
					if (bomResponse != null) {
						jsonPartBOMPayload.put(codeKey, bomResponse.getStatusCode().toString());
						jsonPartBOMPayload.put(messageKey, bomResponse.getBody());
					} else {
						jsonPartBOMPayload.put(codeKey, 304);
						jsonPartBOMPayload.put(messageKey, "Gateway timeout");
					}
					jsonPartBOMPayload.put(statusKey, "failure");
					jsonPartBOMPayload.put(processedDateKey, format.format(date));
					jsonPartBOMPayload.put(createdDateKey, format.format(date));
					jsonPartBOMPayload.put(processedByKey, "SYSTEM");
					jsonPartBOMPayload.put(ecnNumberKey, jsonPartBOMPayload.get(plmpayloadecnnoKey));
					jsonPartBOMPayload.put(transactionIdKey, jsonPartBOMPayload.get(plmpayloadtransactionidKey));
					jsonPartBOMPayload.put(erpKey, jsonPartBOMPayload.get(plmpayloaderpKey));
					jsonPartBOMPayload.put(regionKey, jsonPartBOMPayload.get(plmpayloadregionKey));
					jsonPartBOMPayload.put(plantKey, jsonPartBOMPayload.get(plmpayloadplantKey));
					jsonPartBOMPayload.put(descriptionKey, jsonPartBOMPayload.get(plmpayloaddescriptionKey));
					jsonPartBOMPayload.put(typeKey, jsonPartBOMPayload.get(plmpayloadtypeKey));
					jsonPartBOMPayload.put(uiProcessedKey, 0);
					jsonPartBOMPayload.put(isAcknowledgedKey, 0);
					jsonPartBOMPayload.put(createdbyKey, jsonPartBOMPayload.get(plmpayloadcreatedbyKey));
					LOG.info("In loop 2");
				}
			} else {
				jsonPartBOMPayload.put(isprocessedKey, 1);
				jsonPartBOMPayload.put(iserroredKey, 1);
				// jsonPartBOMPayload.put(messageKey,
				// partResponse.getStatusCode().toString());
				// jsonPartBOMPayload.put(messageKey, "failure from apigee");
				if (partResponse != null) {
					jsonPartBOMPayload.put(codeKey, partResponse.getStatusCode().toString());
					jsonPartBOMPayload.put(messageKey, partResponse.getBody());
				} else {
					jsonPartBOMPayload.put(codeKey, 304);
					jsonPartBOMPayload.put(messageKey, "Gateway timeout");
				}
				jsonPartBOMPayload.put(statusKey, "failure");
				jsonPartBOMPayload.put(processedDateKey, format.format(date));
				jsonPartBOMPayload.put(createdDateKey, format.format(date));
				jsonPartBOMPayload.put(processedByKey, "SYSTEM");
				jsonPartBOMPayload.put(ecnNumberKey, jsonPartBOMPayload.get(plmpayloadecnnoKey));
				jsonPartBOMPayload.put(transactionIdKey, jsonPartBOMPayload.get(plmpayloadtransactionidKey));
				jsonPartBOMPayload.put(erpKey, jsonPartBOMPayload.get(plmpayloaderpKey));
				jsonPartBOMPayload.put(regionKey, jsonPartBOMPayload.get(plmpayloadregionKey));
				jsonPartBOMPayload.put(plantKey, jsonPartBOMPayload.get(plmpayloadplantKey));
				jsonPartBOMPayload.put(descriptionKey, jsonPartBOMPayload.get(plmpayloaddescriptionKey));
				jsonPartBOMPayload.put(typeKey, jsonPartBOMPayload.get(plmpayloadtypeKey));
				jsonPartBOMPayload.put(uiProcessedKey, 0);
				jsonPartBOMPayload.put(isAcknowledgedKey, 0);
				jsonPartBOMPayload.put(createdbyKey, jsonPartBOMPayload.get(plmpayloadcreatedbyKey));
				LOG.info("In loop 3");
			}

			plmpartbomService.insertPayloadEntity(jsonPartBOMPayload);

		} catch (Exception e) {
			LOG.error("#####Exception in PLMPartBomApplication.processJSON #####", e);
			LOG.info("#####Ending PLMPartBomApplication.processJSON #####");
			return new ResponseEntity<String>("failure", HttpStatus.OK);
		}
		return new ResponseEntity<String>("success", HttpStatus.OK);

	}

	@RequestMapping(value = "/fallBack")
	@ResponseBody
	public ResponseEntity<String> hystrixCircuitBreaker() {
		LOG.info("#####Starting PLMPartBomApplication.hystrixCircuitBreaker #####");
		if (plmpartbomService.hystrixCircuitBreaker()) {
			LOG.info("#####Starting PLMPartBomApplication.hystrixCircuitBreaker #####");
			return new ResponseEntity<String>("fail", HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("fail", HttpStatus.OK);
		}
	}
}
