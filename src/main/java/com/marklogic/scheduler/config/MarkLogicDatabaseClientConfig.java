package com.marklogic.scheduler.config;

import com.marklogic.scheduler.domain.config.DataHubConfiguration;
import com.marklogic.scheduler.domain.config.MarkLogicConfiguration;
import com.marklogic.scheduler.domain.config.SchedulerConfiguration;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.ext.modulesloader.ssl.SimpleX509TrustManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.log4j.Log4j2;

/**
 * A reusable database client connection.
 * 
 * @author Matt Smith
 * @author Drew Wanczowski
 */
@Log4j2
@Configuration
class MarkLogicDatabaseClientConfig {

	private final MarkLogicConfiguration marklogicConfiguration;

	private final DataHubConfiguration dataHubConfiguration;


	@Autowired
	MarkLogicDatabaseClientConfig(MarkLogicConfiguration marklogicConfiguration,
			DataHubConfiguration dataHubConfiguration, SchedulerConfiguration schedulerConfiguration) {
		this.marklogicConfiguration = marklogicConfiguration;
		this.dataHubConfiguration = dataHubConfiguration;
	}

	@Bean
	public DatabaseClient getDatabaseClient() {

		log.info("Creating connection to MarkLogic at " + this.marklogicConfiguration.getHost() + ":"
				+ this.dataHubConfiguration.getFinalPort());

		DatabaseClientFactory.SecurityContext securityContext;

		if(this.marklogicConfiguration.isSsl()){
			securityContext = this.dataHubConfiguration.getFinalAuthMethod().equals("digest")
			? new DatabaseClientFactory.DigestAuthContext(this.marklogicConfiguration.getUser(),
					this.marklogicConfiguration.getPassword()).withSSLHostnameVerifier(SSLHostnameVerifier.ANY)
					.withSSLContext(SimpleX509TrustManager.newSSLContext(), new SimpleX509TrustManager())
			: new DatabaseClientFactory.BasicAuthContext(this.marklogicConfiguration.getUser(),
					this.marklogicConfiguration.getPassword()).withSSLHostnameVerifier(SSLHostnameVerifier.ANY)
					.withSSLContext(SimpleX509TrustManager.newSSLContext(), new SimpleX509TrustManager());
		}else{
			securityContext = this.dataHubConfiguration.getFinalAuthMethod().equals("digest")
			? new DatabaseClientFactory.DigestAuthContext(this.marklogicConfiguration.getUser(),
					this.marklogicConfiguration.getPassword())
			: new DatabaseClientFactory.BasicAuthContext(this.marklogicConfiguration.getUser(),
					this.marklogicConfiguration.getPassword());
		}

		
		return DatabaseClientFactory.newClient(this.marklogicConfiguration.getHost(),
				this.dataHubConfiguration.getFinalPort(), this.dataHubConfiguration.getFinalDbName(), securityContext,
				DatabaseClient.ConnectionType.GATEWAY);
	}

}
