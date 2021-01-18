package com.marklogic.scheduler.orchestrator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.marklogic.client.DatabaseClient;
import com.marklogic.scheduler.domain.config.MarkLogicConfiguration;
import com.marklogic.scheduler.domain.config.SchedulerConfiguration;
import com.marklogic.scheduler.domain.config.SchedulerStepConfiguration;
import com.marklogic.scheduler.domain.config.SchedulerStepConfigurationOptions;
import com.marklogic.scheduler.domain.config.SchedulerStepFlowStepConfiguration;
import com.marklogic.scheduler.entity.Workflow;
import com.marklogic.scheduler.service.ChangeWorkflowStatusManager;
import com.marklogic.scheduler.service.WorkflowStateManager;
import com.marklogic.scheduler.service.utility.DataHubService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SchedulerManager {

    /*
     * JobExecutor jobExecutor = new JobExecutor(); List<CompletableFuture<String>>
     * jobExecutionMap = new ArrayList<CompletableFuture<String>>();
     */

    List<CompletableFuture<String>> jobExecutionMap = new ArrayList<CompletableFuture<String>>();

    @Autowired
    DatabaseClient databaseClient;

    @Autowired
    DataHubService dataHubService;

    @Autowired
    SchedulerConfiguration schedulerConfiguration;

    @Autowired
    MarkLogicConfiguration markLogicConfiguration;

    // This method is executed with 10000ms between 2 execution
    @Scheduled(fixedDelay = 10000)
    public void fixedRateSch() {

        // Specifications:
        //
        // A. Call a Marklogic service to get the workflows list to take care This
        // service getWorkflowStatus will check the prerequisites and update each
        // workflow state to pre-processing

        // B. Depending on the Workflow workflowStep

        // 2 -> run flow based on the yaml configuration



        // There is no parallelisation at the moment, the tasks are launched
        // sequentially.
        // In order to implement it, the JobExecutor.java can be used
        // Note that, a @Async method cannot access to @Autowired bean and they have to
        // be passed manually to the method.

        ////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        /////////// A . Call a service to gather Workflows /////////////////////
        ////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////

        List<Workflow> workflows = new ArrayList<Workflow>();
        String workflowServiceName = schedulerConfiguration.getWorkflowServiceName();

        try {

            WorkflowStateManager workflowStateManager = new WorkflowStateManager(databaseClient, workflowServiceName);
            workflows = workflowStateManager.checkWorkflowState();

        } catch (Exception e) {
            log.error("Error during the gather workflows step; error :" + e.getMessage());
        }

        if (workflows.size() > 0) {

            ////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////
            ////////////////// B . depending on the workflow status ////////////////
            ////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////
            for (Workflow workflow : workflows) {
                SchedulerStepConfiguration stepconfig = schedulerConfiguration
                        .getStepConfiguration(workflow.getWorkflowStep());

                try {
                    updateWorkflowStatus(workflow.getWorkflowId(), "inProgress", null);

                    Map<String, Object> options = new HashMap<String, Object>();
                    for (SchedulerStepConfigurationOptions option : stepconfig.getOptions()) {
                        options.put(option.getName(), option.getValue());
                    }

                    for (SchedulerStepFlowStepConfiguration option : stepconfig.getFlowStepConfig()) {
                        System.out.println("TEMPLATE : " + option.getSourceQueryTemplate());
                        if (option.getSourceQueryTemplate() != null) {
                            String sourceQuery = option.getSourceQueryTemplate();
                            option.setSourceQuery(sourceQuery);
                            System.out.println("SOURCEQUERY : " + option.getSourceQuery());
                        }
                    }

                    dataHubService.runDataHubFlow(stepconfig, options, workflow);

                    updateWorkflowStatus(workflow.getWorkflowId(), "waiting", workflow.getWorkflowStep() +1);
                } catch (Exception e) {
                    log.debug("Error during the step 2 flow TS harmonization all steps workflow id : "
                            + workflow.getWorkflowId() + " error :" + e.getMessage());
                }
            }

            /////////////////////////////////////////////////////////////////
            ///////////////// step #14 . Final Export ? /////////////////////
            /////////////////////////////////////////////////////////////////

        } else

        {
            log.info("No workflow to manage");
        }
        log.info("End of the scheduler iteration");
    }



    private void updateWorkflowStatus(String workflowId, String action, Integer newWorkflowStep) {

        ///////////////////////////////////////////////////////////
        //////// called after each step : Change ML State /////////
        ///////////////////////////////////////////////////////////

        String serviceName = schedulerConfiguration.getWorkflowStateServiceName();

        // Call a rest service
        ChangeWorkflowStatusManager changeWorkflowStatusManager = new ChangeWorkflowStatusManager(databaseClient,
        serviceName);
        changeWorkflowStatusManager.changeWorkflowStatus(workflowId, action, newWorkflowStep);

    }

}