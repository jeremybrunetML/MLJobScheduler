package com.marklogic.scheduler;

import java.util.concurrent.Executor;

import com.marklogic.hub.ApplicationConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@Configuration
@ComponentScan(value = "com.marklogic.scheduler")
public class SchedulerApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(SchedulerApplication.class, ApplicationConfig.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run();
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("Ml-SchedulerJobTask-");
		executor.initialize();
		return executor;
	}

}
