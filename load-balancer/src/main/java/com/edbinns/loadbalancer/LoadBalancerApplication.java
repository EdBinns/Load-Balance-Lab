package com.edbinns.loadbalancer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.edbinns.loadbalancer.config.LoadBalancerProperties;

@SpringBootApplication
@EnableConfigurationProperties(LoadBalancerProperties.class)
public class LoadBalancerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoadBalancerApplication.class, args);
	}

}
