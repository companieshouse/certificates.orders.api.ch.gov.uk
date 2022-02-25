package uk.gov.companieshouse.certificates.orders.api.config;

import uk.gov.companieshouse.certificates.orders.api.environment.RequiredEnvironmentVariables;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.exception.EnvironmentVariableException;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import static uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants.APPLICATION_NAMESPACE;

public class EnvironmentVariableChecks {
    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    private EnvironmentVariableChecks(){
    }

    /**
     * Checks whether all required environment variables have defined values.
     * @return whether all required environment variables have defined values (<code>true</code>), or not
     * (<code>false</code>)
     */
    public static boolean checkEnvironmentVariables() {
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
