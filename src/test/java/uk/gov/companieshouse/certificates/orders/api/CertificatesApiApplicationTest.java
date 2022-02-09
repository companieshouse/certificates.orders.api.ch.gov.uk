package uk.gov.companieshouse.certificates.orders.api;

import static java.util.Arrays.stream;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static uk.gov.companieshouse.certificates.orders.api.environment.RequiredEnvironmentVariables.API_URL;
import static uk.gov.companieshouse.certificates.orders.api.environment.RequiredEnvironmentVariables.CHS_API_KEY;
import static uk.gov.companieshouse.certificates.orders.api.environment.RequiredEnvironmentVariables.ITEMS_DATABASE;
import static uk.gov.companieshouse.certificates.orders.api.environment.RequiredEnvironmentVariables.MONGODB_URL;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale.STANDARD;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_AUTHORISED_USER_HEADER_NAME;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_AUTHORISED_USER_VALUE;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_IDENTITY_HEADER_NAME;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_IDENTITY_TYPE_HEADER_NAME;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_IDENTITY_TYPE_OAUTH2_VALUE;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_IDENTITY_VALUE;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.REQUEST_ID_HEADER_NAME;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.TOKEN_REQUEST_ID_VALUE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemCreate;
import uk.gov.companieshouse.certificates.orders.api.environment.RequiredEnvironmentVariables;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;
import uk.gov.companieshouse.certificates.orders.api.model.ItemCosts;
import uk.gov.companieshouse.certificates.orders.api.service.CompanyService;
import uk.gov.companieshouse.certificates.orders.api.service.CompanyServiceException;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CertificatesApiApplicationTest {

	private static final String COMPANY_NUMBER = "00006400";
	private static final String COMPANY_NOT_FOUND_ERROR =
			"Error getting company name for company number " + COMPANY_NUMBER;
	private static final String TOKEN_PERMISSION_VALUE = "user_orders=%s";

	@MockBean
	private CompanyService companyService;

	@MockBean
	private CompanyProfileResource companyProfileResource;

	@Autowired
	private WebTestClient webTestClient;

	@Rule
	public EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    @DisplayName("Create rejects missing company number")
    void createCertificateItemRejectsMissingCompanyNumber() {
        // Given
		final CertificateItemCreate newCertificateItemCreate = createValidNewItem();
		newCertificateItemCreate.setCompanyNumber(null);

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemCreate, "company-number-is-null");
    }

	@Test
	@DisplayName("Create does not reject missing item costs")
	void createCertificateItemDoesNotRejectMissingItemCosts() throws CompanyServiceException {

		// Given
		final CertificateItemCreate newCertificateItemCreate = createValidNewItem();
		when(companyProfileResource.getCompanyName()).thenReturn("name");
		when(companyProfileResource.getCompanyType()).thenReturn("ltd");
		when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
		when(companyService.getCompanyProfile("00006400")).thenReturn(companyProfileResource);

		// When and Then
		webTestClient.post().uri("/orderable/certificates")
				.header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
				.header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
				.header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
				.header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
				.header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(fromValue(newCertificateItemCreate))
				.exchange()
				.expectStatus().isCreated();

	}
	
    @Test
    @DisplayName("Create rejects wrong permission")
    void createCertificateUnauthorised() {

        // Given
        final CertificateItemCreate newCertificateItemCreate = createValidNewItem();

        // When and Then
        webTestClient.post().uri("/orderable/certificates")
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "read"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(newCertificateItemCreate))
                .exchange()
                .expectStatus().isUnauthorized();
    }

	@Test
	@DisplayName("Create rejects missing item options")
	void createCertificateItemRejectsMissingItemOptions() {

		// Given
		final CertificateItemCreate newCertificateItemCreate = createValidNewItem();
		newCertificateItemCreate.setItemOptions(null);

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemCreate, "item_options: must not be null");
	}

	@Test
	@DisplayName("Create rejects missing quantity")
	void createCertificateItemRejectsMissingQuantity() {

		// Given
		final CertificateItemCreate newCertificateItemCreate = createValidNewItem();
		newCertificateItemCreate.setQuantity(0); // 0 is default value when value not specified

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemCreate, "quantity: must be greater than or equal to 1");
	}

	@Test
	@DisplayName("Create rejects read only item costs")
	void createCertificateItemRejectsReadOnlyItemCosts() {

		// Given
		final CertificateItemCreate newCertificateItemCreate = createValidNewItem();
		final List<ItemCosts> costs = new ArrayList<>();
		final ItemCosts cost = new ItemCosts();
		cost.setDiscountApplied("1");
		cost.setItemCost("2");
		cost.setCalculatedCost("4");
		costs.add(cost);
		newCertificateItemCreate.setItemCosts(costs);


		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemCreate, "item_costs: must be null");
	}

	@Test
	@DisplayName("Create rejects read only description")
	void createCertificateItemRejectsReadOnlyDescription() {

		// Given
		final CertificateItemCreate newCertificateItemCreate = createValidNewItem();
		newCertificateItemCreate.setDescription("description text");

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemCreate, "description: must be null");
	}


	@Test
	@DisplayName("Create rejects read only description identifier")
	void createCertificateItemRejectsReadOnlyDescriptionIdentifier() {

		// Given
		final CertificateItemCreate newCertificateItemCreate = createValidNewItem();
		newCertificateItemCreate.setDescriptionIdentifier("description identifier text");

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemCreate, "description_identifier: must be null");
	}

	@Test
	@DisplayName("Create rejects read only description values")
	void createCertificateItemRejectsReadOnlyDescriptionValues() {

		// Given
		final CertificateItemCreate newCertificateItemCreate = createValidNewItem();
		newCertificateItemCreate.setDescriptionValues(new HashMap<>());

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemCreate, "description_values: must be null");
	}

	@Test
	@DisplayName("Create rejects read only id")
	void createCertificateItemRejectsReadOnlyId() throws CompanyServiceException {

		// Given
		when(companyService.getCompanyProfile(any())).thenReturn(companyProfileResource);
		when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
		when(companyProfileResource.getCompanyType()).thenReturn("ltd");

		final CertificateItemCreate newCertificateItemCreate = createValidNewItem();
		newCertificateItemCreate.setId("TEST_ID");

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemCreate, "id: must be null in a create item request");
	}

	@Test
	@DisplayName("Create rejects read only postage cost")
	void createCertificateItemRejectsReadOnlyPostageCost() throws CompanyServiceException {

		// Given
		when(companyService.getCompanyProfile(any())).thenReturn(companyProfileResource);
		when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);

		final CertificateItemCreate newCertificateItemCreate = createValidNewItem();
		newCertificateItemCreate.setPostageCost("0");

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemCreate, "postage_cost: must be null");
	}


	@Test
	@DisplayName("Create rejects read only total item cost")
	void createCertificateItemRejectsReadOnlyTotalItemCost() {

		// Given
		final CertificateItemCreate newCertificateItemCreate = createValidNewItem();
		newCertificateItemCreate.setTotalItemCost("100");

		// When and Then
		postCreateRequestAndExpectBadRequestResponse(newCertificateItemCreate, "total_item_cost: must be null");
	}

	@Test
	@DisplayName("Create rejects missing X-Request-ID")
	void createCertificateItemRejectsMissingRequestId() {

		// Given
		final CertificateItemCreate newCertificateItemCreate = createValidNewItem();

		// When and Then
		webTestClient.post().uri("/orderable/certificates")
				.contentType(MediaType.APPLICATION_JSON)
				.header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
				.header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
				.header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
				.header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
				.body(fromValue(newCertificateItemCreate))
				.exchange()
				.expectStatus().isBadRequest();

	}

	@Test
	@DisplayName("Create reports company not found as bad request")
	void createCertificateItemReportsCompanyNotFoundAsBadRequest() throws CompanyServiceException {
		// Given
		final CertificateItemCreate newCertificateItemCreate = createValidNewItem();
		when(companyService.getCompanyProfile(COMPANY_NUMBER)).
				thenThrow(new ResponseStatusException(BAD_REQUEST, COMPANY_NOT_FOUND_ERROR));

		// When and Then
		postCreateRequestAndExpectBadRequestResponseStatusError(newCertificateItemCreate, COMPANY_NOT_FOUND_ERROR);
	}

	@Test
	@DisplayName("Check returns true where all required environment variables are populated")
	void checkEnvironmentVariablesAllPresentReturnsTrue() {

		stream(RequiredEnvironmentVariables.values()).forEach(
				variable -> environmentVariables.set(variable.getName(), variable.getName()));

		assertTrue(CertificatesApiApplication.checkEnvironmentVariables());

		stream(RequiredEnvironmentVariables.values()).forEach(
				variable -> environmentVariables.clear(variable.getName()));
	}

	@Test
	@DisplayName("Check returns false if ITEMS_DATABASE is not populated")
	void checkEnvironmentVariablesItemsDatabaseMissingReturnsFalse() {
		checkEnvironmentVariableMissing(ITEMS_DATABASE);
	}

	@Test
	@DisplayName("Check returns false if MONGODB_URL is not populated")
	void checkEnvironmentVariablesMongoDbUrlMissingReturnsFalse() {
		checkEnvironmentVariableMissing(MONGODB_URL);
	}

	@Test
	@DisplayName("Check returns false if CHS_API_KEY is not populated")
	void checkEnvironmentVariablesChsApiKeyMissingReturnsFalse() {
		checkEnvironmentVariableMissing(CHS_API_KEY);
	}

	@Test
	@DisplayName("Check returns false if API_URL is not populated")
	void checkEnvironmentVariablesApiUrlMissingReturnsFalse() {
		checkEnvironmentVariableMissing(API_URL);
	}

	/**
	 * Utility method that asserts that if the environment variable specified is not populated,
	 * then {@link CertificatesApiApplication#checkEnvironmentVariables()} returns <code>false</code>.
	 * @param missingVariable the {@link RequiredEnvironmentVariables} value indicating the variable that is to be
	 *                        left unpopulated for the test
	 */
	private void checkEnvironmentVariableMissing(final RequiredEnvironmentVariables missingVariable) {
		stream(RequiredEnvironmentVariables.values()).forEach(
				variable -> {
					if (variable != missingVariable) {
						environmentVariables.set(variable.getName(), variable.getName());
					}
				});
		assertFalse(CertificatesApiApplication.checkEnvironmentVariables());
		stream(RequiredEnvironmentVariables.values()).forEach(
				variable -> {
					if (variable != missingVariable) {
						environmentVariables.clear(variable.getName());
					}
				});
	}

	/**
	 * Utility method that posts the create certificate item request, asserts a bad request status response and an
	 * expected validation error message.
	 * @param itemToCreate the DTO representing the certificate item to be requested
	 * @param expectedError expected validation error message
	 */
	private void postCreateRequestAndExpectBadRequestResponse(final CertificateItemCreate itemToCreate,
															  final String expectedError) {
		WebTestClient.BodyContentSpec bodyContentSpec = webTestClient.post().uri("/orderable/certificates")
				.header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
				.header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
				.header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
				.header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
				.header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(fromValue(itemToCreate))
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.status_code").isEqualTo(BAD_REQUEST.value())
				.jsonPath("$.errors[0].error").isEqualTo(expectedError);
	}

	/**
	 * Utility method that posts the create certificate item request, asserts a bad request status response and an
	 * expected error message.
	 * @param itemToCreate the DTO representing the certificate item to be requested
	 * @param expectedError expected error message
	 */
	private void postCreateRequestAndExpectBadRequestResponseStatusError(final CertificateItemCreate itemToCreate,
																		 final String expectedError) {
		webTestClient.post().uri("/orderable/certificates")
				.header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
				.header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
				.header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
				.header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
				.header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(fromValue(itemToCreate))
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.status").isEqualTo("400")
				.jsonPath("$.message").isEqualTo(expectedError);
	}

	/**
	 * Factory method that produces a DTO for a valid create item request payload.
	 * @return a valid item DTO
	 */
	private CertificateItemCreate createValidNewItem() {
		final CertificateItemCreate newCertificateItemCreate = new CertificateItemCreate();
		newCertificateItemCreate.setCompanyNumber(COMPANY_NUMBER);
		final CertificateItemOptions options = new CertificateItemOptions();
		options.setDeliveryTimescale(STANDARD);
		options.setCompanyType("limited");
		newCertificateItemCreate.setItemOptions(options);
		newCertificateItemCreate.setQuantity(5);
		return newCertificateItemCreate;
	}

}
