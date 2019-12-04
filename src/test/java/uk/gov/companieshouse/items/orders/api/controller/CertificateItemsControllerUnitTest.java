package uk.gov.companieshouse.items.orders.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;
import uk.gov.companieshouse.items.orders.api.repository.CertificateItemRepository;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit/integration tests the {@link CertificateItemsController} class.
 */
@AutoConfigureMockMvc
@SpringBootTest
class CertificateItemsControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CertificateItemRepository repository;

    @Test
    @DisplayName("Create creates certificate item")
    void createCertificateItemCreatesCertificateItem() throws Exception {

        // Given
        final CertificateItemDTO newItem = new CertificateItemDTO();
        newItem.setCompanyNumber("1234");
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertInc(true);
        options.setCertShar(true);
        newItem.setItemOptions(options);
        newItem.setQuantity(5);

        final CertificateItemDTO expectedItem = new CertificateItemDTO();
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
        expectedItem.setQuantity(5);

        // When and Then
        final ResultActions outcome = mockMvc.perform(post("/certificates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.item_options.cert_inc", is(true)))
                .andExpect(jsonPath("$.item_options.cert_shar", is(true)))
                .andExpect(jsonPath("$.item_options.cert_dissliq", is(false)))
                .andExpect(jsonPath("$.postal_delivery", is(true)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        final CertificateItemDTO itemReturned = getItemReturned(outcome);
        final String newCertificateItemId = assertThatItemReturnedIsAsExpected(itemReturned, expectedItem);
        assertItemSavedCorrectly(newCertificateItemId);
    }

    /**
     * Verifies that the item assumed to have been created by the create item POST request can be retrieved
     * from the database.
     * @param returnedItemId the ID of the newly created item as returned in the REST response
     */
    private void assertItemSavedCorrectly(final String returnedItemId) {
        final Optional<CertificateItem> retrievedCertificateItem = repository.findById(returnedItemId);
        assertThat(retrievedCertificateItem.isPresent(), is(true));
        assertThat(retrievedCertificateItem.get().getId(), is(returnedItemId));
    }

    /**
     * Partially compares the content returned with that expected. Comparison excludes the ID as this will vary.
     * As a side effect this returns the actual ID found in the REST response for legibility of test code.
     * @param itemReturned the item returned in the REST response
     * @param itemExpected the item we expect to be returned in the REST response (with no ID)
     * @return the actual ID found in the rest response
     */
    private String assertThatItemReturnedIsAsExpected(final CertificateItemDTO itemReturned,
                                                      final CertificateItemDTO itemExpected) {
        final String newCertificateItemId = itemReturned.getId();
        itemReturned.setId(null);
        assertThat(itemReturned.equals(itemExpected), is(true));
        return newCertificateItemId;
    }

    /**
     * Extracts the item ({@link CertificateItemDTO}) from the JSON payload of the REST response.
     * @param outcome outcome of sending the create item REST request
     * @return the item ({@link CertificateItemDTO}) created from the REST response JSON payload
     * @throws JsonProcessingException should something unexpected happen
     * @throws UnsupportedEncodingException should something unexpected happen
     */
    private CertificateItemDTO getItemReturned(final ResultActions outcome)
            throws JsonProcessingException, UnsupportedEncodingException {
        final MvcResult result = outcome.andReturn();
        final String body = result.getResponse().getContentAsString();
        return objectMapper.readValue(body, CertificateItemDTO.class);
    }

}
