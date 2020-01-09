package uk.gov.companieshouse.items.orders.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.interceptor.UserAuthenticationInterceptor;
import uk.gov.companieshouse.items.orders.api.dto.PatchValidationCertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;
import uk.gov.companieshouse.items.orders.api.repository.CertificateItemRepository;
import uk.gov.companieshouse.items.orders.api.util.PatchMediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.companieshouse.items.orders.api.util.TestConstants.*;

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

    @MockBean
    private UserAuthenticationInterceptor userAuthenticationInterceptor;

    private static final String EXPECTED_ITEM_ID = "CHS00000000000000001";
    private static final String UPDATED_ITEM_ID  = "CHS00000000000000002";
    private static final int QUANTITY = 5;
    private static final int UPDATED_QUANTITY = 10;
    private static final int INVALID_QUANTITY = 0;
    private static final boolean ORIGINAL_CERT_INC = true;
    private static final boolean UPDATED_CERT_INC = false;
    private static final String ALTERNATIVE_CREATED_BY = "abc123";
    private static final String TOKEN_STRING = "TOKEN VALUE";
    static final Map<String, String> TOKEN_VALUES = new HashMap<>();
    private static final ItemCosts TOKEN_ITEM_COSTS = new ItemCosts();

    /**
     * Extends {@link PatchValidationCertificateItemDTO} to introduce a field that is unknown to the implementation.
     */
    private static class TestDTO extends PatchValidationCertificateItemDTO {
        private String unknownField;

        private TestDTO(String unknownField) {
            this.unknownField = unknownField;
        }

        public String getUnknownField() {
            return unknownField;
        }
    }

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
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
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
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
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
        newItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(newItem);

        final CertificateItemDTO expectedItem = new CertificateItemDTO();
        expectedItem.setQuantity(QUANTITY);
        expectedItem.setId(EXPECTED_ITEM_ID);

        // When and then
        mockMvc.perform(get("/certificates/"+EXPECTED_ITEM_ID)
            .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedItem)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Return not found when a certificate item does not exist")
    void getCertificateItemReturnsNotFound() throws Exception {
        // When and then
        mockMvc.perform(get("/certificates/CHS0")
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Return unauthorised if Eric headers are not present")
    void getCertificateItemReturnsUnauthorisedWhenEricHeadersAreNotPresent() throws Exception {
        // Given
        final CertificateItem newItem = new CertificateItem();
        newItem.setId(EXPECTED_ITEM_ID);
        newItem.setQuantity(QUANTITY);
        newItem.setUserId(ALTERNATIVE_CREATED_BY);
        repository.save(newItem);


        // When and then
        mockMvc.perform(get("/certificates/"+EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Return unauthorised if the user has not created the certificate")
    void getCertificateItemReturnsUnauthorisedIfUserDidNotCreateCertificate() throws Exception {
        // Given
        // Create certificate item in database
        final CertificateItem newItem = new CertificateItem();
        newItem.setId(EXPECTED_ITEM_ID);
        newItem.setQuantity(QUANTITY);
        newItem.setUserId(ALTERNATIVE_CREATED_BY);
        repository.save(newItem);


        // When and then
        mockMvc.perform(get("/certificates/"+EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Successfully updates certificate item")
    void updateCertificateItemSuccessfullyUpdatesCertificateItem() throws Exception {

        // Given
        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);

        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertInc(ORIGINAL_CERT_INC);
        savedItem.setItemOptions(options);
        repository.save(savedItem);

        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        itemUpdate.setQuantity(UPDATED_QUANTITY);
        options.setCertInc(UPDATED_CERT_INC);
        itemUpdate.setItemOptions(options);

        // When and then
        final ResultActions response = mockMvc.perform(patch("/certificates/" + EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // Then
        final Optional<CertificateItem> retrievedCertificateItem = repository.findById(EXPECTED_ITEM_ID);
        assertThat(retrievedCertificateItem.isPresent(), is(true));
        assertThat(retrievedCertificateItem.get().getId(), is(EXPECTED_ITEM_ID));
        assertThat(retrievedCertificateItem.get().getQuantity(), is(UPDATED_QUANTITY));
        assertThat(retrievedCertificateItem.get().getItemOptions().isCertInc(), is(UPDATED_CERT_INC));

        response.andExpect(content().json(objectMapper.writeValueAsString(retrievedCertificateItem)));
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
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
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

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList("id: must be null"));

        // When and then
        mockMvc.perform(patch("/certificates/" + EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Should not modify user_id when performing an update")
    void updateCertificateItemDoesNotModifyWhenPerformingAnUpdate() throws Exception {
        // Given
        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        // When and then
        mockMvc.perform(patch("/certificates/" + EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                .content("{\"company_number\":\"00006444\", \"user_id\":\"invalid\"}"))
                .andExpect(status().isBadRequest());

        final Optional<CertificateItem> foundItem = repository.findById(EXPECTED_ITEM_ID);
        assertEquals(ERIC_IDENTITY_VALUE, foundItem.get().getUserId());
    }

    @Test
    @DisplayName("Quantity must be greater than 0")
    void updateCertificateItemRejectsZeroQuantity() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        itemUpdate.setQuantity(INVALID_QUANTITY);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList("quantity: must be greater than or equal to 1"));

        // When and then
        mockMvc.perform(patch("/certificates/" + EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Unknown field is ignored")
    void updateCertificateItemIgnoresUnknownField() throws Exception {

        // Given
        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertInc(ORIGINAL_CERT_INC);
        savedItem.setItemOptions(options);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        final TestDTO itemUpdate = new TestDTO("Unknown field value");

        // When and then
        final ResultActions response = mockMvc.perform(patch("/certificates/" + EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // Then
        final Optional<CertificateItem> retrievedCertificateItem = repository.findById(EXPECTED_ITEM_ID);
        assertThat(retrievedCertificateItem.isPresent(), is(true));
        assertThat(retrievedCertificateItem.get().getId(), is(EXPECTED_ITEM_ID));
        assertThat(retrievedCertificateItem.get().getQuantity(), is(QUANTITY));
        assertThat(retrievedCertificateItem.get().getItemOptions().isCertInc(), is(ORIGINAL_CERT_INC));

        response.andExpect(content().json(objectMapper.writeValueAsString(retrievedCertificateItem)));
    }

    @Test
    @DisplayName("Multiple read only fields rejected")
    void updateCertificateItemRejectsMultipleReadOnlyFields() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        itemUpdate.setDescriptionValues(TOKEN_VALUES);
        itemUpdate.setItemCosts(TOKEN_ITEM_COSTS);
        itemUpdate.setKind(TOKEN_STRING);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        final ApiError expectedValidationErrors =
                new ApiError(BAD_REQUEST, asList("description_values: must be null",
                                                 "item_costs: must be null",
                                                 "kind: must be null"));

        // When and then
        mockMvc.perform(patch("/certificates/" + EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationErrors)))
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
