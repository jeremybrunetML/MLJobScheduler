package com.marklogic.scheduler.domain.config;

import lombok.Data;

@Data
public class SchedulerStepFlowStepConfiguration {
	private String stepNumber;
	private String sourceQuery;
	private String sourceQueryTemplate;
}
