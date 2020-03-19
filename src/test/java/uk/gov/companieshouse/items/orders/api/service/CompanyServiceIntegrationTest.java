package uk.gov.companieshouse.items.orders.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

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
    public void getCompanyNameSuccessfully () throws JsonProcessingException {

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

}
