package uk.gov.companieshouse.certificates.orders.api.controller;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.companieshouse.certificates.orders.api.config.AbstractMongoConfig;
import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;
import uk.gov.companieshouse.certificates.orders.api.repository.CertificateItemRepository;
import uk.gov.companieshouse.certificates.orders.api.service.CompanyService;
import uk.gov.companieshouse.certificates.orders.api.service.IdGeneratorService;
import uk.gov.companieshouse.certificates.orders.api.util.PatchMediaType;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
@Testcontainers
class CertificateItemsControllerUpdateEndpointIntegrationTest extends AbstractMongoConfig {

    private static final String CERTIFICATES_URL = "/orderable/certificates/";
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

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeAll
    static void setup() {
        mongoDBContainer.start();
    }

    @AfterEach
    void tearDown() {
        repository.findById(EXPECTED_ITEM_ID).ifPresent(repository::delete);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("uk.gov.companieshouse.certificates.orders.api.controller." +
            "CertificateItemsControllerUpdateEndpointTestData#updateEndpointTestData")
    @DisplayName("Update certificate endpoint")
    void testUpdateEndpoint(JsonRequestFixture requestFixture) throws Exception {
        Optional.ofNullable(requestFixture.getSavedResource())
                .ifPresent(resource -> mongoTemplate.insert(Document.parse(resource), "certificates"));
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "update"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content(requestFixture.getRequestBody()))
                .andExpect(status().is(requestFixture.getExpectedResponseCode()))
                .andExpect(content().json(requestFixture.getExpectedResponseBody()))
                .andDo(MockMvcResultHandlers.print());
    }
}
