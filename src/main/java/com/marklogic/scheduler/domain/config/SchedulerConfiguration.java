package com.marklogic.scheduler.domain.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "scheduler")
public class SchedulerConfiguration {
	private String threadName;
	private String workflowServiceName;
	private String workflowStateServiceName;
	private List<SchedulerStepConfiguration> stepsConfiguration;

	public SchedulerStepConfiguration getStepConfiguration(int i) {
		for (SchedulerStepConfiguration stepConfiguration : stepsConfiguration) {
			if (stepConfiguration.getStepNumber() == i) {
				return stepConfiguration;
			}
		}
		return null;
	}
}
