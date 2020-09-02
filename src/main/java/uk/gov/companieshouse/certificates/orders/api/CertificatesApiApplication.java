package uk.gov.companieshouse.certificates.orders.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.gov.companieshouse.certificates.orders.api.environment.RequiredEnvironmentVariables;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.exception.EnvironmentVariableException;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import static uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants.APPLICATION_NAMESPACE;

@SpringBootApplication
public class CertificatesApiApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

	public static void main(String[] args) {
		if (checkEnvironmentVariables()) {
			SpringApplication.run(CertificatesApiApplication.class, args);
		}
	}

	/**
	 * Checks whether all required environment variables have defined values.
	 * @return whether all required environment variables have defined values (<code>true</code>), or not
	 * (<code>false</code>)
	 */
	static boolean checkEnvironmentVariables() {
		final EnvironmentReader environmentReader = new EnvironmentReaderImpl();
		boolean allVariablesPresent = true;
		LOGGER.info("Checking all required environment variables present");
		for (final RequiredEnvironmentVariables variable : RequiredEnvironmentVariables.values()) {
			try {
				environmentReader.getMandatoryString(variable.getName());
			} catch (EnvironmentVariableException eve) {
				allVariablesPresent = false;
				LOGGER.error(String.format("Required config item environment variable %s missing", variable.getName()));
			}
		}

		return allVariablesPresent;
	}

}
