package uk.gov.companieshouse.items.orders.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.CoreMatchers.either;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Unit/integration tests the {@link CompanyService} class. Uses JUnit4 to take advantage of the
 * system-rules {@link EnvironmentVariables} class rule. The JUnit5 system-extensions equivalent does not
 * seem to have been released.
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureWireMock(port = 0)
public class CompanyServiceIntegrationTest {

    @ClassRule
    public static final EnvironmentVariables ENVIRONMENT_VARIABLES = new EnvironmentVariables();

    private static final String COMPANY_NUMBER = "00006400";
    private static final String EXPECTED_COMPANY_NAME = "THE GIRLS' DAY SCHOOL TRUST";

    private static final CompanyProfileApi COMPANY_PROFILE;

    private static class Error {
        private String type;
        private String error;

        private Error(String type, String error) {
            this.type = type;
            this.error = error;
        }

        public String getType() {
            return type;
        }

        public String getError() {
            return error;
        }
    }

    private static class CompanyProfileApiErrorResponsePayload {

        private List<Error> errors;

        private CompanyProfileApiErrorResponsePayload(List<Error> errors) {
            this.errors = errors;
        }

        public List<Error> getErrors() {
            return errors;
        }
    }

    private static final CompanyProfileApiErrorResponsePayload COMPANY_NOT_FOUND =
            new CompanyProfileApiErrorResponsePayload(singletonList(new Error("ch:service", "company-profile-not-found")));

    static {
        COMPANY_PROFILE = new CompanyProfileApi();
        COMPANY_PROFILE.setCompanyName(EXPECTED_COMPANY_NAME);
    }

    @Autowired
    private CompanyService serviceUnderTest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment environment;

    @Test
    public void getCompanyNameGetsNameSuccessfully () throws JsonProcessingException {

        final String wireMockPort = environment.getProperty("wiremock.server.port");

        // Given
        ENVIRONMENT_VARIABLES.set("CHS_API_KEY", "MGQ1MGNlYmFkYzkxZTM2MzlkNGVmMzg4ZjgxMmEz");
        ENVIRONMENT_VARIABLES.set("API_URL", "http://localhost:" + wireMockPort);
        givenThat(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/company/" + COMPANY_NUMBER))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(COMPANY_PROFILE))));

        // When and then
        assertThat(serviceUnderTest.getCompanyName(COMPANY_NUMBER), is(EXPECTED_COMPANY_NAME));
    }

    @Test
    public void getCompanyNameThrowsBadRequestExceptionForCompanyNotFound () throws JsonProcessingException {

        final String wireMockPort = environment.getProperty("wiremock.server.port");

        // Given
        ENVIRONMENT_VARIABLES.set("CHS_API_KEY", "MGQ1MGNlYmFkYzkxZTM2MzlkNGVmMzg4ZjgxMmEz");
        ENVIRONMENT_VARIABLES.set("API_URL", "http://localhost:" + wireMockPort);
        givenThat(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/company/" + COMPANY_NUMBER))
                .willReturn(badRequest()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(COMPANY_NOT_FOUND))));

        // When and then
        final ResponseStatusException exception =
                Assertions.assertThrows(ResponseStatusException.class,
                        () -> serviceUnderTest.getCompanyName(COMPANY_NUMBER));
        assertThat(exception.getStatus(), is(BAD_REQUEST));
        assertThat(exception.getReason(), is("Error getting company name for company number 00006400"));
    }

    // TODO Is there any way to make this perform consistently quickly?
    @Test
    public void getCompanyNameThrowsInternalServerErrorForForConnectionFailure() throws Exception {

        final int wrongPort = getWrongPort();

        // Given
        ENVIRONMENT_VARIABLES.set("CHS_API_KEY", "MGQ1MGNlYmFkYzkxZTM2MzlkNGVmMzg4ZjgxMmEz");
        ENVIRONMENT_VARIABLES.set("API_URL", "http://localhost:" + wrongPort);

        // When and then
        final ResponseStatusException exception =
                Assertions.assertThrows(ResponseStatusException.class,
                        () -> serviceUnderTest.getCompanyName(COMPANY_NUMBER));
        assertThat(exception.getStatus(), is(INTERNAL_SERVER_ERROR));
        final String expectedReason = "Error sending request to http://localhost:"
                + wrongPort + "/company/" + COMPANY_NUMBER;
        final String connectionRefused = expectedReason + ": Connection refused (Connection refused)";
        final String readTimedOut =  expectedReason + ": Read timed out";
        assertThat(exception.getReason(), either(is(connectionRefused)).or(is(readTimedOut)));
    }

    /**
     * Gets a port that is available and therefore not served by anything, including WireMock. Useful for getting a
     * connection refused error in a short length of time. TODO Is this consistent?
     * @return a local port not currently used by anything
     * @throws IOException should something unexpected happen
     */
    private int getWrongPort() throws IOException {
        final ServerSocket socket = new ServerSocket(0);
        return socket.getLocalPort();
    }

}
