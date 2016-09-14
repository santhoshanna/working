package com.jci.plmacknowledgement;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.jci.plmacknowledgement.services.PLMACKMSService;

@EnableDiscoveryClient
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
@EnableHystrix
@EnableHystrixDashboard
@EnableCircuitBreaker
@RestController
public class PLMACKMSApplication {

	private static final Logger LOG = LoggerFactory.getLogger(PLMACKMSApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(PLMACKMSApplication.class, args);
		LOG.info("#####STARTING PLM ACK Microservice #####");
	}

	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private PLMACKMSService plmackService;

	@RequestMapping("/service-instances/{applicationName}")
	public List<ServiceInstance> serviceInstancesByApplicationName(@PathVariable String applicationName) {
		return this.discoveryClient.getInstances(applicationName);
	}

	@Scheduled(fixedDelay = 60000)
	@RequestMapping(value = "/sendAcknowledgementToPTC", method = RequestMethod.GET, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
	public void sendAcknowledgementToPTC() throws IOException {
		LOG.info("#####Starting PLMACKMSApplication.sendAcknowledgementToPTC #####");
		try {
			plmackService.readPayloadTableEntityList();
		} catch (Exception e) {
			LOG.error("Exception in PLMACKMSApplication.sendAcknowledgementToPTC", e);
			LOG.info("#####Ending PLMACKMSApplication.sendAcknowledgementToPTC #####");
		}
	}

}