package uk.gov.companieshouse.items.orders.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;

import java.util.HashMap;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.STANDARD;
import static uk.gov.companieshouse.items.orders.api.util.TestConstants.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemsApiApplicationTest {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	@DisplayName("Application context loads successfully")
	void contextLoads() {
		// No implementation required here to test that context loads.
	}

	@Test
	@DisplayName("Create rejects missing company name")
	void createCertificateItemRejectsMissingCompanyName() {
		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setCompanyName(null);

		// When and Then
		postBadCreateRequestAndExpectError(newCertificateItemDTO, "company_name: must not be null");
	}

    @Test
    @DisplayName("Create rejects missing company number")
    void createCertificateItemRejectsMissingCompanyNumber() {
        // Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setCompanyNumber(null);

		// When and Then
		postBadCreateRequestAndExpectError(newCertificateItemDTO, "company_number: must not be null");
    }

	@Test
	@DisplayName("Create does not reject missing item costs")
	void createCertificateItemDoesNotRejectMissingItemCosts() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();

		// When and Then
		webTestClient.post().uri("/orderable/certificates")
				.header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
				.header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
				.header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
				.header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(fromObject(newCertificateItemDTO))
				.exchange()
				.expectStatus().isCreated();

	}

	@Test
	@DisplayName("Create rejects missing item options")
	void createCertificateItemRejectsMissingItemOptions() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setItemOptions(null);

		// When and Then
		postBadCreateRequestAndExpectError(newCertificateItemDTO, "item_options: must not be null");
	}

	@Test
	@DisplayName("Create rejects missing quantity")
	void createCertificateItemRejectsMissingQuantity() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setQuantity(0); // 0 is default value when value not specified

		// When and Then
		postBadCreateRequestAndExpectError(newCertificateItemDTO, "quantity: must be greater than or equal to 1");
	}

	@Test
	@DisplayName("Create rejects read only item costs")
	void createCertificateItemRejectsReadOnlyItemCosts() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		final ItemCosts costs = new ItemCosts();
		costs.setDiscountApplied("1");
		costs.setIndividualItemCost("2");
		costs.setPostageCost("3");
		costs.setTotalCost("4");
		newCertificateItemDTO.setItemCosts(costs);


		// When and Then
		postBadCreateRequestAndExpectError(newCertificateItemDTO, "item_costs: must be null");
	}

	@Test
	@DisplayName("Create rejects read only description")
	void createCertificateItemRejectsReadOnlyDescription() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setDescription("description text");

		// When and Then
		postBadCreateRequestAndExpectError(newCertificateItemDTO, "description: must be null");
	}


	@Test
	@DisplayName("Create rejects read only description identifier")
	void createCertificateItemRejectsReadOnlyDescriptionIdentifier() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setDescriptionIdentifier("description identifier text");

		// When and Then
		postBadCreateRequestAndExpectError(newCertificateItemDTO, "description_identifier: must be null");
	}

	@Test
	@DisplayName("Create rejects read only description values")
	void createCertificateItemRejectsReadOnlyDescriptionValues() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setDescriptionValues(new HashMap<>());

		// When and Then
		postBadCreateRequestAndExpectError(newCertificateItemDTO, "description_values: must be null");
	}

	@Test
	@DisplayName("Create rejects read only id")
	void createCertificateItemRejectsReadOnlyId() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();
		newCertificateItemDTO.setId("TEST_ID");

		// When and Then
		postBadCreateRequestAndExpectError(newCertificateItemDTO, "id: must be null in a create item request");
	}

	@Test
	@DisplayName("Create rejects missing X-Request-ID")
	void createCertificateItemRejectsMissingRequestId() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = createValidNewItem();

		// When and Then
		webTestClient.post().uri("/orderable/certificates")
				.contentType(MediaType.APPLICATION_JSON)
				.header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
				.header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
				.header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
				.body(fromObject(newCertificateItemDTO))
				.exchange()
				.expectStatus().isBadRequest();

	}

	/**
	 * Utility method that posts the create certificate item request, asserts a bad request status response and an
	 * expected validation error message.
	 * @param itemToCreate the DTO representing the certificate item to be requested
	 * @param expectedError expected validation error message
	 */
	private void postBadCreateRequestAndExpectError(final CertificateItemDTO itemToCreate, final String expectedError) {
		webTestClient.post().uri("/orderable/certificates")
				.header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
				.header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
				.header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
				.header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(fromObject(itemToCreate))
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.status").isEqualTo("BAD_REQUEST")
				.jsonPath("$.errors[0]").isEqualTo(expectedError);
	}

	/**
	 * Factory method that produces a DTO for a valid create item request payload.
	 * @return a valid item DTO
	 */
	private CertificateItemDTO createValidNewItem() {
		final CertificateItemDTO newCertificateItemDTO = new CertificateItemDTO();
		newCertificateItemDTO.setCompanyName("Smith & Co");
		newCertificateItemDTO.setCompanyNumber("1234");
		final CertificateItemOptions options = new CertificateItemOptions();
		options.setDeliveryTimescale(STANDARD);
		newCertificateItemDTO.setItemOptions(options);
		newCertificateItemDTO.setQuantity(5);
		return newCertificateItemDTO;
	}

}
