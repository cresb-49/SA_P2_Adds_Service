package com.sap.adds_service;

import com.sap.common_lib.security.web.SecurityConfig;
import com.sap.common_lib.security.web.WebClientConfig;
import com.sap.common_lib.security.web.filter.MicroServiceFilter;
import com.sap.common_lib.util.PublicEndpointUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@SpringBootApplication
//@EnableConfigurationProperties({ AppProperties.class, JwtProperties.class, HeadersProperties.class, PublicEndpointProperties.class })
@Import({ PublicEndpointUtil.class, SecurityConfig.class, MicroServiceFilter.class, WebClientConfig.class })
public class AddsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AddsServiceApplication.class, args);
	}

}
