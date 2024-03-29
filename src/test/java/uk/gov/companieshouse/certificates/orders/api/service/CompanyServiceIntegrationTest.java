package uk.gov.companieshouse.certificates.orders.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Fault;
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
import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
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
    public static final String EXPECTED_COMPANY_TYPE = "limited";


    private static final CompanyProfileApi COMPANY_PROFILE;

    private static class Error {
        private final String type;
        private final String error;

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

        private final List<Error> errors;

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
        COMPANY_PROFILE.setType(EXPECTED_COMPANY_TYPE);
    }

    @Autowired
    private CompanyService serviceUnderTest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment environment;

    @Test
    public void getCompanyProfileReturnsSuccessfully () throws JsonProcessingException, CompanyServiceException {

        final String wireMockPort = environment.getProperty("wiremock.server.port");
        final CompanyProfileResource expectedCompanyProfile = new CompanyProfileResource(
                "THE GIRLS' DAY SCHOOL TRUST", "limited", null);

        // Given
        ENVIRONMENT_VARIABLES.set("CHS_API_KEY", "MGQ1MGNlYmFkYzkxZTM2MzlkNGVmMzg4ZjgxMmEz");
        ENVIRONMENT_VARIABLES.set("API_URL", "http://localhost:" + wireMockPort);
        givenThat(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/company/" + COMPANY_NUMBER))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(COMPANY_PROFILE))));

        CompanyProfileResource profileResource = serviceUnderTest.getCompanyProfile(COMPANY_NUMBER);
        // When and then
        assertThat(profileResource, is(expectedCompanyProfile));
    }

    @Test
    public void getCompanyNameThrowsBadRequestExceptionForCompanyNotFound () throws JsonProcessingException {

        final String wireMockPort = environment.getProperty("wiremock.server.port");

        // Given
        ENVIRONMENT_VARIABLES.set("CHS_API_KEY", "MGQ1MGNlYmFkYzkxZTM2MzlkNGVmMzg4ZjgxMmEz");
        ENVIRONMENT_VARIABLES.set("API_URL", "http://localhost:" + wireMockPort);
        givenThat(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/company/" + COMPANY_NUMBER))
                .willReturn(notFound()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(COMPANY_NOT_FOUND))));

        // When and then
        final CompanyNotFoundException exception =
                Assertions.assertThrows(CompanyNotFoundException.class,
                        () -> serviceUnderTest.getCompanyProfile(COMPANY_NUMBER));
        assertThat(exception.getMessage(), is("Company profile not found company number 00006400"));
    }

    @Test
    public void getCompanyProfileThrowsCompanyServiceExceptionForForConnectionFailure() {

        final String wireMockPort = environment.getProperty("wiremock.server.port");

        // Given
        ENVIRONMENT_VARIABLES.set("CHS_API_KEY", "MGQ1MGNlYmFkYzkxZTM2MzlkNGVmMzg4ZjgxMmEz");
        ENVIRONMENT_VARIABLES.set("API_URL", "http://localhost:" + wireMockPort);
        givenThat(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/company/" + COMPANY_NUMBER))
                .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

        // When and then
        final CompanyServiceException exception =
                Assertions.assertThrows(CompanyServiceException.class,
                        () -> serviceUnderTest.getCompanyProfile(COMPANY_NUMBER));
        final String expectedReason = "Error sending request to http://localhost:"
                + wireMockPort + "/company/" + COMPANY_NUMBER + ": Connection reset";
        assertThat(exception.getMessage(), is(expectedReason));
    }

}
