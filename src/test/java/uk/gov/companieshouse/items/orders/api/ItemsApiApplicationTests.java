package uk.gov.companieshouse.items.orders.api;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemsApiApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("Application context loads successfully")
	void contextLoads() {
		// No implementation required here to test that context loads.
	}

    @Test
    @DisplayName("Create rejects missing company number")
    void createCertificateItemRejectsMissingCompanyNumber() {

        // Given
        final CertificateItemDTO newCertificateItemDTO = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertInc(true);
        options.setCertShar(true);
        newCertificateItemDTO.setItemOptions(options);
        newCertificateItemDTO.setQuantity(5);

		// When and Then
		postBadCreateRequestAndExpectError(newCertificateItemDTO, "company_number: must not be null");
    }

	@Test
	@DisplayName("Create does not reject missing item costs")
	void createCertificateItemDoesNotRejectMissingItemCosts() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = new CertificateItemDTO();
		newCertificateItemDTO.setCompanyNumber("1234");
		final CertificateItemOptions options = new CertificateItemOptions();
		options.setCertInc(true);
		options.setCertShar(true);
		newCertificateItemDTO.setItemOptions(options);
		newCertificateItemDTO.setQuantity(5);

		// When and Then
		webTestClient.post().uri("/orderable/certificates")
				.contentType(MediaType.APPLICATION_JSON)
				.body(fromValue(newCertificateItemDTO))
				.exchange()
				.expectStatus().isCreated();

	}

	@Test
	@DisplayName("Create rejects missing item options")
	void createCertificateItemRejectsMissingItemOptions() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = new CertificateItemDTO();
		newCertificateItemDTO.setCompanyNumber("1234");
		newCertificateItemDTO.setQuantity(5);

		// When and Then
		postBadCreateRequestAndExpectError(newCertificateItemDTO, "item_options: must not be null");
	}

	@Test
	@DisplayName("Create rejects missing quantity")
	void createCertificateItemRejectsMissingQuantity() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = new CertificateItemDTO();
		newCertificateItemDTO.setCompanyNumber("1234");
		final CertificateItemOptions options = new CertificateItemOptions();
		options.setCertInc(true);
		options.setCertShar(true);
		newCertificateItemDTO.setItemOptions(options);

		// When and Then
		postBadCreateRequestAndExpectError(newCertificateItemDTO, "quantity: must be greater than or equal to 1");
	}

	@Test
	@DisplayName("Create rejects read only item costs")
	void createCertificateItemRejectsReadOnlyItemCosts() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = new CertificateItemDTO();
		newCertificateItemDTO.setCompanyNumber("1234");
		final ItemCosts costs = new ItemCosts();
		costs.setDiscountApplied("1");
		costs.setIndividualItemCost("2");
		costs.setPostageCost("3");
		costs.setTotalCost("4");
		newCertificateItemDTO.setItemCosts(costs);
		final CertificateItemOptions options = new CertificateItemOptions();
		options.setCertInc(true);
		options.setCertShar(true);
		newCertificateItemDTO.setItemOptions(options);
		newCertificateItemDTO.setQuantity(5);

		// When and Then
		postBadCreateRequestAndExpectError(newCertificateItemDTO, "item_costs: must be null");
	}

	@Test
	@DisplayName("Create rejects read only description")
	void createCertificateItemRejectsReadOnlyDescription() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = new CertificateItemDTO();
		newCertificateItemDTO.setCompanyNumber("1234");
		newCertificateItemDTO.setDescription("description text");
		final CertificateItemOptions options = new CertificateItemOptions();
		options.setCertInc(true);
		options.setCertShar(true);
		newCertificateItemDTO.setItemOptions(options);
		newCertificateItemDTO.setQuantity(5);

		// When and Then
		postBadCreateRequestAndExpectError(newCertificateItemDTO, "description: must be null");
	}


	@Test
	@DisplayName("Create rejects read only description identifier")
	void createCertificateItemRejectsReadOnlyDescriptionIdentifier() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = new CertificateItemDTO();
		newCertificateItemDTO.setCompanyNumber("1234");
		newCertificateItemDTO.setDescriptionIdentifier("description identifier text");
		final CertificateItemOptions options = new CertificateItemOptions();
		options.setCertInc(true);
		options.setCertShar(true);
		newCertificateItemDTO.setItemOptions(options);
		newCertificateItemDTO.setQuantity(5);

		// When and Then
		postBadCreateRequestAndExpectError(newCertificateItemDTO, "description_identifier: must be null");
	}

	@Test
	@DisplayName("Create rejects read only description values")
	void createCertificateItemRejectsReadOnlyDescriptionValues() {

		// Given
		final CertificateItemDTO newCertificateItemDTO = new CertificateItemDTO();
		newCertificateItemDTO.setCompanyNumber("1234");
		newCertificateItemDTO.setDescriptionValues(new HashMap<>());
		final CertificateItemOptions options = new CertificateItemOptions();
		options.setCertInc(true);
		options.setCertShar(true);
		newCertificateItemDTO.setItemOptions(options);
		newCertificateItemDTO.setQuantity(5);

		// When and Then
		postBadCreateRequestAndExpectError(newCertificateItemDTO, "description_values: must be null");
	}

	/**
	 * Utility method that posts the create certificate item request, asserts a bad request status response and an
	 * expected validation error message.
	 * @param itemToCreate the DTO representing the certificate item to be requested
	 * @param expectedError expected validation error message
	 */
	private void postBadCreateRequestAndExpectError(final CertificateItemDTO itemToCreate, final String expectedError) {
		webTestClient.post().uri("/orderable/certificates")
				.contentType(MediaType.APPLICATION_JSON)
				.body(fromValue(itemToCreate))
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.status").isEqualTo("BAD_REQUEST")
				.jsonPath("$.errors[0]").isEqualTo(expectedError);
	}

}
