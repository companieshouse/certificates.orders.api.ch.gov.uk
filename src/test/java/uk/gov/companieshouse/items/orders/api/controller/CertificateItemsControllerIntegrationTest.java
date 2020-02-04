package uk.gov.companieshouse.items.orders.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import uk.gov.companieshouse.items.orders.api.dto.PatchValidationCertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.interceptor.UserAuthenticationInterceptor;
import uk.gov.companieshouse.items.orders.api.model.*;
import uk.gov.companieshouse.items.orders.api.repository.CertificateItemRepository;
import uk.gov.companieshouse.items.orders.api.service.EtagGeneratorService;
import uk.gov.companieshouse.items.orders.api.util.PatchMediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.companieshouse.items.orders.api.model.CertificateType.*;
import static uk.gov.companieshouse.items.orders.api.model.CollectionLocation.BELFAST;
import static uk.gov.companieshouse.items.orders.api.model.CollectionLocation.CARDIFF;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryMethod.COLLECTION;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryMethod.POSTAL;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.SAME_DAY;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.STANDARD;
import static uk.gov.companieshouse.items.orders.api.model.IncludeDobType.FULL;
import static uk.gov.companieshouse.items.orders.api.model.IncludeDobType.PARTIAL;
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

    @MockBean
    private EtagGeneratorService etagGenerator;

    private static final String CERTIFICATES_URL = "/orderable/certificates/";
    private static final String EXPECTED_ITEM_ID = "CHS00000000000000001";
    private static final String UPDATED_ITEM_ID  = "CHS00000000000000002";
    private static final int QUANTITY = 5;
    private static final int UPDATED_QUANTITY = 10;
    private static final int INVALID_QUANTITY = 0;
    private static final int STANDARD_EXTRA_CERTIFICATE_DISCOUNT = 5;
    private static final int SAME_DAY_EXTRA_CERTIFICATE_DISCOUNT = 40;
    private static final int STANDARD_INDIVIDUAL_CERTIFICATE_COST = 15;
    private static final int SAME_DAY_INDIVIDUAL_CERTIFICATE_COST = 50;
    private static final String ALTERNATIVE_CREATED_BY = "abc123";
    private static final String TOKEN_STRING = "TOKEN VALUE";
    static final Map<String, String> TOKEN_VALUES = new HashMap<>();
    private static final ItemCosts TOKEN_ITEM_COSTS = new ItemCosts();
    private static final String COMPANY_NUMBER = "00006400";
    private static final String DESCRIPTION = "certificate for company " + COMPANY_NUMBER;
    private static final String UPDATED_COMPANY_NUMBER = "00006444";
    private static final String EXPECTED_DESCRIPTION = "certificate for company " + UPDATED_COMPANY_NUMBER;
    private static final String POSTAGE_COST = "0";
    private static final String COMPANY_NUMBER_KEY = "company_number";
    private static final DeliveryTimescale DELIVERY_TIMESCALE = STANDARD;
    private static final DeliveryTimescale UPDATED_DELIVERY_TIMESCALE = SAME_DAY;
    private static final String CUSTOMER_REFERENCE = "Certificate ordered by NJ.";
    private static final String UPDATED_CUSTOMER_REFERENCE = "Certificate ordered by PJ.";
    private static final String INVALID_DELIVERY_TIMESCALE_MESSAGE =
            "Cannot deserialize value of type `uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale`"
            + " from String \"unknown\": value not one of declared Enum instance names: [same_day, standard]";
    private static final String COMPANY_NAME = "Phillips & Daughters";
    private static final String UPDATED_COMPANY_NAME = "Philips & Daughters";
    private static final String TOKEN_ETAG = "9d39ea69b64c80ca42ed72328b48c303c4445e28";
    private static final CertificateType CERTIFICATE_TYPE = INCORPORATION;
    private static final CertificateType UPDATED_CERTIFICATE_TYPE = INCORPORATION_WITH_ALL_NAME_CHANGES;
    private static final String INVALID_CERTIFICATE_TYPE_MESSAGE =
        "Cannot deserialize value of type `uk.gov.companieshouse.items.orders.api.model.CertificateType`"
         + " from String \"unknown\": value not one of declared Enum instance names: "
         + "[dissolution_liquidation, incorporation_with_all_name_changes, incorporation, "
         + "incorporation_with_last_name_changes]";
    private static final DeliveryMethod DELIVERY_METHOD = POSTAL;
    private static final DeliveryMethod UPDATED_DELIVERY_METHOD = COLLECTION;
    private static final String INVALID_DELIVERY_METHOD_MESSAGE =
            "Cannot deserialize value of type `uk.gov.companieshouse.items.orders.api.model.DeliveryMethod`"
                    + " from String \"unknown\": value not one of declared Enum instance names: "
                    + "[postal, collection]";
    private static final CollectionLocation COLLECTION_LOCATION = BELFAST;
    private static final CollectionLocation UPDATED_COLLECTION_LOCATION = CARDIFF;
    private static final String INVALID_COLLECTION_LOCATION_MESSAGE =
            "Cannot deserialize value of type `uk.gov.companieshouse.items.orders.api.model.CollectionLocation`"
                    + " from String \"unknown\": value not one of declared Enum instance names: "
                    + "[london, cardiff, edinburgh, belfast]";
    private static final String MISSING_COLLECTION_LOCATION_MESSAGE =
            "collection_location: must not be null when delivery method is collection";
    private static final String CONTACT_NUMBER = "+44 1234 123456";
    private static final String UPDATED_CONTACT_NUMBER = "+44 1234 123457";
    private static final boolean INCLUDE_COMPANY_OBJECTS_INFORMATION = true;
    private static final boolean UPDATED_INCLUDE_COMPANY_OBJECTS_INFORMATION = false;
    private static final String DO_NOT_INCLUDE_COMPANY_OBJECTS_INFO_MESSAGE =
            "include_company_objects_information: must not be true when certificate type is dissolution_liquidation";
    private static final boolean INCLUDE_EMAIL_COPY = false;
    private static final boolean UPDATED_INCLUDE_EMAIL_COPY = true;
    private static final String INCLUDE_EMAIL_COPY_FOR_SAME_DAY_ONLY_MESSAGE =
            "include_email_copy: can only be true when delivery timescale is same_day";
    private static final boolean INCLUDE_GOOD_STANDING_INFORMATION = true;
    private static final boolean UPDATED_INCLUDE_GOOD_STANDING_INFORMATION = false;
    private static final String DO_NOT_INCLUDE_GOOD_STANDING_INFO_MESSAGE =
    "include_good_standing_information: must not be true when certificate type is dissolution_liquidation";

    private static final boolean INCLUDE_ADDRESS = true;
    private static final boolean UPDATED_INCLUDE_ADDRESS = false;
    private static final boolean INCLUDE_APPOINTMENT_DATE = false;
    private static final boolean UPDATED_INCLUDE_APPOINTMENT_DATE = true;
    private static final boolean INCLUDE_BASIC_INFORMATION = true;
    private static final boolean UPDATED_INCLUDE_BASIC_INFORMATION = false;
    private static final boolean INCLUDE_COUNTRY_OF_RESIDENCE = false;
    private static final boolean UPDATED_INCLUDE_COUNTRY_OF_RESIDENCE = true;
    private static final IncludeDobType INCLUDE_DOB_TYPE = PARTIAL;
    private static final IncludeDobType  UPDATED_INCLUDE_DOB_TYPE = FULL;
    private static final String INVALID_INCLUDE_DOB_TYPE_MESSAGE =
            "Cannot deserialize value of type `uk.gov.companieshouse.items.orders.api.model.IncludeDobType`"
                    + " from String \"unknown\": value not one of declared Enum instance names: [partial, full]";
    private static final boolean INCLUDE_NATIONALITY= false;
    private static final boolean UPDATED_INCLUDE_NATIONALITY= true;
    private static final boolean INCLUDE_OCCUPATION = true;
    private static final boolean UPDATED_INCLUDE_OCCUPATION = false;

    private static final DirectorDetails DIRECTOR_DETAILS;
    private static final DirectorDetails UPDATED_DIRECTOR_DETAILS;

    static {
        DIRECTOR_DETAILS = new DirectorDetails();
        DIRECTOR_DETAILS.setIncludeAddress(INCLUDE_ADDRESS);
        DIRECTOR_DETAILS.setIncludeAppointmentDate(INCLUDE_APPOINTMENT_DATE);
        DIRECTOR_DETAILS.setIncludeBasicInformation(INCLUDE_BASIC_INFORMATION);
        DIRECTOR_DETAILS.setIncludeCountryOfResidence(INCLUDE_COUNTRY_OF_RESIDENCE);
        DIRECTOR_DETAILS.setIncludeDobType(INCLUDE_DOB_TYPE);
        DIRECTOR_DETAILS.setIncludeNationality(INCLUDE_NATIONALITY);
        DIRECTOR_DETAILS.setIncludeOccupation(INCLUDE_OCCUPATION);
        UPDATED_DIRECTOR_DETAILS = new DirectorDetails();
        UPDATED_DIRECTOR_DETAILS.setIncludeAddress(UPDATED_INCLUDE_ADDRESS);
        UPDATED_DIRECTOR_DETAILS.setIncludeAppointmentDate(UPDATED_INCLUDE_APPOINTMENT_DATE);
        UPDATED_DIRECTOR_DETAILS.setIncludeBasicInformation(UPDATED_INCLUDE_BASIC_INFORMATION);
        UPDATED_DIRECTOR_DETAILS.setIncludeCountryOfResidence(UPDATED_INCLUDE_COUNTRY_OF_RESIDENCE);
        UPDATED_DIRECTOR_DETAILS.setIncludeDobType(UPDATED_INCLUDE_DOB_TYPE);
        UPDATED_DIRECTOR_DETAILS.setIncludeNationality(UPDATED_INCLUDE_NATIONALITY);
        UPDATED_DIRECTOR_DETAILS.setIncludeOccupation(UPDATED_INCLUDE_OCCUPATION);
    }

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
        newItem.setCompanyName(COMPANY_NAME);
        newItem.setCompanyNumber(COMPANY_NUMBER);
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertificateType(CERTIFICATE_TYPE);
        options.setCollectionLocation(COLLECTION_LOCATION);
        options.setContactNumber(CONTACT_NUMBER);
        options.setDeliveryMethod(DELIVERY_METHOD);
        options.setDeliveryTimescale(DELIVERY_TIMESCALE);
        options.setDirectorDetails(DIRECTOR_DETAILS);
        options.setIncludeCompanyObjectsInformation(INCLUDE_COMPANY_OBJECTS_INFORMATION);
        options.setIncludeEmailCopy(INCLUDE_EMAIL_COPY);
        options.setIncludeGoodStandingInformation(INCLUDE_GOOD_STANDING_INFORMATION);
        newItem.setItemOptions(options);
        newItem.setQuantity(QUANTITY);
        newItem.setCustomerReference(CUSTOMER_REFERENCE);

        final CertificateItemDTO expectedItem = new CertificateItemDTO();
        expectedItem.setId(EXPECTED_ITEM_ID);
        expectedItem.setCompanyNumber(newItem.getCompanyNumber());
        expectedItem.setKind("certificate");
        expectedItem.setDescriptionIdentifier("certificate");
        final ItemCosts costs = new ItemCosts();
        final int expectedDiscountApplied = (QUANTITY - 1) * STANDARD_EXTRA_CERTIFICATE_DISCOUNT;
        costs.setDiscountApplied(Integer.toString(expectedDiscountApplied));
        costs.setIndividualItemCost(Integer.toString(STANDARD_INDIVIDUAL_CERTIFICATE_COST));
        costs.setPostageCost(POSTAGE_COST);
        costs.setTotalCost(Integer.toString(QUANTITY * STANDARD_INDIVIDUAL_CERTIFICATE_COST - expectedDiscountApplied));
        expectedItem.setItemCosts(costs);
        expectedItem.setItemOptions(options);
        expectedItem.setPostalDelivery(true);
        expectedItem.setQuantity(QUANTITY);
        expectedItem.setCustomerReference(CUSTOMER_REFERENCE);
        expectedItem.setEtag(TOKEN_ETAG);

        when(etagGenerator.generateEtag()).thenReturn(TOKEN_ETAG);

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedItem)))
                .andExpect(jsonPath("$.item_options.certificate_type", is(CERTIFICATE_TYPE.getJsonName())))
                .andExpect(jsonPath("$.item_options.collection_location", is(COLLECTION_LOCATION.getJsonName())))
                .andExpect(jsonPath("$.item_options.contact_number", is(CONTACT_NUMBER)))
                .andExpect(jsonPath("$.item_options.delivery_method", is(DELIVERY_METHOD.getJsonName())))
                .andExpect(jsonPath("$.item_options.delivery_timescale", is(DELIVERY_TIMESCALE.getJsonName())))
                .andExpect(jsonPath("$.item_options.director_details",
                        is(objectMapper.convertValue(DIRECTOR_DETAILS, Map.class))))
                .andExpect(jsonPath("$.item_options.include_company_objects_information",
                        is(INCLUDE_COMPANY_OBJECTS_INFORMATION)))
                .andExpect(jsonPath("$.item_options.include_email_copy", is(INCLUDE_EMAIL_COPY)))
                .andExpect(jsonPath("$.item_options.include_good_standing_information",
                        is(INCLUDE_GOOD_STANDING_INFORMATION)))
                .andExpect(jsonPath("$.postal_delivery", is(true)))
                .andExpect(jsonPath("$.description_values." + COMPANY_NUMBER_KEY, is(COMPANY_NUMBER)))
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
        options.setDeliveryTimescale(DELIVERY_TIMESCALE);
        newItem.setItemOptions(options);
        newItem.setQuantity(QUANTITY);
        newItem.setEtag(TOKEN_ETAG);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, asList("company_number: must not be null",
                                                 "company_name: must not be null",
                                                 "etag: must be null"));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
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
    @DisplayName("Fails to create certificate item with an invalid delivery timescale")
    void createCertificateItemFailsToCreateCertificateItemWithInvalidDeliveryTimescale() throws Exception {

        // Given
        final CertificateItemDTO newItem = new CertificateItemDTO();
        newItem.setCompanyNumber(COMPANY_NUMBER);
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryTimescale(SAME_DAY);
        newItem.setItemOptions(options);
        newItem.setQuantity(QUANTITY);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INVALID_DELIVERY_TIMESCALE_MESSAGE));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeSameDayDeliveryTimescaleInvalid(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemWasNotSaved(EXPECTED_ITEM_ID);
    }

    @Test
    @DisplayName("Fails to create certificate item with an invalid certificate type")
    void createCertificateItemFailsToCreateCertificateItemWithInvalidCertificateType() throws Exception {

        // Given
        final CertificateItemDTO newItem = new CertificateItemDTO();
        newItem.setCompanyNumber(COMPANY_NUMBER);
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertificateType(CERTIFICATE_TYPE);
        newItem.setItemOptions(options);
        newItem.setQuantity(QUANTITY);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INVALID_CERTIFICATE_TYPE_MESSAGE));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeIncorporationCertificateTypeInvalid(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemWasNotSaved(EXPECTED_ITEM_ID);
    }

    @Test
    @DisplayName("Fails to create certificate item with an invalid collection location")
    void createCertificateItemFailsToCreateCertificateItemWithInvalidCollectionLocation() throws Exception {

        // Given
        final CertificateItemDTO newItem = new CertificateItemDTO();
        newItem.setCompanyNumber(COMPANY_NUMBER);
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCollectionLocation(COLLECTION_LOCATION);
        newItem.setItemOptions(options);
        newItem.setQuantity(QUANTITY);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INVALID_COLLECTION_LOCATION_MESSAGE));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeBelfastCollectionLocationInvalid(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemWasNotSaved(EXPECTED_ITEM_ID);
    }

    @Test
    @DisplayName("Fails to create certificate item with a missing collection location")
    void createCertificateItemFailsToCreateCertificateItemWithMissingCollectionLocation() throws Exception {

        // Given
        final CertificateItemDTO newItem = new CertificateItemDTO();
        newItem.setCompanyName(COMPANY_NAME);
        newItem.setCompanyNumber(COMPANY_NUMBER);
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryMethod(COLLECTION);
        newItem.setItemOptions(options);
        newItem.setQuantity(QUANTITY);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(MISSING_COLLECTION_LOCATION_MESSAGE));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
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
    @DisplayName("Fails to create certificate item with an invalid delivery method")
    void createCertificateItemFailsToCreateCertificateItemWithInvalidDeliveryMethod() throws Exception {

        // Given
        final CertificateItemDTO newItem = new CertificateItemDTO();
        newItem.setCompanyNumber(COMPANY_NUMBER);
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryMethod(DELIVERY_METHOD);
        newItem.setItemOptions(options);
        newItem.setQuantity(QUANTITY);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INVALID_DELIVERY_METHOD_MESSAGE));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(makePostalDeliveryMethodInvalid(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemWasNotSaved(EXPECTED_ITEM_ID);
    }

    @Test
    @DisplayName("Fails to create certificate item with include company objects, good standing true for dissolution liquidation")
    void createCertificateItemFailsToCreateCertificateItemWithIncludeCompanyObjectsAndGoodStanding() throws Exception {

        // Given
        final CertificateItemDTO newItem = new CertificateItemDTO();
        newItem.setCompanyName(COMPANY_NAME);
        newItem.setCompanyNumber(COMPANY_NUMBER);
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertificateType(DISSOLUTION_LIQUIDATION);
        options.setIncludeCompanyObjectsInformation(true);
        options.setIncludeGoodStandingInformation(true);
        newItem.setItemOptions(options);
        newItem.setQuantity(QUANTITY);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST,
                        asList(DO_NOT_INCLUDE_COMPANY_OBJECTS_INFO_MESSAGE, DO_NOT_INCLUDE_GOOD_STANDING_INFO_MESSAGE));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
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
    @DisplayName("Fails to create certificate item with include email copy true for standard delivery timescale")
    void createCertificateItemFailsToCreateCertificateItemWithIncludeEmailCopyForStandardDeliveryTimescale()
            throws Exception {

        // Given
        final CertificateItemDTO newItem = new CertificateItemDTO();
        newItem.setCompanyName(COMPANY_NAME);
        newItem.setCompanyNumber(COMPANY_NUMBER);
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryTimescale(STANDARD);
        options.setIncludeEmailCopy(true);
        newItem.setItemOptions(options);
        newItem.setQuantity(QUANTITY);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INCLUDE_EMAIL_COPY_FOR_SAME_DAY_ONLY_MESSAGE));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
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
    @DisplayName("Fails to create certificate item with include email copy true for default delivery timescale")
    void createCertificateItemFailsToCreateCertificateItemWithIncludeEmailCopyForDefaultDeliveryTimescale()
            throws Exception {

        // Given
        final CertificateItemDTO newItem = new CertificateItemDTO();
        newItem.setCompanyName(COMPANY_NAME);
        newItem.setCompanyNumber(COMPANY_NUMBER);
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setIncludeEmailCopy(true);
        newItem.setItemOptions(options);
        newItem.setQuantity(QUANTITY);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INCLUDE_EMAIL_COPY_FOR_SAME_DAY_ONLY_MESSAGE));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
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
    @DisplayName("Fails to create certificate item with an invalid include DOB type")
    void createCertificateItemFailsToCreateCertificateItemWithInvalidIncludeDobType() throws Exception {

        // Given
        final CertificateItemDTO newItem = new CertificateItemDTO();
        newItem.setCompanyNumber(COMPANY_NUMBER);
        final DirectorDetails director = new DirectorDetails();
        director.setIncludeDobType(INCLUDE_DOB_TYPE);
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDirectorDetails(director);
        newItem.setItemOptions(options);
        newItem.setQuantity(QUANTITY);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INVALID_INCLUDE_DOB_TYPE_MESSAGE));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(makePartialIncludeDobTypeInvalid(newItem)))
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
        newItem.setCustomerReference(CUSTOMER_REFERENCE);
        newItem.setEtag(TOKEN_ETAG);
        repository.save(newItem);

        final CertificateItemDTO expectedItem = new CertificateItemDTO();
        expectedItem.setQuantity(QUANTITY);
        expectedItem.setId(EXPECTED_ITEM_ID);

        final ItemCosts costs = new ItemCosts();
        final int expectedDiscountApplied = (QUANTITY - 1) * STANDARD_EXTRA_CERTIFICATE_DISCOUNT;
        costs.setDiscountApplied(Integer.toString(expectedDiscountApplied));
        costs.setIndividualItemCost(Integer.toString(STANDARD_INDIVIDUAL_CERTIFICATE_COST));
        costs.setPostageCost(POSTAGE_COST);
        costs.setTotalCost(Integer.toString(QUANTITY * STANDARD_INDIVIDUAL_CERTIFICATE_COST - expectedDiscountApplied));
        expectedItem.setItemCosts(costs);
        expectedItem.setCustomerReference(CUSTOMER_REFERENCE);
        expectedItem.setEtag(TOKEN_ETAG);

        // When and then
        mockMvc.perform(get(CERTIFICATES_URL+EXPECTED_ITEM_ID)
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
        mockMvc.perform(get( CERTIFICATES_URL+"CHS0")
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
        mockMvc.perform(get(CERTIFICATES_URL+EXPECTED_ITEM_ID)
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
        mockMvc.perform(get(CERTIFICATES_URL+EXPECTED_ITEM_ID)
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
        savedItem.setCompanyName(COMPANY_NAME);
        savedItem.setCompanyNumber(COMPANY_NUMBER);
        savedItem.setCustomerReference(CUSTOMER_REFERENCE);
        savedItem.setDescription(DESCRIPTION);
        savedItem.setDescriptionValues(singletonMap(COMPANY_NUMBER_KEY, COMPANY_NUMBER));

        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertificateType(CERTIFICATE_TYPE);
        options.setCollectionLocation(COLLECTION_LOCATION);
        options.setContactNumber(CONTACT_NUMBER);
        options.setDeliveryMethod(DELIVERY_METHOD);
        options.setDeliveryTimescale(DELIVERY_TIMESCALE);
        options.setDirectorDetails(DIRECTOR_DETAILS);
        options.setIncludeCompanyObjectsInformation(INCLUDE_COMPANY_OBJECTS_INFORMATION);
        options.setIncludeEmailCopy(INCLUDE_EMAIL_COPY);
        options.setIncludeGoodStandingInformation(INCLUDE_GOOD_STANDING_INFORMATION);
        savedItem.setItemOptions(options);
        repository.save(savedItem);

        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        itemUpdate.setQuantity(UPDATED_QUANTITY);
        options.setCertificateType(UPDATED_CERTIFICATE_TYPE);
        options.setCollectionLocation(UPDATED_COLLECTION_LOCATION);
        options.setContactNumber(UPDATED_CONTACT_NUMBER);
        options.setDeliveryMethod(UPDATED_DELIVERY_METHOD);
        options.setDeliveryTimescale(UPDATED_DELIVERY_TIMESCALE);
        options.setDirectorDetails(UPDATED_DIRECTOR_DETAILS);
        options.setIncludeCompanyObjectsInformation(UPDATED_INCLUDE_COMPANY_OBJECTS_INFORMATION);
        options.setIncludeEmailCopy(UPDATED_INCLUDE_EMAIL_COPY);
        options.setIncludeGoodStandingInformation(UPDATED_INCLUDE_GOOD_STANDING_INFORMATION);
        itemUpdate.setItemOptions(options);
        itemUpdate.setCompanyName(UPDATED_COMPANY_NAME);
        itemUpdate.setCompanyNumber(UPDATED_COMPANY_NUMBER);
        itemUpdate.setCustomerReference(UPDATED_CUSTOMER_REFERENCE);

        final CertificateItemDTO expectedItem = new CertificateItemDTO();
        expectedItem.setQuantity(UPDATED_QUANTITY);
        expectedItem.setItemOptions(options);
        expectedItem.setCompanyName(UPDATED_COMPANY_NAME);
        expectedItem.setCompanyNumber(UPDATED_COMPANY_NUMBER);
        expectedItem.setCustomerReference(UPDATED_CUSTOMER_REFERENCE);
        expectedItem.setDescription(EXPECTED_DESCRIPTION);
        expectedItem.setDescriptionValues(singletonMap(COMPANY_NUMBER_KEY, UPDATED_COMPANY_NUMBER));

        final ItemCosts costs = new ItemCosts();
        final int expectedDiscountApplied = (UPDATED_QUANTITY - 1) * SAME_DAY_EXTRA_CERTIFICATE_DISCOUNT;
        costs.setDiscountApplied(Integer.toString(expectedDiscountApplied));
        costs.setIndividualItemCost(Integer.toString(SAME_DAY_INDIVIDUAL_CERTIFICATE_COST));
        costs.setPostageCost(POSTAGE_COST);
        costs.setTotalCost(
                Integer.toString(UPDATED_QUANTITY * SAME_DAY_INDIVIDUAL_CERTIFICATE_COST - expectedDiscountApplied));
        expectedItem.setItemCosts(costs);
        expectedItem.setEtag(TOKEN_ETAG);

        when(etagGenerator.generateEtag()).thenReturn(TOKEN_ETAG);

        // When and then
        final ResultActions response = mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedItem)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        final Optional<CertificateItem> retrievedCertificateItem = repository.findById(EXPECTED_ITEM_ID);
        assertThat(retrievedCertificateItem.isPresent(), is(true));
        assertThat(retrievedCertificateItem.get().getId(), is(EXPECTED_ITEM_ID));
        assertThat(retrievedCertificateItem.get().getQuantity(), is(UPDATED_QUANTITY));
        assertThat(retrievedCertificateItem.get().getItemOptions().getCertificateType(),
                is(UPDATED_CERTIFICATE_TYPE));
        assertThat(retrievedCertificateItem.get().getItemOptions().getCollectionLocation(),
                is(UPDATED_COLLECTION_LOCATION));
        assertThat(retrievedCertificateItem.get().getItemOptions().getContactNumber(),
                is(UPDATED_CONTACT_NUMBER));
        assertThat(retrievedCertificateItem.get().getItemOptions().getDeliveryMethod(),
                is(UPDATED_DELIVERY_METHOD));
        assertThat(retrievedCertificateItem.get().getItemOptions().getDeliveryTimescale(),
                is(UPDATED_DELIVERY_TIMESCALE));
        assertThat(retrievedCertificateItem.get().getItemOptions().getDirectorDetails(),
                is(UPDATED_DIRECTOR_DETAILS));
        assertThat(retrievedCertificateItem.get().getItemOptions().getIncludeCompanyObjectsInformation(),
                is(UPDATED_INCLUDE_COMPANY_OBJECTS_INFORMATION));
        assertThat(retrievedCertificateItem.get().getItemOptions().getIncludeEmailCopy(),
                is(UPDATED_INCLUDE_EMAIL_COPY));
        assertThat(retrievedCertificateItem.get().getItemOptions().getIncludeGoodStandingInformation(),
                is(UPDATED_INCLUDE_GOOD_STANDING_INFORMATION));
        assertThat(retrievedCertificateItem.get().getCompanyName(), is(UPDATED_COMPANY_NAME));

        // Costs are calculated on the fly and are NOT to be saved to the DB.
        assertThat(retrievedCertificateItem.get().getItemCosts(), is(nullValue()));
    }

    @Test
    @DisplayName("Reports failure to find certificate item")
    void updateCertificateItemReportsFailureToFindItem() throws Exception {

        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        itemUpdate.setQuantity(UPDATED_QUANTITY);
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryTimescale(DELIVERY_TIMESCALE);
        itemUpdate.setItemOptions(options);

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
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
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
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
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
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
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
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
        options.setDeliveryTimescale(DELIVERY_TIMESCALE);
        savedItem.setItemOptions(options);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        final TestDTO itemUpdate = new TestDTO("Unknown field value");

        final CertificateItemDTO expectedItem = new CertificateItemDTO();
        expectedItem.setQuantity(QUANTITY);
        expectedItem.setItemOptions(options);

        // When and then
        final ResultActions response = mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedItem)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        final Optional<CertificateItem> retrievedCertificateItem = repository.findById(EXPECTED_ITEM_ID);
        assertThat(retrievedCertificateItem.isPresent(), is(true));
        assertThat(retrievedCertificateItem.get().getId(), is(EXPECTED_ITEM_ID));
        assertThat(retrievedCertificateItem.get().getQuantity(), is(QUANTITY));
        assertThat(retrievedCertificateItem.get().getItemOptions().getDeliveryTimescale(), is(DELIVERY_TIMESCALE));
    }

    @Test
    @DisplayName("Multiple read only fields rejected")
    void updateCertificateItemRejectsMultipleReadOnlyFields() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        itemUpdate.setDescriptionValues(TOKEN_VALUES);
        itemUpdate.setItemCosts(TOKEN_ITEM_COSTS);
        itemUpdate.setKind(TOKEN_STRING);
        itemUpdate.setEtag(TOKEN_ETAG);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        final ApiError expectedValidationErrors =
                new ApiError(BAD_REQUEST, asList("description_values: must be null",
                                                 "item_costs: must be null",
                                                 "kind: must be null",
                                                 "etag: must be null"));

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
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

    @Test
    @DisplayName("Rejects update request containing an invalid delivery timescale")
    void updateCertificateItemRejectsInvalidDeliveryTimescale() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryTimescale(SAME_DAY);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INVALID_DELIVERY_TIMESCALE_MESSAGE));

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                .content(makeSameDayDeliveryTimescaleInvalid(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Rejects update request containing an invalid certificate type")
    void updateCertificateItemRejectsInvalidCertificateType() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertificateType(CERTIFICATE_TYPE);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INVALID_CERTIFICATE_TYPE_MESSAGE));

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                .content(makeIncorporationCertificateTypeInvalid(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Rejects update request containing an invalid collection location")
    void updateCertificateItemRejectsInvalidCollectionLocation() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCollectionLocation(COLLECTION_LOCATION);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INVALID_COLLECTION_LOCATION_MESSAGE));

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                .content(makeBelfastCollectionLocationInvalid(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Rejects update request with collection delivery method and a missing collection location")
    void updateCertificateItemRejectsMissingCollectionLocation() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryMethod(COLLECTION);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(MISSING_COLLECTION_LOCATION_MESSAGE));

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
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
    @DisplayName("Rejects update request with a missing collection location to an item with collection delivery method")
    void updateCertificateItemRejectsMissingCollectionLocationToCollectionDeliveryMethodItem() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        final CertificateItemOptions savedOptions = new CertificateItemOptions();
        savedOptions.setDeliveryMethod(COLLECTION);
        savedItem.setItemOptions(savedOptions);
        repository.save(savedItem);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(MISSING_COLLECTION_LOCATION_MESSAGE));

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
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
    @DisplayName("Rejects update request containing an invalid delivery method")
    void updateCertificateItemRejectsInvalidDeliveryMethod() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryMethod(DELIVERY_METHOD);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INVALID_DELIVERY_METHOD_MESSAGE));

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                .content(makePostalDeliveryMethodInvalid(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Rejects update request with include company objects, good standing info and dissolution liquidation")
    void updateCertificateItemRejectsRequestWithIncludeCompanyObjectsGoodStandingInfoAndDissolutionLiquidation()
            throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertificateType(DISSOLUTION_LIQUIDATION);
        options.setIncludeCompanyObjectsInformation(true);
        options.setIncludeGoodStandingInformation(true);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST,
                        asList(DO_NOT_INCLUDE_COMPANY_OBJECTS_INFO_MESSAGE, DO_NOT_INCLUDE_GOOD_STANDING_INFO_MESSAGE));

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
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
    @DisplayName("Rejects update request with include company objects, good standing info updating dissolution liquidation item")
    void updateCertificateItemRejectsRequestWithIncludeCompanyObjectsGoodStandingInfoUpdatingDissolutionLiquidationItem()
            throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setIncludeCompanyObjectsInformation(true);
        options.setIncludeGoodStandingInformation(true);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        final CertificateItemOptions savedOptions = new CertificateItemOptions();
        savedOptions.setCertificateType(DISSOLUTION_LIQUIDATION);
        savedItem.setItemOptions(savedOptions);

        repository.save(savedItem);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST,
                        asList(DO_NOT_INCLUDE_COMPANY_OBJECTS_INFO_MESSAGE, DO_NOT_INCLUDE_GOOD_STANDING_INFO_MESSAGE));

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
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
    @DisplayName("Rejects update request with include email copy and default delivery timescale")
    void updateCertificateItemRejectsRequestWithIncludeEmailCopyAndDefaultDeliveryTimescale() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setIncludeEmailCopy(true);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INCLUDE_EMAIL_COPY_FOR_SAME_DAY_ONLY_MESSAGE));

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
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
    @DisplayName("Rejects update request with include email copy updating standard delivery timescale item")
    void updateCertificateItemRejectsRequestWithIncludeEmailCopyUpdatingStandardDeliveryTimescaleItem()
            throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setIncludeEmailCopy(true);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        final CertificateItemOptions savedOptions = new CertificateItemOptions();
        savedOptions.setDeliveryTimescale(STANDARD);
        savedItem.setItemOptions(savedOptions);
        repository.save(savedItem);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INCLUDE_EMAIL_COPY_FOR_SAME_DAY_ONLY_MESSAGE));

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
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
    @DisplayName("Rejects update request containing an invalid include DOB type")
    void updateCertificateItemRejectsInvalidIncludeDobType() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final DirectorDetails director = new DirectorDetails();
        director.setIncludeDobType(INCLUDE_DOB_TYPE);
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDirectorDetails(director);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INVALID_INCLUDE_DOB_TYPE_MESSAGE));

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME,ERIC_IDENTITY_TYPE_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                .content(makePartialIncludeDobTypeInvalid(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "incorporation" certificate type value with "unknown" for validation testing purposes.
     * @param newItem the item to be serialised to JSON with an incorrect certificate type value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makeIncorporationCertificateTypeInvalid(final CertificateItemDTO newItem)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(newItem).replace("incorporation", "unknown");
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "incorporation" certificate type value with "unknown" for validation testing purposes.
     * @param itemUpdate the item to be serialised to JSON with an incorrect certificate type value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makeIncorporationCertificateTypeInvalid(final PatchValidationCertificateItemDTO itemUpdate)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(itemUpdate).replace("incorporation", "unknown");
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "belfast" collection location value with "unknown" for validation testing purposes.
     * @param newItem the item to be serialised to JSON with an incorrect delivery method value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makeBelfastCollectionLocationInvalid(final CertificateItemDTO newItem)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(newItem).replace("belfast", "unknown");
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "belfast" collection location value with "unknown" for validation testing purposes.
     * @param itemUpdate the item to be serialised to JSON with an incorrect certificate type value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makeBelfastCollectionLocationInvalid(final PatchValidationCertificateItemDTO itemUpdate)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(itemUpdate).replace("belfast", "unknown");
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "postal" delivery method value with "unknown" for validation testing purposes.
     * @param newItem the item to be serialised to JSON with an incorrect delivery method value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makePostalDeliveryMethodInvalid(final CertificateItemDTO newItem)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(newItem).replace("postal", "unknown");
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "postal" certificate type value with "unknown" for validation testing purposes.
     * @param itemUpdate the item to be serialised to JSON with an incorrect certificate type value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makePostalDeliveryMethodInvalid(final PatchValidationCertificateItemDTO itemUpdate)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(itemUpdate).replace("postal", "unknown");
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "same_day" delivery timescale value with "unknown" for validation testing purposes.
     * @param newItem the item to be serialised to JSON with an incorrect delivery timescale value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makeSameDayDeliveryTimescaleInvalid(final CertificateItemDTO newItem)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(newItem).replace("same_day", "unknown");
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "same_day" delivery timescale value with "unknown" for validation testing purposes.
     * @param itemUpdate the item to be serialised to JSON with an incorrect delivery timescale value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makeSameDayDeliveryTimescaleInvalid(final PatchValidationCertificateItemDTO itemUpdate)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(itemUpdate).replace("same_day", "unknown");
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "partial" include DOB type value with "unknown" for validation testing purposes.
     * @param newItem the item to be serialised to JSON with an incorrect include DOB type value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makePartialIncludeDobTypeInvalid(final CertificateItemDTO newItem)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(newItem).replace("partial", "unknown");
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "partial" include DOB type value with "unknown" for validation testing purposes.
     * @param itemUpdate the item to be serialised to JSON with an incorrect include DOB type value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makePartialIncludeDobTypeInvalid(final PatchValidationCertificateItemDTO itemUpdate)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(itemUpdate).replace("partial", "unknown");
    }

    /**
     * Verifies that the item assumed to have been created by the create item POST request can be retrieved
     * from the database using its expected ID value. Also verifies that item costs have NOT been saved to the DB.
     * @param expectedItemId the expected ID of the newly created item
     */
    private void assertItemSavedCorrectly(final String expectedItemId) {
        final Optional<CertificateItem> retrievedCertificateItem = repository.findById(expectedItemId);
        assertThat(retrievedCertificateItem.isPresent(), is(true));
        assertThat(retrievedCertificateItem.get().getId(), is(expectedItemId));

        // Costs are calculated on the fly and are NOT to be saved to the DB.
        assertThat(retrievedCertificateItem.get().getItemCosts(), is(nullValue()));
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
