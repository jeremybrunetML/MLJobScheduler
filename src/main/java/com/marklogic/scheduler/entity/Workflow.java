package com.marklogic.scheduler.entity;

import lombok.Data;

/**
 * Workflow
 */
@Data
public class Workflow {
    String workflowId;
    String workflowName;
    String lastExecutionDate;
    String workflowStatus;
    int workflowStep;
}