package com.marklogic.scheduler.domain.config;

import java.util.List;

import lombok.Data;

@Data
public class SchedulerStepConfiguration {
	private int stepNumber;
	private String serviceName;
	private String name;
	private List<SchedulerStepFlowStepConfiguration> flowStepConfig;
	private String datahubFlowName;
	private String datahubSourceQuery;
	private String user;
	private String password;
	private List<SchedulerStepConfigurationOptions> options;
}
