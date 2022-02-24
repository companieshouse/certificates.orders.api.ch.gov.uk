package uk.gov.companieshouse.certificates.orders.api.config;

import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.certificates.orders.api.environment.RequiredEnvironmentVariables;

import static java.util.Arrays.stream;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.gov.companieshouse.certificates.orders.api.environment.RequiredEnvironmentVariables.API_URL;
import static uk.gov.companieshouse.certificates.orders.api.environment.RequiredEnvironmentVariables.CHS_API_KEY;
import static uk.gov.companieshouse.certificates.orders.api.environment.RequiredEnvironmentVariables.ITEMS_DATABASE;
import static uk.gov.companieshouse.certificates.orders.api.environment.RequiredEnvironmentVariables.MONGODB_URL;

public class EnvironmentVariablesCheckTest {
    public EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    @DisplayName("Check returns true where all required environment variables are populated")
    void checkEnvironmentVariablesAllPresentReturnsTrue() {

        stream(RequiredEnvironmentVariables.values()).forEach(
                variable -> environmentVariables.set(variable.getName(), variable.getName()));

        assertTrue(EnvironmentVariableChecks.checkEnvironmentVariables());

        stream(RequiredEnvironmentVariables.values()).forEach(
                variable -> environmentVariables.clear(variable.getName()));
    }

    @Test
    @DisplayName("Check returns false if ITEMS_DATABASE is not populated")
    void checkEnvironmentVariablesItemsDatabaseMissingReturnsFalse() {
        checkEnvironmentVariableMissing(ITEMS_DATABASE);
    }

    @Test
    @DisplayName("Check returns false if MONGODB_URL is not populated")
    void checkEnvironmentVariablesMongoDbUrlMissingReturnsFalse() {
        checkEnvironmentVariableMissing(MONGODB_URL);
    }

    @Test
    @DisplayName("Check returns false if CHS_API_KEY is not populated")
    void checkEnvironmentVariablesChsApiKeyMissingReturnsFalse() {
        checkEnvironmentVariableMissing(CHS_API_KEY);
    }

    @Test
    @DisplayName("Check returns false if API_URL is not populated")
    void checkEnvironmentVariablesApiUrlMissingReturnsFalse() {
        checkEnvironmentVariableMissing(API_URL);
    }

    /**
     * Utility method that asserts that if the environment variable specified is not populated,
     * then {@link EnvironmentVariableChecks#checkEnvironmentVariables()} returns <code>false</code>.
     *
     * @param missingVariable the {@link RequiredEnvironmentVariables} value indicating the variable that is to be
     *                        left unpopulated for the test
     */
    private void checkEnvironmentVariableMissing(final RequiredEnvironmentVariables missingVariable) {
        stream(RequiredEnvironmentVariables.values()).forEach(
                variable -> {
                    if (variable != missingVariable) {
                        environmentVariables.set(variable.getName(), variable.getName());
                    }
                });
        assertFalse(EnvironmentVariableChecks.checkEnvironmentVariables());
        stream(RequiredEnvironmentVariables.values()).forEach(
                variable -> {
                    if (variable != missingVariable) {
                        environmentVariables.clear(variable.getName());
                    }
                });
    }
}
