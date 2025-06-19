package uk.gov.companieshouse.certificates.orders.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.env.Environment;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@ExtendWith(SystemStubsExtension.class)
public class CompanyServiceIntegrationTest {

    private static final String COMPANY_NUMBER = "00006400";
    private static final String EXPECTED_COMPANY_NAME = "THE GIRLS' DAY SCHOOL TRUST";
    public static final String EXPECTED_COMPANY_TYPE = "limited";


    private static final CompanyProfileApi COMPANY_PROFILE;

    private record Error(String type, String error) {
    }

    private record CompanyProfileApiErrorResponsePayload(List<Error> errors) {

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

    @SystemStub
    private EnvironmentVariables environmentVariables;

    @BeforeEach
    public void beforeEach(){
        final String wireMockPort = environment.getProperty("wiremock.server.port");
        environmentVariables.set("CHS_API_KEY", "MGQ1MGNlYmFkYzkxZTM2MzlkNGVmMzg4ZjgxMmEz");
        environmentVariables.set("API_URL", "http://localhost:" + wireMockPort);
        environmentVariables.set("PAYMENTS_API_URL", "http://localhost:" + wireMockPort);
        environmentVariables.set("DOCUMENT_API_LOCAL_URL", "http://localhost:" + wireMockPort);
        environmentVariables.set("ORACLE_QUERY_API_URL", "http://localhost:" + wireMockPort);
    }

    @Test
    void getCompanyProfileReturnsSuccessfully () throws JsonProcessingException, CompanyServiceException {
        final CompanyProfileResource expectedCompanyProfile = new CompanyProfileResource(
                "THE GIRLS' DAY SCHOOL TRUST", "limited", null);

        // Given
        givenThat(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/company/" + COMPANY_NUMBER))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(COMPANY_PROFILE))));

        CompanyProfileResource profileResource = serviceUnderTest.getCompanyProfile(COMPANY_NUMBER);
        // When and then
        assertThat(profileResource, is(expectedCompanyProfile));
    }

    @Test
    void getCompanyNameThrowsBadRequestExceptionForCompanyNotFound () throws JsonProcessingException {
        // Given
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
    void getCompanyProfileThrowsCompanyServiceExceptionForForConnectionFailure() {
        final String wireMockPort = environment.getProperty("wiremock.server.port");

        // Given
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
