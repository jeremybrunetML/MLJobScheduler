package com.marklogic.scheduler.domain.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "marklogic.dhf")
public class DataHubConfiguration {
	private String finalAuthMethod;
	private String stagingAuthMethod;
	private String jobAuthMethod;
	private boolean finalSsl;
	private boolean stagingSsl;
	private boolean jobSsl;
	private String finalDbName;
	private int finalPort;
	private String stagingDbName;
	private int stagingPort;
	private String jobDbName;
	private int jobPort;
	private String moduleDbName;
	private String stagingTriggersDbName;
	private String stagingSchemasDbName;
	private String finalTriggersDbName;
	private String finalSchemasDbName;

}
