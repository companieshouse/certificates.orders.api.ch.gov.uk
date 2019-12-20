package uk.gov.companieshouse.items.orders.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.dto.PatchValidationCertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;
import uk.gov.companieshouse.items.orders.api.repository.CertificateItemRepository;
import uk.gov.companieshouse.items.orders.api.util.PatchMediaType;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.companieshouse.items.orders.api.util.TestConstants.REQUEST_ID_HEADER_NAME;
import static uk.gov.companieshouse.items.orders.api.util.TestConstants.TOKEN_REQUEST_ID_VALUE;

/**
 * Unit/integration tests the {@link CertificateItemsController} class.
 */
@AutoConfigureMockMvc
@SpringBootTest
class CertificateItemsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CertificateItemRepository repository;

    private static final String EXPECTED_ITEM_ID = "CHS00000000000000001";
    private static final String UPDATED_ITEM_ID  = "CHS00000000000000002";
    private static final int QUANTITY = 5;
    private static final int UPDATED_QUANTITY = 10;
    private static final boolean ORIGINAL_CERT_INC = true;
    private static final boolean UPDATED_CERT_INC = false;

    @AfterEach
    void tearDown() {
        repository.findById(EXPECTED_ITEM_ID).ifPresent(repository::delete);
    }

    @Test
    @DisplayName("Successfully creates certificate item")
    void createCertificateItemSuccessfullyCreatesCertificateItem() throws Exception {

        // Given
        final CertificateItemDTO newItem = new CertificateItemDTO();
        newItem.setCompanyNumber("1234");
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertInc(true);
        options.setCertShar(true);
        options.setCertDissLiq(false);
        newItem.setItemOptions(options);
        newItem.setQuantity(QUANTITY);

        final CertificateItemDTO expectedItem = new CertificateItemDTO();
        expectedItem.setId(EXPECTED_ITEM_ID);
        expectedItem.setCompanyNumber(newItem.getCompanyNumber());
        expectedItem.setKind("certificate");
        expectedItem.setDescriptionIdentifier("certificate");
        final ItemCosts costs = new ItemCosts();
        costs.setDiscountApplied("1");
        costs.setIndividualItemCost("2");
        costs.setPostageCost("3");
        costs.setTotalCost("4");
        expectedItem.setItemCosts(costs);
        expectedItem.setItemOptions(options);
        expectedItem.setPostalDelivery(true);
        expectedItem.setQuantity(QUANTITY);

        // When and Then
        mockMvc.perform(post("/certificates")
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedItem)))
                .andExpect(jsonPath("$.item_options.cert_inc", is(true)))
                .andExpect(jsonPath("$.item_options.cert_shar", is(true)))
                .andExpect(jsonPath("$.item_options.cert_dissliq", is(false)))
                .andExpect(jsonPath("$.postal_delivery", is(true)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemSavedCorrectly(EXPECTED_ITEM_ID);
    }

    @Test
    @DisplayName("Fails to create certificate item that fails validation")
    void createCertificateItemFailsToCreateCertificateItem() throws Exception {

        // Given
        final CertificateItemDTO newItem = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertInc(true);
        options.setCertShar(true);
        newItem.setItemOptions(options);
        newItem.setQuantity(QUANTITY);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList("company_number: must not be null"));

        // When and Then
        mockMvc.perform(post("/certificates")
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemWasNotSaved(EXPECTED_ITEM_ID);
    }

    @Test
    @DisplayName("Successfully gets a certificate item")
    void getCertificateItemSuccessfully() throws Exception {
        // Given
        // Create certificate item in database
        final CertificateItem newItem = new CertificateItem();
        newItem.setId(EXPECTED_ITEM_ID);
        newItem.setQuantity(QUANTITY);
        repository.save(newItem);

        final CertificateItemDTO expectedItem = new CertificateItemDTO();
        expectedItem.setQuantity(QUANTITY);
        expectedItem.setId(EXPECTED_ITEM_ID);

        // When and then
        mockMvc.perform(get("/certificates/"+EXPECTED_ITEM_ID)
            .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedItem)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Return not found when a certificate item does not exist")
    void getCertificateItemReturnsNotFound() throws Exception {
        final ApiError expectedValidationError =
                new ApiError(NOT_FOUND, singletonList("certificate resource not found"));

        // When and then
        mockMvc.perform(get("/certificates/CHS0")
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Successfully updates certificate item")
    void updateCertificateItemSuccessfullyUpdatesCertificateItem() throws Exception {

        // Given
        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertInc(ORIGINAL_CERT_INC);
        savedItem.setItemOptions(options);
        repository.save(savedItem);

        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        itemUpdate.setQuantity(UPDATED_QUANTITY);
        options.setCertInc(UPDATED_CERT_INC);
        itemUpdate.setItemOptions(options);

        // When and then
        mockMvc.perform(patch("/certificates/" + EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isNoContent())
                .andDo(MockMvcResultHandlers.print());

        // Then
        final Optional<CertificateItem> retrievedCertificateItem = repository.findById(EXPECTED_ITEM_ID);
        assertThat(retrievedCertificateItem.isPresent(), is(true));
        assertThat(retrievedCertificateItem.get().getId(), is(EXPECTED_ITEM_ID));
        assertThat(retrievedCertificateItem.get().getQuantity(), is(UPDATED_QUANTITY));
        assertThat(retrievedCertificateItem.get().getItemOptions().isCertInc(), is(UPDATED_CERT_INC));
    }

    @Test
    @DisplayName("Reports failure to find certificate item")
    void updateCertificateItemReportsFailureToFindItem() throws Exception {

        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        itemUpdate.setQuantity(UPDATED_QUANTITY);
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertInc(UPDATED_CERT_INC);
        itemUpdate.setItemOptions(options);

        // When and then
        mockMvc.perform(patch("/certificates/" + EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Rejects update request containing read only attribute value")
    void updateCertificateItemRejectsPatchWithReadOnlyAttributeValue() throws Exception {

        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        itemUpdate.setId(UPDATED_ITEM_ID);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList("id: must be null"));

        // When and then
        mockMvc.perform(patch("/certificates/" + EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * Verifies that the item assumed to have been created by the create item POST request can be retrieved
     * from the database using its expected ID value.
     * @param expectedItemId the expected ID of the newly created item
     */
    private void assertItemSavedCorrectly(final String expectedItemId) {
        final Optional<CertificateItem> retrievedCertificateItem = repository.findById(expectedItemId);
        assertThat(retrievedCertificateItem.isPresent(), is(true));
        assertThat(retrievedCertificateItem.get().getId(), is(expectedItemId));
    }

    /**
     * Verifies that the item that could have been created by the create item POST request cannot in fact be retrieved
     * from the database.
     * @param expectedItemId the expected ID of the newly created item
     */
    private void assertItemWasNotSaved(final String expectedItemId) {
        final Optional<CertificateItem> retrievedCertificateItem = repository.findById(expectedItemId);
        assertThat(retrievedCertificateItem.isPresent(), is(false));
    }

}
