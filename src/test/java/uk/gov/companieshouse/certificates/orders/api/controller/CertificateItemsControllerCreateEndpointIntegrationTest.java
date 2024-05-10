package uk.gov.companieshouse.certificates.orders.api.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItem;
import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;
import uk.gov.companieshouse.certificates.orders.api.repository.CertificateItemRepository;
import uk.gov.companieshouse.certificates.orders.api.service.CompanyService;
import uk.gov.companieshouse.certificates.orders.api.service.IdGeneratorService;
import wiremock.org.eclipse.jetty.http.HttpStatus;

import java.util.Optional;

import static java.util.Objects.isNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_AUTHORISED_USER_HEADER_NAME;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_AUTHORISED_USER_VALUE;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_IDENTITY_HEADER_NAME;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_IDENTITY_TYPE_HEADER_NAME;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_IDENTITY_TYPE_OAUTH2_VALUE;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_IDENTITY_VALUE;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.REQUEST_ID_HEADER_NAME;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.TOKEN_REQUEST_ID_VALUE;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(
        properties = """
    lp.certificate.orders.enabled=true
    llp.certificate.orders.enabled=true
    liquidated.company.certificate.enabled=true
    administrator.company.certificate.enabled=true
  """
)
class CertificateItemsControllerCreateEndpointIntegrationTest {

    private static final String CERTIFICATES_URL = "/orderable/certificates";
    private static final String EXPECTED_ITEM_ID = "CRT-123456-123456";
    private static final String TOKEN_PERMISSION_VALUE = "user_orders=%s";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService companyService;

    @MockBean
    private CompanyProfileResource companyProfileResource;

    @MockBean
    private IdGeneratorService idGeneratorService;

    @Autowired
    private CertificateItemRepository repository;

    @AfterEach
    void tearDown() {
        repository.findById(EXPECTED_ITEM_ID).ifPresent(repository::delete);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("uk.gov.companieshouse.certificates.orders.api.controller." +
            "CertificateItemsControllerCreateEndpointTestData#createEndpointTestData")
    @DisplayName("Create certificate endpoint")
    void testCreateEndpoint(JsonRequestFixture requestFixture) throws Exception {
        Optional.ofNullable(requestFixture.getCompanyStatus())
                .ifPresent(status -> when(companyProfileResource.companyStatus()).thenReturn(status));
        Optional.ofNullable(requestFixture.getCompanyType())
                .ifPresent(type -> when(companyProfileResource.companyType()).thenReturn(type.getCompanyType()));
        if (isNull(requestFixture.getCompanyServiceException())) {
            when(companyService.getCompanyProfile(any())).thenReturn(companyProfileResource);
        } else {
            when(companyService.getCompanyProfile(any())).thenThrow(requestFixture.getCompanyServiceException());
        }
        when(companyProfileResource.companyName()).thenReturn("ACME LIMITED");
        when(idGeneratorService.autoGenerateId()).thenReturn(EXPECTED_ITEM_ID);
        mockMvc.perform(post(CERTIFICATES_URL)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestFixture.getRequestBody()))
                .andExpect(status().is(requestFixture.getExpectedResponseCode()))
                .andExpect(content().json(requestFixture.getExpectedResponseBody()))
                .andDo(MockMvcResultHandlers.print());

        if (!HttpStatus.isSuccess(requestFixture.getExpectedResponseCode())) {
            assertItemWasNotSaved();
        }
    }

    /**
     * Verifies that the item that could have been created by the create item POST request cannot in fact be retrieved
     * from the database.
     */
    private void assertItemWasNotSaved() {
        final Optional<CertificateItem> retrievedCertificateItem = repository.findById(CertificateItemsControllerCreateEndpointIntegrationTest.EXPECTED_ITEM_ID);
        assertThat(retrievedCertificateItem.isPresent(), is(false));
    }
}
