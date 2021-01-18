package com.marklogic.scheduler.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "marklogic")
public class MarkLogicConfiguration {
	private String host;
	private String user;
	private String password;
	private boolean ssl;

}
