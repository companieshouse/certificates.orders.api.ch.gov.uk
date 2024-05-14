package uk.gov.companieshouse.certificates.orders.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.companieshouse.certificates.orders.api.config.AbstractMongoConfig;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemCreate;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptionsRequest;
import uk.gov.companieshouse.certificates.orders.api.repository.CertificateItemRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale.STANDARD;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_AUTHORISED_USER_HEADER_NAME;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_AUTHORISED_USER_VALUE;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_IDENTITY_HEADER_NAME;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_IDENTITY_TYPE_HEADER_NAME;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_IDENTITY_TYPE_OAUTH2_VALUE;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_IDENTITY_VALUE;

/**
 * Unit/integration tests the {@link CertificateItemsController} class.
 */
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("feature-flags-enabled")
@Testcontainers
class CertificateItemsControllerIntegrationTest extends AbstractMongoConfig {

    private static final String EXPECTED_ITEM_ID = "CRT-123456-123456";
    private static final String COMPANY_NUMBER = "00006400";
    private static final String TOKEN_PERMISSION_VALUE = "user_orders=%s";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CertificateItemRepository repository;

    @BeforeAll
    static void setup() {
        mongoDBContainer.start();
    }

    @AfterEach
    void tearDown() {
        repository.findById(EXPECTED_ITEM_ID).ifPresent(repository::delete);
    }

    @Test
    @DisplayName("Create rejects missing X-Request-ID")
    void createCertificateItemRejectsMissingRequestId() throws Exception {

        // Given
        final CertificateItemCreate newCertificateItemCreate = createValidNewItem();

        // When and Then
        mockMvc.perform(post("/orderable/certificates")
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCertificateItemCreate)))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * Factory method that produces a DTO for a valid create item request payload.
     *
     * @return a valid item DTO
     */
    private CertificateItemCreate createValidNewItem() {
        final CertificateItemCreate newCertificateItemCreate = new CertificateItemCreate();
        newCertificateItemCreate.setCompanyNumber(COMPANY_NUMBER);
        final CertificateItemOptionsRequest options = new CertificateItemOptionsRequest();
        options.setDeliveryTimescale(STANDARD);
        newCertificateItemCreate.setItemOptions(options);
        newCertificateItemCreate.setQuantity(5);
        return newCertificateItemCreate;
    }
}
