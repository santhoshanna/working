/**
 * 
 */
package com.jci.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The Main Spring Boot Application class that starts the Eureka discovery
 * server since the application is annotated with {@link EnableEurekaServer}.
 * <br>
 * <br>
 * 
 * Note that all these annotations work in conjunction with properties defined
 * in the external configuration files specified by the config server.
 * 
 * @author jci
 */

@EnableEurekaServer
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		System.out.println("### Starting registry.Application.main ####");
		SpringApplication.run(Application.class, args);
		System.out.println("### Ending registry.Application.main ####");
	}
}
