package com.marklogic.scheduler.service.utility;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import com.marklogic.hub.DatabaseKind;
import com.marklogic.hub.flow.FlowInputs;
import com.marklogic.hub.flow.FlowRunner;
import com.marklogic.hub.flow.RunFlowResponse;
import com.marklogic.hub.flow.impl.FlowRunnerImpl;
import com.marklogic.hub.impl.HubConfigImpl;
import com.marklogic.scheduler.domain.config.DataHubConfiguration;
import com.marklogic.scheduler.domain.config.MarkLogicConfiguration;
import com.marklogic.scheduler.domain.config.SchedulerConfiguration;
import com.marklogic.scheduler.domain.config.SchedulerStepConfiguration;
import com.marklogic.scheduler.domain.config.SchedulerStepFlowStepConfiguration;
import com.marklogic.scheduler.entity.Workflow;

import org.apache.http.conn.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Data Hub Service allows for the processing of content already loaded into
 * the MarkLogic Database.
 * 
 * @author Jeremy Brunet
 */
@Component
public class DataHubService {

	@Autowired
	MarkLogicConfiguration markLogicConfiguration;

	@Autowired
	DataHubConfiguration dataHubConfiguration;

	@Autowired
	SchedulerConfiguration schedulerConfiguration;

	public void runDataHubFlow(SchedulerStepConfiguration stepconfig, Map<String, Object> options, Workflow workflow) {
		try {
			HubConfigImpl hubConfig = HubConfigImpl.withDefaultProperties();
			hubConfig.setHost(markLogicConfiguration.getHost());
			hubConfig.setMlUsername(markLogicConfiguration.getUser());
			hubConfig.setMlPassword(markLogicConfiguration.getPassword());
			hubConfig.setAuthMethod(DatabaseKind.FINAL, dataHubConfiguration.getFinalAuthMethod());
			hubConfig.setAuthMethod(DatabaseKind.STAGING, dataHubConfiguration.getStagingAuthMethod());
			hubConfig.setAuthMethod(DatabaseKind.JOB, dataHubConfiguration.getJobAuthMethod());
			hubConfig.setDbName(DatabaseKind.FINAL, dataHubConfiguration.getFinalDbName());
			hubConfig.setPort(DatabaseKind.FINAL, dataHubConfiguration.getFinalPort());
			hubConfig.setDbName(DatabaseKind.STAGING, dataHubConfiguration.getStagingDbName());
			hubConfig.setPort(DatabaseKind.STAGING, dataHubConfiguration.getStagingPort());
			hubConfig.setDbName(DatabaseKind.JOB, dataHubConfiguration.getJobDbName());
			hubConfig.setPort(DatabaseKind.JOB, dataHubConfiguration.getJobPort());
			hubConfig.setDbName(DatabaseKind.MODULES, dataHubConfiguration.getModuleDbName());
			hubConfig.setDbName(DatabaseKind.STAGING_TRIGGERS, dataHubConfiguration.getStagingTriggersDbName());
			hubConfig.setDbName(DatabaseKind.STAGING_SCHEMAS, dataHubConfiguration.getStagingSchemasDbName());
			hubConfig.setDbName(DatabaseKind.FINAL_TRIGGERS, dataHubConfiguration.getFinalTriggersDbName());
			hubConfig.setDbName(DatabaseKind.FINAL_SCHEMAS, dataHubConfiguration.getFinalSchemasDbName());

			TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
			
			SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
			.loadTrustMaterial(null, acceptingTrustStrategy)
			.build();

			if (dataHubConfiguration.isFinalSsl()) {
				hubConfig.setSimpleSsl(DatabaseKind.FINAL, true);
				hubConfig.setSslContext(DatabaseKind.FINAL, sslContext);
			}
			if (dataHubConfiguration.isStagingSsl()) {
				hubConfig.setSimpleSsl(DatabaseKind.STAGING, true);
				hubConfig.setSslContext(DatabaseKind.STAGING, sslContext);
			}
			if (dataHubConfiguration.isJobSsl()) {
				hubConfig.setSimpleSsl(DatabaseKind.JOB, true);
				hubConfig.setSslContext(DatabaseKind.JOB, sslContext);
			}

			String jobid = workflow.getWorkflowName() + "/" + new Date() +"/";
			jobid += java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(java.time.ZoneOffset.UTC)
					.format(java.time.Instant.now());

			for (SchedulerStepFlowStepConfiguration step : stepconfig.getFlowStepConfig()) {

				FlowRunner flowRunner = new FlowRunnerImpl(hubConfig);
				FlowInputs inputs = new FlowInputs(stepconfig.getDatahubFlowName());
				inputs.setJobId(jobid);

				Map<String, Object> stepOptions = new HashMap<>();

				if (step.getSourceQuery() != null) {
					options.put("sourceQuery", step.getSourceQuery());
				} else {
					if (options.containsKey("sourceQuery")) {
						options.remove("sourceQuery");
					}
				}

				inputs.setOptions(options);
				inputs.setStepConfig(stepOptions);
				inputs.setSteps(Arrays.asList(step.getStepNumber()));

				System.out
						.println("Running flow: " + stepconfig.getDatahubFlowName() + " step: " + step.getStepNumber());
				RunFlowResponse response = flowRunner.runFlow(inputs);
				flowRunner.awaitCompletion();
				System.out.println("Response: " + response);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
