/**
 * 
 */
package com.jci.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.ComponentScan;


/**
 * The Main Spring Boot Application class.<br>
 * <br>
 * 
 * The {@link EnableConfigServer} annotation defines that this application will
 * serve as the REST based API for providing external configuration. <br>
 * <br>
 * 
 * The external repository from where the configuration will be picked up is
 * defined in the {@linkplain application.yml} file.
 * 
 * @author jci
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigServer
public class Application {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		System.out.println("### Starting config.Application.main ####");
		SpringApplication.run(Application.class, args);
		System.out.println("### Ending config.Application.main ####");
	}
}
