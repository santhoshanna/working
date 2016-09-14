package com.jci.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PlmWebportalMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlmWebportalMsApplication.class, args);
	}
}
