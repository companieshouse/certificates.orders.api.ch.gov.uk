package uk.gov.companieshouse.certificates.orders.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.gov.companieshouse.api.interceptor.CRUDAuthenticationInterceptor;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemCreate;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemInitial;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemResponse;
import uk.gov.companieshouse.certificates.orders.api.dto.PatchValidationCertificateItemDTO;
import uk.gov.companieshouse.certificates.orders.api.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.certificates.orders.api.interceptor.UserAuthenticationInterceptor;
import uk.gov.companieshouse.certificates.orders.api.interceptor.UserAuthorisationInterceptor;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItem;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptionsRequest;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateType;
import uk.gov.companieshouse.certificates.orders.api.model.CollectionLocation;
import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;
import uk.gov.companieshouse.certificates.orders.api.model.DeliveryMethod;
import uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale;
import uk.gov.companieshouse.certificates.orders.api.model.DirectorOrSecretaryDetails;
import uk.gov.companieshouse.certificates.orders.api.model.IncludeAddressRecordsType;
import uk.gov.companieshouse.certificates.orders.api.model.IncludeDobType;
import uk.gov.companieshouse.certificates.orders.api.model.ItemCosts;
import uk.gov.companieshouse.certificates.orders.api.model.Links;
import uk.gov.companieshouse.certificates.orders.api.model.LiquidatorsDetails;
import uk.gov.companieshouse.certificates.orders.api.model.ProductType;
import uk.gov.companieshouse.certificates.orders.api.model.RegisteredOfficeAddressDetails;
import uk.gov.companieshouse.certificates.orders.api.repository.CertificateItemRepository;
import uk.gov.companieshouse.certificates.orders.api.service.CompanyNotFoundException;
import uk.gov.companieshouse.certificates.orders.api.service.CompanyService;
import uk.gov.companieshouse.certificates.orders.api.service.EtagGeneratorService;
import uk.gov.companieshouse.certificates.orders.api.service.IdGeneratorService;
import uk.gov.companieshouse.certificates.orders.api.util.PatchMediaType;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

/**
 * Unit/integration tests the {@link CertificateItemsController} class.
 */
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("feature-flags-enabled")
class CertificateItemsControllerIntegrationTest {

    static final Map<String, String> TOKEN_VALUES = new HashMap<>();
    private static final String CERTIFICATES_URL = "/orderable/certificates/";
    private static final String INITIAL_CERTIFICATE_URL = "/orderable/certificates/initial";
    private static final String EXPECTED_ITEM_ID = "CRT-123456-123456";
    private static final String UPDATED_ITEM_ID = "CRT-123456-123457";
    private static final int QUANTITY = 5;
    private static final int UPDATED_QUANTITY = 10;
    private static final int INVALID_QUANTITY = 0;
    private static final int STANDARD_EXTRA_CERTIFICATE_DISCOUNT = 5;
    private static final int SAME_DAY_EXTRA_CERTIFICATE_DISCOUNT = 40;
    private static final int STANDARD_INDIVIDUAL_CERTIFICATE_COST = 15;
    private static final int SAME_DAY_INDIVIDUAL_CERTIFICATE_COST = 50;
    private static final String ALTERNATIVE_CREATED_BY = "abc123";
    private static final String TOKEN_STRING = "TOKEN VALUE";
    private static final ItemCosts TOKEN_ITEM_COSTS = new ItemCosts();
    private static final String COMPANY_NUMBER = "00006400";
    private static final String PREVIOUS_COMPANY_NUMBER = "00006400";
    private static final CompanyStatus PREVIOUS_COMPANY_STATUS = CompanyStatus.ACTIVE;
    private static final CompanyStatus EXPECTED_COMPANY_STATUS = CompanyStatus.ACTIVE;
    private static final CompanyStatus UPDATED_COMPANY_STATUS = CompanyStatus.LIQUIDATION;
    private static final String EXPECTED_COMPANY_NAME = "THE GIRLS' DAY SCHOOL TRUST";
    private static final String PREVIOUS_COMPANY_TYPE = "limited";
    private static final String UPDATED_COMPANY_TYPE = "llp";
    private static final String PREVIOUS_COMPANY_NAME = "Phillips and Daughters";
    private static final String DESCRIPTION = "certificate for company " + COMPANY_NUMBER;
    private static final String UPDATED_COMPANY_NUMBER = "00006444";
    private static final String EXPECTED_DESCRIPTION = "certificate for company " + UPDATED_COMPANY_NUMBER;
    private static final String POSTAGE_COST = "0";
    private static final String COMPANY_NUMBER_KEY = "company_number";
    private static final DeliveryTimescale DELIVERY_TIMESCALE = DeliveryTimescale.STANDARD;
    private static final DeliveryTimescale UPDATED_DELIVERY_TIMESCALE = DeliveryTimescale.SAME_DAY;
    private static final String CUSTOMER_REFERENCE = "Certificate ordered by NJ.";
    private static final String UPDATED_CUSTOMER_REFERENCE = "Certificate ordered by PJ.";
    private static final String INVALID_DELIVERY_TIMESCALE_MESSAGE =
            "Cannot deserialize value of type `uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale`"
                    + " from String \"unknown\": not one of the values accepted for Enum class: [standard, same-day]";
    private static final String COMPANY_NAME = "Phillips & Daughters";
    private static final String UPDATED_COMPANY_NAME = "Philips & Daughters";
    private static final String TOKEN_ETAG = "9d39ea69b64c80ca42ed72328b48c303c4445e28";
    private static final CertificateType CERTIFICATE_TYPE = CertificateType.INCORPORATION;
    private static final CertificateType UPDATED_CERTIFICATE_TYPE = CertificateType.INCORPORATION_WITH_ALL_NAME_CHANGES;
    private static final String INVALID_CERTIFICATE_TYPE_MESSAGE =
            "Cannot deserialize value of type `uk.gov.companieshouse.certificates.orders.api.model.CertificateType`"
                    + " from String \"unknown\": not one of the values accepted for Enum class: "
                    + "[incorporation-with-all-name-changes, incorporation, dissolution, "
                    + "incorporation-with-last-name-changes]";
    private static final DeliveryMethod DELIVERY_METHOD = DeliveryMethod.POSTAL;
    private static final DeliveryMethod UPDATED_DELIVERY_METHOD = DeliveryMethod.COLLECTION;
    private static final String INVALID_DELIVERY_METHOD_MESSAGE =
            "Cannot deserialize value of type `uk.gov.companieshouse.certificates.orders.api.model.DeliveryMethod`"
                    + " from String \"unknown\": not one of the values accepted for Enum class: "
                    + "[postal, collection]";
    private static final CollectionLocation COLLECTION_LOCATION = CollectionLocation.BELFAST;
    private static final CollectionLocation UPDATED_COLLECTION_LOCATION = CollectionLocation.CARDIFF;
    private static final String INVALID_COLLECTION_LOCATION_MESSAGE =
            "Cannot deserialize value of type `uk.gov.companieshouse.certificates.orders.api.model.CollectionLocation`"
                    + " from String \"unknown\": not one of the values accepted for Enum class: "
                    + "[london, cardiff, edinburgh, belfast]";
    private static final String MISSING_COLLECTION_LOCATION_MESSAGE =
            "collection_location: must not be null when delivery method is collection";
    private static final String MISSING_COLLECTION_FORENAME_MESSAGE =
            "forename: must not be blank when delivery method is collection";
    private static final String MISSING_COLLECTION_SURNAME_MESSAGE =
            "surname: must not be blank when delivery method is collection";
    private static final String CONTACT_NUMBER = "+44 1234 123456";
    private static final String UPDATED_CONTACT_NUMBER = "+44 1234 123457";
    private static final boolean INCLUDE_COMPANY_OBJECTS_INFORMATION = true;
    private static final boolean UPDATED_INCLUDE_COMPANY_OBJECTS_INFORMATION = false;
    private static final String DO_NOT_INCLUDE_COMPANY_OBJECTS_INFO_MESSAGE =
            "include_company_objects_information: must not exist when certificate type is dissolution";
    private static final String DO_NOT_INCLUDE_REGISTERED_OFFICE_ADDRESS_INFO_MESSAGE =
            "include_registered_office_address_details: must not exist when certificate type is dissolution";
    private static final String DO_NOT_INCLUDE_SECRETARY_DETAILS_INFO_MESSAGE =
            "include_secretary_details: must not exist when certificate type is dissolution";
    private static final String DO_NOT_INCLUDE_DIRECTOR_DETAILS_INFO_MESSAGE =
            "include_director_details: must not exist when certificate type is dissolution";
    private static final boolean INCLUDE_EMAIL_COPY = false;
    private static final boolean UPDATED_INCLUDE_EMAIL_COPY = true;
    private static final String INCLUDE_EMAIL_COPY_FOR_SAME_DAY_ONLY_MESSAGE =
            "include_email_copy: can only be true when delivery timescale is same_day";
    private static final boolean INCLUDE_GOOD_STANDING_INFORMATION = true;
    private static final boolean UPDATED_INCLUDE_GOOD_STANDING_INFORMATION = false;
    private static final String DO_NOT_INCLUDE_GOOD_STANDING_INFO_MESSAGE =
            "include_good_standing_information: must not exist when certificate type is dissolution";
    private static final boolean INCLUDE_ADDRESS = true;
    private static final boolean UPDATED_INCLUDE_ADDRESS = false;
    private static final boolean INCLUDE_APPOINTMENT_DATE = false;
    private static final boolean UPDATED_INCLUDE_APPOINTMENT_DATE = true;
    private static final boolean INCLUDE_BASIC_INFORMATION = true;
    private static final boolean UPDATED_INCLUDE_BASIC_INFORMATION = true;
    private static final boolean INCLUDE_COUNTRY_OF_RESIDENCE = false;
    private static final boolean UPDATED_INCLUDE_COUNTRY_OF_RESIDENCE = true;
    private static final IncludeDobType INCLUDE_DOB_TYPE = IncludeDobType.PARTIAL;
    private static final IncludeDobType UPDATED_INCLUDE_DOB_TYPE = IncludeDobType.FULL;
    private static final String INVALID_INCLUDE_DOB_TYPE_MESSAGE =
            "Cannot deserialize value of type `uk.gov.companieshouse.certificates.orders.api.model.IncludeDobType`"
                    + " from String \"unknown\": not one of the values accepted for Enum class: [partial, full]";
    private static final boolean INCLUDE_NATIONALITY = false;
    private static final boolean UPDATED_INCLUDE_NATIONALITY = true;
    private static final boolean INCLUDE_OCCUPATION = true;
    private static final boolean UPDATED_INCLUDE_OCCUPATION = false;
    private static final IncludeAddressRecordsType INCLUDE_ADDRESS_RECORDS_TYPE = IncludeAddressRecordsType.CURRENT;
    private static final IncludeAddressRecordsType UPDATED_INCLUDE_ADDRESS_RECORDS_TYPE = IncludeAddressRecordsType.CURRENT_PREVIOUS_AND_PRIOR;
    private static final String INVALID_INCLUDE_ADDRESS_RECORDS_TYPE_MESSAGE =
            "Cannot deserialize value of type `uk.gov.companieshouse.certificates.orders.api.model.IncludeAddressRecordsType`"
                    + " from String \"unknown\": not one of the values accepted for Enum class: "
                    + "[all, current-previous-and-prior, current, current-and-previous]";
    private static final boolean INCLUDE_DATES = true;
    private static final boolean UPDATED_INCLUDE_DATES = false;
    private static final DirectorOrSecretaryDetails DIRECTOR_OR_SECRETARY_DETAILS;
    private static final DirectorOrSecretaryDetails UPDATED_DIRECTOR_OR_SECRETARY_DETAILS;
    private static final DirectorOrSecretaryDetails CONFLICTING_DIRECTOR_OR_SECRETARY_DETAILS;
    private static final String CONFLICTING_DIRECTOR_DETAILS_MESSAGE =
            "director_details: include_address, include_nationality, include_occupation must not be true when "
                    + "include_basic_information is false";
    private static final String CONFLICTING_SECRETARY_DETAILS_MESSAGE =
            "secretary_details: include_address, include_nationality, include_occupation must not be true when "
                    + "include_basic_information is false";
    private static final RegisteredOfficeAddressDetails REGISTERED_OFFICE_ADDRESS_DETAILS;
    private static final RegisteredOfficeAddressDetails UPDATED_REGISTERED_OFFICE_ADDRESS_DETAILS;
    private static final String SELF_PATH = "/orderable/certificates";
    private static final Links LINKS;
    private static final List<String> ITEM_OPTIONS_ENUM_FIELDS =
            asList("certificate_type", "collection_location", "delivery_method", "delivery_timescale");
    private static final String FORENAME = "John";
    private static final String SURNAME = "Smith";
    private static final String UPDATED_SURNAME = "Smyth";
    private static final String TOKEN_TOTAL_ITEM_COST = "100";
    private static final String TOKEN_PERMISSION_VALUE = "user_orders=%s";

    static {
        DIRECTOR_OR_SECRETARY_DETAILS = new DirectorOrSecretaryDetails();
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeAddress(INCLUDE_ADDRESS);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeAppointmentDate(INCLUDE_APPOINTMENT_DATE);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeBasicInformation(INCLUDE_BASIC_INFORMATION);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeCountryOfResidence(INCLUDE_COUNTRY_OF_RESIDENCE);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeDobType(INCLUDE_DOB_TYPE);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeNationality(INCLUDE_NATIONALITY);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeOccupation(INCLUDE_OCCUPATION);

        UPDATED_DIRECTOR_OR_SECRETARY_DETAILS = new DirectorOrSecretaryDetails();
        UPDATED_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeAddress(UPDATED_INCLUDE_ADDRESS);
        UPDATED_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeAppointmentDate(UPDATED_INCLUDE_APPOINTMENT_DATE);
        UPDATED_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeBasicInformation(UPDATED_INCLUDE_BASIC_INFORMATION);
        UPDATED_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeCountryOfResidence(UPDATED_INCLUDE_COUNTRY_OF_RESIDENCE);
        UPDATED_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeDobType(UPDATED_INCLUDE_DOB_TYPE);
        UPDATED_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeNationality(UPDATED_INCLUDE_NATIONALITY);
        UPDATED_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeOccupation(UPDATED_INCLUDE_OCCUPATION);

        CONFLICTING_DIRECTOR_OR_SECRETARY_DETAILS = new DirectorOrSecretaryDetails();
        CONFLICTING_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeAddress(true);
        CONFLICTING_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeAppointmentDate(false);
        CONFLICTING_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeNationality(true);
        CONFLICTING_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeOccupation(true);

        REGISTERED_OFFICE_ADDRESS_DETAILS = new RegisteredOfficeAddressDetails();
        REGISTERED_OFFICE_ADDRESS_DETAILS.setIncludeAddressRecordsType(INCLUDE_ADDRESS_RECORDS_TYPE);
        REGISTERED_OFFICE_ADDRESS_DETAILS.setIncludeDates(INCLUDE_DATES);

        UPDATED_REGISTERED_OFFICE_ADDRESS_DETAILS = new RegisteredOfficeAddressDetails();
        UPDATED_REGISTERED_OFFICE_ADDRESS_DETAILS.setIncludeAddressRecordsType(UPDATED_INCLUDE_ADDRESS_RECORDS_TYPE);
        UPDATED_REGISTERED_OFFICE_ADDRESS_DETAILS.setIncludeDates(UPDATED_INCLUDE_DATES);

        LINKS = new Links();
        LINKS.setSelf(SELF_PATH + "/" + EXPECTED_ITEM_ID);
    }

    @Autowired
    UserAuthenticationInterceptor userAuthenticationInterceptor;
    @Autowired
    LoggingInterceptor loggingInterceptor;
    @Autowired
    UserAuthorisationInterceptor userAuthorisationInterceptor;
    @Autowired
    CRUDAuthenticationInterceptor crudPermissionsInterceptor;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CertificateItemRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @MockBean
    private EtagGeneratorService etagGenerator;
    @MockBean
    private IdGeneratorService idGeneratorService;
    @MockBean
    private CompanyService companyService;
    @MockBean
    private CompanyProfileResource companyProfileResource;
    @MockBean
    private CompanyProfileToCertificateTypeMapper certificateTypeMapperIF;

    private CertificateItemCreate certificateItemCreate;

    private CertificateItemOptionsRequest certificateItemOptions;

    private static Stream<Arguments> provideLiquidatorsDetailsErrorFixtures() {
        return Stream.of(
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_COMPANY)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .withLiquidatorsDetails(new LiquidatorsDetails())
                        .withExpectedErrors(singletonList(ApiErrors.raiseError(ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED, "include_liquidators_details: must not exist when company status is active")))
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_COMPANY)
                        .withCompanyStatus(CompanyStatus.LIQUIDATION)
                        .withIncludeGoodStandingInformation(true)
                        .withExpectedErrors(singletonList(ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_GOOD_STANDING_INFORMATION_SUPPLIED, "include_good_standing_information: must not exist when company status is liquidation")))
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_LIABILITY_PARTNERSHIP)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .withLiquidatorsDetails(new LiquidatorsDetails())
                        .withExpectedErrors(singletonList(ApiErrors.raiseError(ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED, "include_liquidators_details: must not exist when company status is active")))
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_LIABILITY_PARTNERSHIP)
                        .withCompanyStatus(CompanyStatus.LIQUIDATION)
                        .withIncludeGoodStandingInformation(true)
                        .withExpectedErrors(singletonList(ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_GOOD_STANDING_INFORMATION_SUPPLIED, "include_good_standing_information: must not exist when company status is liquidation")))
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_PARTNERSHIP)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .withLiquidatorsDetails(new LiquidatorsDetails())
                        .withExpectedErrors(singletonList(ApiErrors.raiseError(ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED, "include_liquidators_details: must not exist when company type is limited-partnership")))
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_PARTNERSHIP)
                        .withCompanyStatus(CompanyStatus.LIQUIDATION)
                        .withExpectedErrors(singletonList(ApiErrors.raiseError(ApiErrors.ERR_COMPANY_STATUS_INVALID, "company_status: liquidation not valid for company type limited-partnership")))
                        .build())
        );
    }

    @AfterEach
    void tearDown() {
        repository.findById(EXPECTED_ITEM_ID).ifPresent(repository::delete);
    }

    @BeforeEach
    void beforeEach() {
        certificateItemCreate = new CertificateItemCreate();
        certificateItemOptions = new CertificateItemOptionsRequest();
        certificateItemCreate.setItemOptions(certificateItemOptions);
    }

    @Test
    @DisplayName("Successfully creates certificate item")
    void createCertificateItemSuccessfullyCreatesCertificateItem() throws Exception {
        // Given
        final CertificateItemOptionsRequest itemOptionsRequest = new CertificateItemOptionsRequest();
        itemOptionsRequest.setCollectionLocation(COLLECTION_LOCATION);
        itemOptionsRequest.setContactNumber(CONTACT_NUMBER);
        itemOptionsRequest.setDeliveryMethod(DELIVERY_METHOD);
        itemOptionsRequest.setDeliveryTimescale(DELIVERY_TIMESCALE);
        itemOptionsRequest.setDirectorDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        itemOptionsRequest.setForename(FORENAME);
        itemOptionsRequest.setIncludeCompanyObjectsInformation(INCLUDE_COMPANY_OBJECTS_INFORMATION);
        itemOptionsRequest.setIncludeEmailCopy(INCLUDE_EMAIL_COPY);
        itemOptionsRequest.setIncludeGoodStandingInformation(INCLUDE_GOOD_STANDING_INFORMATION);
        itemOptionsRequest.setRegisteredOfficeAddressDetails(REGISTERED_OFFICE_ADDRESS_DETAILS);
        itemOptionsRequest.setSecretaryDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        itemOptionsRequest.setSurname(SURNAME);

        final CertificateItemCreate newItem = new CertificateItemCreate();
        newItem.setCompanyNumber(COMPANY_NUMBER);
        newItem.setQuantity(QUANTITY);
        newItem.setCustomerReference(CUSTOMER_REFERENCE);
        newItem.setItemOptions(itemOptionsRequest);

        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertificateType(CERTIFICATE_TYPE);
        options.setCompanyType(PREVIOUS_COMPANY_TYPE);
        options.setCollectionLocation(COLLECTION_LOCATION);
        options.setContactNumber(CONTACT_NUMBER);
        options.setDeliveryMethod(DELIVERY_METHOD);
        options.setDeliveryTimescale(DELIVERY_TIMESCALE);
        options.setDirectorDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        options.setForename(FORENAME);
        options.setIncludeCompanyObjectsInformation(INCLUDE_COMPANY_OBJECTS_INFORMATION);
        options.setIncludeEmailCopy(INCLUDE_EMAIL_COPY);
        options.setIncludeGoodStandingInformation(INCLUDE_GOOD_STANDING_INFORMATION);
        options.setRegisteredOfficeAddressDetails(REGISTERED_OFFICE_ADDRESS_DETAILS);
        options.setSecretaryDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        options.setSurname(SURNAME);

        final CertificateItemResponse expectedItem = new CertificateItemResponse();
        expectedItem.setId(EXPECTED_ITEM_ID);
        expectedItem.setCompanyNumber(newItem.getCompanyNumber());
        expectedItem.setCompanyName(EXPECTED_COMPANY_NAME);
        expectedItem.setKind("item#certificate");
        expectedItem.setDescriptionIdentifier("certificate");
        final List<ItemCosts> costs = generateExpectedCosts(QUANTITY, DELIVERY_TIMESCALE);
        expectedItem.setItemCosts(costs);
        expectedItem.setItemOptions(options);
        expectedItem.setPostalDelivery(true);
        expectedItem.setQuantity(QUANTITY);
        expectedItem.setCustomerReference(CUSTOMER_REFERENCE);
        expectedItem.setLinks(LINKS);
        expectedItem.setEtag(TOKEN_ETAG);
        expectedItem.setPostageCost(POSTAGE_COST);
        final String totalItemCost = calculateExpectedTotalItemCost(costs, POSTAGE_COST);
        expectedItem.setTotalItemCost(totalItemCost);

        when(etagGenerator.generateEtag()).thenReturn(TOKEN_ETAG);
        when(companyService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(
                new CompanyProfileResource(EXPECTED_COMPANY_NAME, PREVIOUS_COMPANY_TYPE, EXPECTED_COMPANY_STATUS));
        when(idGeneratorService.autoGenerateId()).thenReturn(EXPECTED_ITEM_ID);
        when(certificateTypeMapperIF.mapToCertificateType(any())).thenReturn(new CertificateTypeMapResult(CertificateType.INCORPORATION));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedItem)))
                .andExpect(jsonPath("$.company_name", is(EXPECTED_COMPANY_NAME)))
                .andExpect(jsonPath("$.item_options.certificate_type", is(CERTIFICATE_TYPE.getJsonName())))
                .andExpect(jsonPath("$.item_options.company_type", is(PREVIOUS_COMPANY_TYPE)))
                .andExpect(jsonPath("$.item_options.collection_location", is(COLLECTION_LOCATION.getJsonName())))
                .andExpect(jsonPath("$.item_options.contact_number", is(CONTACT_NUMBER)))
                .andExpect(jsonPath("$.item_options.delivery_method", is(DELIVERY_METHOD.getJsonName())))
                .andExpect(jsonPath("$.item_options.delivery_timescale", is(DELIVERY_TIMESCALE.getJsonName())))
                .andExpect(jsonPath("$.item_options.director_details",
                        is(objectMapper.convertValue(DIRECTOR_OR_SECRETARY_DETAILS, Map.class))))
                .andExpect(jsonPath("$.item_options.forename",
                        is(FORENAME)))
                .andExpect(jsonPath("$.item_options.include_company_objects_information",
                        is(INCLUDE_COMPANY_OBJECTS_INFORMATION)))
                .andExpect(jsonPath("$.item_options.include_email_copy", is(INCLUDE_EMAIL_COPY)))
                .andExpect(jsonPath("$.item_options.include_good_standing_information",
                        is(INCLUDE_GOOD_STANDING_INFORMATION)))
                .andExpect(jsonPath("$.item_options.registered_office_address_details",
                        is(objectMapper.convertValue(REGISTERED_OFFICE_ADDRESS_DETAILS, Map.class))))
                .andExpect(jsonPath("$.postal_delivery", is(true)))
                .andExpect(jsonPath("$.description_values." + COMPANY_NUMBER_KEY, is(COMPANY_NUMBER)))
                .andExpect(jsonPath("$.item_options.secretary_details",
                        is(objectMapper.convertValue(DIRECTOR_OR_SECRETARY_DETAILS, Map.class))))
                .andExpect(jsonPath("$.item_options.surname",
                        is(SURNAME)))
                .andExpect(jsonPath("$.links",
                        is(objectMapper.convertValue(LINKS, Map.class))))
                .andExpect(jsonPath("$.postage_cost",
                        is(POSTAGE_COST)))
                .andExpect(jsonPath("$.total_item_cost",
                        is(totalItemCost)))
                .andExpect(jsonPath("$.item_options.company_status",
                        is(EXPECTED_COMPANY_STATUS.getStatusName())))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemSavedCorrectly(EXPECTED_ITEM_ID);

        verify(etagGenerator).generateEtag();
        verify(companyService).getCompanyProfile(COMPANY_NUMBER);
    }

    @Test
    @DisplayName("Fails to create certificate item with incorrect token permission")
    void createCertificateItemUnauthorizedTokenPermission() throws Exception {
        // Given
        final CertificateItemOptionsRequest itemOptionsRequest = new CertificateItemOptionsRequest();
        itemOptionsRequest.setCollectionLocation(COLLECTION_LOCATION);
        itemOptionsRequest.setContactNumber(CONTACT_NUMBER);
        itemOptionsRequest.setDeliveryMethod(DELIVERY_METHOD);
        itemOptionsRequest.setDeliveryTimescale(DELIVERY_TIMESCALE);
        itemOptionsRequest.setDirectorDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        itemOptionsRequest.setForename(FORENAME);
        itemOptionsRequest.setIncludeCompanyObjectsInformation(INCLUDE_COMPANY_OBJECTS_INFORMATION);
        itemOptionsRequest.setIncludeEmailCopy(INCLUDE_EMAIL_COPY);
        itemOptionsRequest.setIncludeGoodStandingInformation(INCLUDE_GOOD_STANDING_INFORMATION);
        itemOptionsRequest.setRegisteredOfficeAddressDetails(REGISTERED_OFFICE_ADDRESS_DETAILS);
        itemOptionsRequest.setSecretaryDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        itemOptionsRequest.setSurname(SURNAME);

        final CertificateItemCreate newItem = new CertificateItemCreate();
        newItem.setCompanyNumber(COMPANY_NUMBER);
        newItem.setItemOptions(itemOptionsRequest);
        newItem.setQuantity(QUANTITY);
        newItem.setCustomerReference(CUSTOMER_REFERENCE);

        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertificateType(CERTIFICATE_TYPE);
        options.setCollectionLocation(COLLECTION_LOCATION);
        options.setContactNumber(CONTACT_NUMBER);
        options.setDeliveryMethod(DELIVERY_METHOD);
        options.setDeliveryTimescale(DELIVERY_TIMESCALE);
        options.setDirectorDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        options.setForename(FORENAME);
        options.setIncludeCompanyObjectsInformation(INCLUDE_COMPANY_OBJECTS_INFORMATION);
        options.setIncludeEmailCopy(INCLUDE_EMAIL_COPY);
        options.setIncludeGoodStandingInformation(INCLUDE_GOOD_STANDING_INFORMATION);
        options.setRegisteredOfficeAddressDetails(REGISTERED_OFFICE_ADDRESS_DETAILS);
        options.setSecretaryDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        options.setSurname(SURNAME);

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "read"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Fails to create certificate item that fails validation")
    void createCertificateItemFailsToCreateCertificateItem() throws Exception {

        // Given
        final CertificateItemOptionsRequest itemOptionsRequest = new CertificateItemOptionsRequest();
        itemOptionsRequest.setDeliveryTimescale(DELIVERY_TIMESCALE);

        final CertificateItemCreate newItem = new CertificateItemCreate();
        newItem.setItemOptions(itemOptionsRequest);
        newItem.setQuantity(QUANTITY);
        newItem.setEtag(TOKEN_ETAG);
        newItem.setLinks(LINKS);
        newItem.setPostageCost(POSTAGE_COST);
        newItem.setTotalItemCost(TOKEN_TOTAL_ITEM_COST);

        when(idGeneratorService.autoGenerateId()).thenReturn(EXPECTED_ITEM_ID);

        final ApiResponse expectedValidationError = new ApiResponse(asList(
                new uk.gov.companieshouse.api.error.ApiError("company-number-is-null", "company_number", ApiErrors.STRING_LOCATION_TYPE, ApiErrors.ERROR_TYPE_VALIDATION),
                new uk.gov.companieshouse.api.error.ApiError("etag: must be null", "etag", ApiErrors.STRING_LOCATION_TYPE, ApiErrors.ERROR_TYPE_VALIDATION),
                new uk.gov.companieshouse.api.error.ApiError("links: must be null", "links", ApiErrors.STRING_LOCATION_TYPE, ApiErrors.ERROR_TYPE_VALIDATION),
                new uk.gov.companieshouse.api.error.ApiError("postage_cost: must be null", "postage_cost", ApiErrors.STRING_LOCATION_TYPE, ApiErrors.ERROR_TYPE_VALIDATION),
                new uk.gov.companieshouse.api.error.ApiError("total_item_cost: must be null", "total_item_cost", ApiErrors.STRING_LOCATION_TYPE, ApiErrors.ERROR_TYPE_VALIDATION)
        ));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
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
        final CertificateItemOptionsRequest itemOptionsRequest = new CertificateItemOptionsRequest();
        itemOptionsRequest.setDeliveryTimescale(DeliveryTimescale.SAME_DAY);

        final CertificateItemCreate newItem = new CertificateItemCreate();
        newItem.setCompanyNumber(COMPANY_NUMBER);
        newItem.setItemOptions(itemOptionsRequest);
        newItem.setQuantity(QUANTITY);

        when(idGeneratorService.autoGenerateId()).thenReturn(EXPECTED_ITEM_ID);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INVALID_DELIVERY_TIMESCALE_MESSAGE));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeSameDayDeliveryTimescaleInvalid(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemWasNotSaved(EXPECTED_ITEM_ID);
    }

    @Test
    @DisplayName("Fails to create certificate item with an invalid company status")
    void createCertificateItemFailsToCreateCertificateItemWithInvalidCompanyStatus() throws Exception {

        // Given
        final CertificateItemOptionsRequest itemOptionsRequest = new CertificateItemOptionsRequest();

        final CertificateItemCreate newItem = new CertificateItemCreate();
        newItem.setCompanyNumber(COMPANY_NUMBER);
        newItem.setItemOptions(itemOptionsRequest);
        newItem.setQuantity(QUANTITY);

        when(idGeneratorService.autoGenerateId()).thenReturn(EXPECTED_ITEM_ID);
        when(certificateTypeMapperIF.mapToCertificateType(any())).thenReturn(new CertificateTypeMapResult(ApiErrors.ERR_COMPANY_STATUS_INVALID));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error", is(ApiErrors.ERR_COMPANY_STATUS_INVALID.getError())))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemWasNotSaved(EXPECTED_ITEM_ID);
    }

    @Test
    @DisplayName("Fails to create certificate item with an invalid collection location")
    void createCertificateItemFailsToCreateCertificateItemWithInvalidCollectionLocation() throws Exception {

        // Given
        final CertificateItemOptionsRequest itemOptionsRequest = new CertificateItemOptionsRequest();
        itemOptionsRequest.setCollectionLocation(COLLECTION_LOCATION);

        final CertificateItemCreate newItem = new CertificateItemCreate();
        newItem.setCompanyNumber(COMPANY_NUMBER);
        newItem.setItemOptions(itemOptionsRequest);
        newItem.setQuantity(QUANTITY);
        when(idGeneratorService.autoGenerateId()).thenReturn(EXPECTED_ITEM_ID);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INVALID_COLLECTION_LOCATION_MESSAGE));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeBelfastCollectionLocationInvalid(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemWasNotSaved(EXPECTED_ITEM_ID);
    }

    @Test
    @DisplayName("Fails to create certificate item with missing collection details")
    void createCertificateItemFailsToCreateCertificateItemWithMissingCollectionDetails() throws Exception {

        // Given
        final CertificateItemOptionsRequest itemOptionsRequest = new CertificateItemOptionsRequest();
        itemOptionsRequest.setDeliveryMethod(DeliveryMethod.COLLECTION);

        final CertificateItemCreate newItem = new CertificateItemCreate();
        newItem.setCompanyNumber(COMPANY_NUMBER);
        newItem.setItemOptions(itemOptionsRequest);
        newItem.setQuantity(QUANTITY);
        when(idGeneratorService.autoGenerateId()).thenReturn(EXPECTED_ITEM_ID);
        when(companyService.getCompanyProfile(any())).thenReturn(companyProfileResource);
        when(companyProfileResource.getCompanyType()).thenReturn(CompanyType.LIMITED_COMPANY.getCompanyType());
        when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
        when(certificateTypeMapperIF.mapToCertificateType(companyProfileResource)).thenReturn(new CertificateTypeMapResult(CertificateType.INCORPORATION));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error", is(ApiErrors.ERR_COLLECTION_LOCATION_REQUIRED.getError())))
                .andExpect(jsonPath("$.errors[1].error", is(ApiErrors.ERR_FORENAME_REQUIRED.getError())))
                .andExpect(jsonPath("$.errors[2].error", is(ApiErrors.ERR_SURNAME_REQUIRED.getError())))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemWasNotSaved(EXPECTED_ITEM_ID);
    }

    @Test
    @DisplayName("Fails to create certificate item with an invalid delivery method")
    void createCertificateItemFailsToCreateCertificateItemWithInvalidDeliveryMethod() throws Exception {

        // Given
        final CertificateItemOptionsRequest itemOptionsRequest = new CertificateItemOptionsRequest();
        itemOptionsRequest.setDeliveryMethod(DELIVERY_METHOD);

        final CertificateItemCreate newItem = new CertificateItemCreate();
        newItem.setCompanyNumber(COMPANY_NUMBER);
        newItem.setItemOptions(itemOptionsRequest);
        newItem.setQuantity(QUANTITY);
        when(idGeneratorService.autoGenerateId()).thenReturn(EXPECTED_ITEM_ID);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INVALID_DELIVERY_METHOD_MESSAGE));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makePostalDeliveryMethodInvalid(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemWasNotSaved(EXPECTED_ITEM_ID);
    }

    @Test
    @DisplayName("Fails to create certificate item with include Company objects, good standing," +
            " registered office details, secretary details or director details true for dissolution")
    void createCertificateItemFailsToCreateCertificateItemWithIncludeCompanyObjectsGoodStandingOfficeAddressSecretaryDetailsDirectorDetails()
            throws Exception {

        // Given
        final CertificateItemOptionsRequest itemOptionsRequest = new CertificateItemOptionsRequest();
        itemOptionsRequest.setIncludeCompanyObjectsInformation(true);
        itemOptionsRequest.setIncludeGoodStandingInformation(true);
        itemOptionsRequest.setRegisteredOfficeAddressDetails(REGISTERED_OFFICE_ADDRESS_DETAILS);
        itemOptionsRequest.setSecretaryDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        itemOptionsRequest.setDirectorDetails(DIRECTOR_OR_SECRETARY_DETAILS);

        final CertificateItemCreate newItem = new CertificateItemCreate();
        newItem.setCompanyNumber(COMPANY_NUMBER);
        newItem.setItemOptions(itemOptionsRequest);
        newItem.setQuantity(QUANTITY);
        when(idGeneratorService.autoGenerateId()).thenReturn(EXPECTED_ITEM_ID);
        when(companyService.getCompanyProfile(any())).thenReturn(companyProfileResource);
        when(companyProfileResource.getCompanyType()).thenReturn(CompanyType.LIMITED_COMPANY.getCompanyType());
        when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.DISSOLVED);
        when(certificateTypeMapperIF.mapToCertificateType(companyProfileResource)).thenReturn(new CertificateTypeMapResult(CertificateType.DISSOLUTION));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error", is(ApiErrors.ERR_INCLUDE_COMPANY_OBJECTS_INFORMATION_SUPPLIED.getError())))
                .andExpect(jsonPath("$.errors[1].error", is(ApiErrors.ERR_REGISTERED_OFFICE_ADDRESS_DETAILS_SUPPLIED.getError())))
                .andExpect(jsonPath("$.errors[2].error", is(ApiErrors.ERR_SECRETARY_DETAILS_SUPPLIED.getError())))
                .andExpect(jsonPath("$.errors[3].error", is(ApiErrors.ERR_DIRECTOR_DETAILS_SUPPLIED.getError())))
                .andExpect(jsonPath("$.errors[4].error", is(ApiErrors.ERR_INCLUDE_GOOD_STANDING_INFORMATION_SUPPLIED.getError())))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemWasNotSaved(EXPECTED_ITEM_ID);
    }

    @Test
    @DisplayName("Fails to create certificate item with include email copy true for standard delivery timescale")
    void createCertificateItemFailsToCreateCertificateItemWithIncludeEmailCopyForStandardDeliveryTimescale()
            throws Exception {

        // Given
        final CertificateItemOptionsRequest itemOptionsRequest = new CertificateItemOptionsRequest();
        itemOptionsRequest.setDeliveryTimescale(DeliveryTimescale.STANDARD);
        itemOptionsRequest.setIncludeEmailCopy(true);

        final CertificateItemCreate newItem = new CertificateItemCreate();
        newItem.setCompanyNumber(COMPANY_NUMBER);
        newItem.setItemOptions(itemOptionsRequest);
        newItem.setQuantity(QUANTITY);
        when(idGeneratorService.autoGenerateId()).thenReturn(EXPECTED_ITEM_ID);
        when(companyService.getCompanyProfile(any())).thenReturn(companyProfileResource);
        when(companyProfileResource.getCompanyType()).thenReturn(CompanyType.LIMITED_COMPANY.getCompanyType());
        when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.DISSOLVED);
        when(certificateTypeMapperIF.mapToCertificateType(companyProfileResource)).thenReturn(new CertificateTypeMapResult(CertificateType.INCORPORATION));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error", is(ApiErrors.ERR_INCLUDE_EMAIL_COPY_NOT_ALLOWED.getError())))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemWasNotSaved(EXPECTED_ITEM_ID);
    }

    @Test
    @DisplayName("Fails to create certificate item with include email copy true for default delivery timescale")
    void createCertificateItemFailsToCreateCertificateItemWithIncludeEmailCopyForDefaultDeliveryTimescale()
            throws Exception {

        // Given
        final CertificateItemOptionsRequest itemOptionsRequest = new CertificateItemOptionsRequest();
        itemOptionsRequest.setIncludeEmailCopy(true);

        final CertificateItemCreate newItem = new CertificateItemCreate();
        newItem.setCompanyNumber(COMPANY_NUMBER);
        newItem.setItemOptions(itemOptionsRequest);
        newItem.setQuantity(QUANTITY);
        when(idGeneratorService.autoGenerateId()).thenReturn(EXPECTED_ITEM_ID);
        when(companyService.getCompanyProfile(any())).thenReturn(companyProfileResource);
        when(certificateTypeMapperIF.mapToCertificateType(companyProfileResource)).thenReturn(new CertificateTypeMapResult(CertificateType.INCORPORATION));
        when(companyProfileResource.getCompanyType()).thenReturn(CompanyType.LIMITED_COMPANY.getCompanyType());
        when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error").value(is("include-email-copy-is-true-error")))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemWasNotSaved(EXPECTED_ITEM_ID);
    }

    @Test
    @DisplayName("Fails to create certificate item with an invalid include DOB type")
    void createCertificateItemFailsToCreateCertificateItemWithInvalidIncludeDobType() throws Exception {

        // Given
        final DirectorOrSecretaryDetails director = new DirectorOrSecretaryDetails();
        director.setIncludeDobType(INCLUDE_DOB_TYPE);

        final CertificateItemOptionsRequest itemOptionsRequest = new CertificateItemOptionsRequest();
        itemOptionsRequest.setDirectorDetails(director);

        final CertificateItemCreate newItem = new CertificateItemCreate();
        newItem.setItemOptions(itemOptionsRequest);
        newItem.setCompanyNumber(COMPANY_NUMBER);
        newItem.setQuantity(QUANTITY);
        when(idGeneratorService.autoGenerateId()).thenReturn(EXPECTED_ITEM_ID);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INVALID_INCLUDE_DOB_TYPE_MESSAGE));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makePartialIncludeDobTypeInvalid(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemWasNotSaved(EXPECTED_ITEM_ID);
    }

    @Test
    @DisplayName("Fails to create certificate item with an invalid include address records type")
    void createCertificateItemFailsToCreateCertificateItemWithInvalidIncludeAddressRecordsType() throws Exception {

        // Given
        final RegisteredOfficeAddressDetails registeredOfficeAddressDetails = new RegisteredOfficeAddressDetails();
        registeredOfficeAddressDetails.setIncludeAddressRecordsType(INCLUDE_ADDRESS_RECORDS_TYPE);

        final CertificateItemOptionsRequest itemOptionsRequest = new CertificateItemOptionsRequest();
        itemOptionsRequest.setRegisteredOfficeAddressDetails(registeredOfficeAddressDetails);

        final CertificateItemCreate newItem = new CertificateItemCreate();
        newItem.setCompanyNumber(COMPANY_NUMBER);
        newItem.setItemOptions(itemOptionsRequest);
        newItem.setQuantity(QUANTITY);
        when(idGeneratorService.autoGenerateId()).thenReturn(EXPECTED_ITEM_ID);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST, singletonList(INVALID_INCLUDE_ADDRESS_RECORDS_TYPE_MESSAGE));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeCurrentIncludeAddressRecordsTypeInvalid(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemWasNotSaved(EXPECTED_ITEM_ID);
    }

    @Test
    @DisplayName("Fails to create certificate item with conflicting details settings")
    void createCertificateItemFailsToCreateCertificateItemWithConflictingDetailsSettings() throws Exception {

        // Given
        final CertificateItemOptionsRequest itemOptionsRequest = new CertificateItemOptionsRequest();
        itemOptionsRequest.setDirectorDetails(CONFLICTING_DIRECTOR_OR_SECRETARY_DETAILS);
        itemOptionsRequest.setSecretaryDetails(CONFLICTING_DIRECTOR_OR_SECRETARY_DETAILS);

        final CertificateItemCreate newItem = new CertificateItemCreate();
        newItem.setCompanyNumber(COMPANY_NUMBER);
        newItem.setItemOptions(itemOptionsRequest);
        newItem.setQuantity(QUANTITY);
        when(idGeneratorService.autoGenerateId()).thenReturn(EXPECTED_ITEM_ID);
        when(companyService.getCompanyProfile(any())).thenReturn(companyProfileResource);
        when(companyProfileResource.getCompanyType()).thenReturn(CompanyType.LIMITED_COMPANY.getCompanyType());
        when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
        when(certificateTypeMapperIF.mapToCertificateType(companyProfileResource)).thenReturn(new CertificateTypeMapResult(CertificateType.INCORPORATION));

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeCurrentIncludeAddressRecordsTypeInvalid(newItem)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error", is(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_ADDRESS.getError())))
                .andExpect(jsonPath("$.errors[1].error", is(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_NATIONALITY.getError())))
                .andExpect(jsonPath("$.errors[2].error", is(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_OCCUPATION.getError())))
                .andExpect(jsonPath("$.errors[3].error", is(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_ADDRESS.getError())))
                .andExpect(jsonPath("$.errors[4].error", is(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_NATIONALITY.getError())))
                .andExpect(jsonPath("$.errors[5].error", is(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_OCCUPATION.getError())))
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
        newItem.setCompanyNumber(COMPANY_NUMBER);
        newItem.setCompanyName(EXPECTED_COMPANY_NAME);
        newItem.setId(EXPECTED_ITEM_ID);
        newItem.setQuantity(QUANTITY);
        newItem.setUserId(ERIC_IDENTITY_VALUE);
        newItem.setCustomerReference(CUSTOMER_REFERENCE);
        newItem.setEtag(TOKEN_ETAG);
        newItem.setLinks(LINKS);
        repository.save(newItem);

        final CertificateItemResponse expectedItem = new CertificateItemResponse();
        expectedItem.setCompanyNumber(COMPANY_NUMBER);
        expectedItem.setCompanyName(EXPECTED_COMPANY_NAME);
        expectedItem.setQuantity(QUANTITY);
        expectedItem.setId(EXPECTED_ITEM_ID);

        final List<ItemCosts> costs = generateExpectedCosts(QUANTITY, DELIVERY_TIMESCALE);
        expectedItem.setItemCosts(costs);
        expectedItem.setCustomerReference(CUSTOMER_REFERENCE);
        expectedItem.setEtag(TOKEN_ETAG);
        expectedItem.setLinks(LINKS);
        expectedItem.setPostageCost(POSTAGE_COST);
        expectedItem.setTotalItemCost(calculateExpectedTotalItemCost(costs, POSTAGE_COST));

        // When and then
        mockMvc.perform(get(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "read"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedItem)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Return not found when a certificate item does not exist")
    void getCertificateItemReturnsNotFound() throws Exception {
        // When and then
        mockMvc.perform(get(CERTIFICATES_URL + "CHS0")
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "read"))
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
        mockMvc.perform(get(CERTIFICATES_URL + EXPECTED_ITEM_ID)
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
        mockMvc.perform(get(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "read"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Return unauthorised if the user does not have the right token permission")
    void getCertificateItemReturnsUnauthorisedIfMissingTokenPermission() throws Exception {
        // Given
        // Create certificate item in database
        final CertificateItem newItem = new CertificateItem();
        newItem.setId(EXPECTED_ITEM_ID);
        newItem.setQuantity(QUANTITY);
        newItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(newItem);


        // When and then
        mockMvc.perform(get(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "other"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Successfully updates certificate item")
    void updateCertificateItemSuccessfullyUpdatesCertificateItem() throws Exception {

        // Given
        final CertificateItem savedItem = new CertificateItem();
        savedItem.setCompanyNumber(PREVIOUS_COMPANY_NUMBER);
        savedItem.setCompanyName(PREVIOUS_COMPANY_NAME);
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        savedItem.setCustomerReference(CUSTOMER_REFERENCE);
        savedItem.setDescription(DESCRIPTION);
        savedItem.setDescriptionValues(singletonMap(COMPANY_NUMBER_KEY, COMPANY_NUMBER));

        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertificateType(CertificateType.INCORPORATION_WITH_ALL_NAME_CHANGES);
        options.setCompanyType(CompanyType.LIMITED_COMPANY.getCompanyType());
        options.setCompanyStatus(PREVIOUS_COMPANY_STATUS.getStatusName());
        options.setCollectionLocation(COLLECTION_LOCATION);
        options.setContactNumber(CONTACT_NUMBER);
        options.setDeliveryMethod(DELIVERY_METHOD);
        options.setDeliveryTimescale(DELIVERY_TIMESCALE);
        options.setDirectorDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        options.setForename(FORENAME);
        options.setIncludeCompanyObjectsInformation(INCLUDE_COMPANY_OBJECTS_INFORMATION);
        options.setIncludeEmailCopy(INCLUDE_EMAIL_COPY);
        options.setIncludeGoodStandingInformation(INCLUDE_GOOD_STANDING_INFORMATION);
        options.setRegisteredOfficeAddressDetails(REGISTERED_OFFICE_ADDRESS_DETAILS);
        options.setSecretaryDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        options.setSurname(SURNAME);
        savedItem.setItemOptions(options);
        savedItem.setLinks(LINKS);
        repository.save(savedItem);

        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptionsRequest itemUpdateOptions = new CertificateItemOptionsRequest();
        itemUpdate.setQuantity(UPDATED_QUANTITY);
        itemUpdate.setCustomerReference(UPDATED_CUSTOMER_REFERENCE);
        itemUpdate.setItemOptions(itemUpdateOptions);
        itemUpdateOptions.setCollectionLocation(UPDATED_COLLECTION_LOCATION);
        itemUpdateOptions.setContactNumber(UPDATED_CONTACT_NUMBER);
        itemUpdateOptions.setDeliveryMethod(UPDATED_DELIVERY_METHOD);
        itemUpdateOptions.setDeliveryTimescale(UPDATED_DELIVERY_TIMESCALE);
        itemUpdateOptions.setDirectorDetails(UPDATED_DIRECTOR_OR_SECRETARY_DETAILS);
        itemUpdateOptions.setForename(FORENAME);
        itemUpdateOptions.setIncludeCompanyObjectsInformation(UPDATED_INCLUDE_COMPANY_OBJECTS_INFORMATION);
        itemUpdateOptions.setIncludeEmailCopy(UPDATED_INCLUDE_EMAIL_COPY);
        itemUpdateOptions.setIncludeGoodStandingInformation(UPDATED_INCLUDE_GOOD_STANDING_INFORMATION);
        itemUpdateOptions.setRegisteredOfficeAddressDetails(UPDATED_REGISTERED_OFFICE_ADDRESS_DETAILS);
        itemUpdateOptions.setSecretaryDetails(UPDATED_DIRECTOR_OR_SECRETARY_DETAILS);
        itemUpdateOptions.setSurname(UPDATED_SURNAME);

        final CertificateItemOptions expectedCertificateItemOptions = new CertificateItemOptions();
        expectedCertificateItemOptions.setCertificateType(CertificateType.INCORPORATION_WITH_ALL_NAME_CHANGES);
        expectedCertificateItemOptions.setCompanyType(CompanyType.LIMITED_COMPANY.getCompanyType());
        expectedCertificateItemOptions.setCompanyStatus(PREVIOUS_COMPANY_STATUS.getStatusName());
        expectedCertificateItemOptions.setCollectionLocation(UPDATED_COLLECTION_LOCATION);
        expectedCertificateItemOptions.setContactNumber(UPDATED_CONTACT_NUMBER);
        expectedCertificateItemOptions.setDeliveryMethod(UPDATED_DELIVERY_METHOD);
        expectedCertificateItemOptions.setDeliveryTimescale(UPDATED_DELIVERY_TIMESCALE);
        expectedCertificateItemOptions.setDirectorDetails(UPDATED_DIRECTOR_OR_SECRETARY_DETAILS);
        expectedCertificateItemOptions.setForename(FORENAME);
        expectedCertificateItemOptions.setIncludeCompanyObjectsInformation(UPDATED_INCLUDE_COMPANY_OBJECTS_INFORMATION);
        expectedCertificateItemOptions.setIncludeEmailCopy(UPDATED_INCLUDE_EMAIL_COPY);
        expectedCertificateItemOptions.setIncludeGoodStandingInformation(UPDATED_INCLUDE_GOOD_STANDING_INFORMATION);
        expectedCertificateItemOptions.setRegisteredOfficeAddressDetails(UPDATED_REGISTERED_OFFICE_ADDRESS_DETAILS);
        expectedCertificateItemOptions.setSecretaryDetails(UPDATED_DIRECTOR_OR_SECRETARY_DETAILS);
        expectedCertificateItemOptions.setSurname(UPDATED_SURNAME);

        final CertificateItemResponse expectedItem = new CertificateItemResponse();
        expectedItem.setCompanyNumber(PREVIOUS_COMPANY_NUMBER);
        expectedItem.setCompanyName(PREVIOUS_COMPANY_NAME);
        expectedItem.setQuantity(UPDATED_QUANTITY);
        expectedItem.setItemOptions(expectedCertificateItemOptions);
        expectedItem.setCustomerReference(UPDATED_CUSTOMER_REFERENCE);
        expectedItem.setDescription(DESCRIPTION);
        expectedItem.setDescriptionValues(singletonMap(COMPANY_NUMBER_KEY, PREVIOUS_COMPANY_NUMBER));

        final List<ItemCosts> costs = generateExpectedCosts(UPDATED_QUANTITY, UPDATED_DELIVERY_TIMESCALE);
        expectedItem.setItemCosts(costs);
        expectedItem.setEtag(TOKEN_ETAG);
        expectedItem.setLinks(LINKS);
        expectedItem.setPostageCost(POSTAGE_COST);
        expectedItem.setTotalItemCost(calculateExpectedTotalItemCost(costs, POSTAGE_COST));

        when(etagGenerator.generateEtag()).thenReturn(TOKEN_ETAG);

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "update"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedItem)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        final Optional<CertificateItem> retrievedCertificateItem = repository.findById(EXPECTED_ITEM_ID);
        assertThat(retrievedCertificateItem.isPresent(), is(true));
        assertThat(retrievedCertificateItem.get().getId(), is(EXPECTED_ITEM_ID));
        assertThat(retrievedCertificateItem.get().getCompanyNumber(),
                is(PREVIOUS_COMPANY_NUMBER));
        assertThat(retrievedCertificateItem.get().getCompanyName(),
                is(PREVIOUS_COMPANY_NAME));
        assertThat(retrievedCertificateItem.get().getQuantity(), is(UPDATED_QUANTITY));
        assertThat(retrievedCertificateItem.get().getItemOptions(),
                is(expectedCertificateItemOptions));
        assertThat(retrievedCertificateItem.get().getLinks(), is(LINKS));

        // Costs are calculated on the fly and are NOT to be saved to the DB.
        assertThat(retrievedCertificateItem.get().getItemCosts(), is(nullValue()));
        assertThat(retrievedCertificateItem.get().getPostageCost(), is(nullValue()));
        assertThat(retrievedCertificateItem.get().getTotalItemCost(), is(nullValue()));

        assertItemOptionsEnumValueNamesSavedCorrectly(ITEM_OPTIONS_ENUM_FIELDS);

        verify(etagGenerator).generateEtag();
    }

    @Test
    @DisplayName("Fails to create certificate item with incorrect token permission")
    void updateCertificateItemUnauthorizedTokenPermission() throws Exception {

        // Given
        final CertificateItem savedItem = new CertificateItem();
        savedItem.setCompanyNumber(PREVIOUS_COMPANY_NUMBER);
        savedItem.setCompanyName(PREVIOUS_COMPANY_NAME);
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        savedItem.setCompanyNumber(COMPANY_NUMBER);
        savedItem.setCustomerReference(CUSTOMER_REFERENCE);
        savedItem.setDescription(DESCRIPTION);
        savedItem.setDescriptionValues(singletonMap(COMPANY_NUMBER_KEY, COMPANY_NUMBER));

        repository.save(savedItem);

        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("Reports failure to find certificate item")
    void updateCertificateItemReportsFailureToFindItem() throws Exception {

        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        itemUpdate.setQuantity(UPDATED_QUANTITY);
        final CertificateItemOptionsRequest options = new CertificateItemOptionsRequest();
        options.setDeliveryTimescale(DELIVERY_TIMESCALE);
        itemUpdate.setItemOptions(options);

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "update"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Should not modify user_id when performing an update")
    void updateCertificateItemDoesNotModifyWhenPerformingAnUpdate() throws Exception {
        // Given
        // TODO: this may fail
        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "update"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content("{\"company_number\":\"00006444\", \"user_id\":\"invalid\"}"))
                .andExpect(status().isBadRequest());

        final Optional<CertificateItem> foundItem = repository.findById(EXPECTED_ITEM_ID);
        assertTrue(foundItem.isPresent());
        Assertions.assertEquals(ERIC_IDENTITY_VALUE, foundItem.get().getUserId());
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
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "update"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error", is("quantity-error")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Unknown field is ignored")
    void updateCertificateItemIgnoresUnknownField() throws Exception {

        // Given
        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setCompanyNumber(COMPANY_NUMBER);
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryTimescale(DELIVERY_TIMESCALE);
        options.setCompanyType("ltd");
        options.setCompanyStatus(CompanyStatus.ACTIVE.getStatusName());
        savedItem.setItemOptions(options);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        final TestDTO itemUpdate = new TestDTO("Unknown field value");

        when(companyService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(
                new CompanyProfileResource(EXPECTED_COMPANY_NAME, "ltd", EXPECTED_COMPANY_STATUS));

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "update"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(EXPECTED_ITEM_ID)))
                .andExpect(jsonPath("$.quantity", is(QUANTITY)))
                .andExpect(jsonPath("$.company_number", is(COMPANY_NUMBER)))
                .andExpect(jsonPath("$.item_options.delivery_timescale", is(DELIVERY_TIMESCALE.getJsonName())))
                .andExpect(jsonPath("$.item_options.company_type", is(CompanyType.LIMITED_COMPANY.getCompanyType())))
                .andExpect(jsonPath("$.item_options.company_status", is(CompanyStatus.ACTIVE.getStatusName())))
                .andExpect(jsonPath("$.item_costs", hasSize(5)))
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
        itemUpdate.setCompanyName(UPDATED_COMPANY_NAME);
        itemUpdate.setDescriptionValues(TOKEN_VALUES);
        itemUpdate.setItemCosts(TOKEN_ITEM_COSTS);
        itemUpdate.setKind(TOKEN_STRING);
        itemUpdate.setEtag(TOKEN_ETAG);
        itemUpdate.setLinks(LINKS);
        itemUpdate.setId(UPDATED_ITEM_ID);
        itemUpdate.setPostageCost(POSTAGE_COST);
        itemUpdate.setTotalItemCost(TOKEN_TOTAL_ITEM_COST);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "update"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error", is("company-name-error")))
                .andExpect(jsonPath("$.errors[1].error", is("description-values-error")))
                .andExpect(jsonPath("$.errors[2].error", is("etag-error")))
                .andExpect(jsonPath("$.errors[3].error", is("id-error")))
                .andExpect(jsonPath("$.errors[4].error", is("item-costs-error")))
                .andExpect(jsonPath("$.errors[5].error", is("kind-error")))
                .andExpect(jsonPath("$.errors[6].error", is("links-error")))
                .andExpect(jsonPath("$.errors[7].error", is("postage-cost-error")))
                .andExpect(jsonPath("$.errors[8].error", is("total-item-cost-error")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Rejects update request containing an invalid delivery timescale")
    void updateCertificateItemRejectsInvalidDeliveryTimescale() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptionsRequest options = new CertificateItemOptionsRequest();
        options.setDeliveryTimescale(DeliveryTimescale.SAME_DAY);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "update"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content(makeSameDayDeliveryTimescaleInvalid(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error", is(ApiErrors.ERR_JSON_PROCESSING.getError())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Rejects update request containing an invalid collection location")
    void updateCertificateItemRejectsInvalidCollectionLocation() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptionsRequest options = new CertificateItemOptionsRequest();
        options.setCollectionLocation(COLLECTION_LOCATION);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "update"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content(makeBelfastCollectionLocationInvalid(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error").value(is(ApiErrors.ERR_JSON_PROCESSING.getError())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Rejects update request with missing collection details")
    void updateCertificateItemRejectsMissingCollectionDetails() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptionsRequest options = new CertificateItemOptionsRequest();
        options.setDeliveryMethod(DeliveryMethod.COLLECTION);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        CertificateItemOptions savedOptions = new CertificateItemOptions();
        savedOptions.setCompanyType(CompanyType.LIMITED_COMPANY.getCompanyType());
        savedOptions.setCompanyStatus(CompanyStatus.ACTIVE.getStatusName());
        savedItem.setItemOptions(savedOptions);
        repository.save(savedItem);

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "update"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error", is(ApiErrors.ERR_COLLECTION_LOCATION_REQUIRED.getError())))
                .andExpect(jsonPath("$.errors[1].error", is(ApiErrors.ERR_FORENAME_REQUIRED.getError())))
                .andExpect(jsonPath("$.errors[2].error", is(ApiErrors.ERR_SURNAME_REQUIRED.getError())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Rejects update request containing an invalid delivery method")
    void updateCertificateItemRejectsInvalidDeliveryMethod() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptionsRequest options = new CertificateItemOptionsRequest();
        options.setDeliveryMethod(DELIVERY_METHOD);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "update"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content(makePostalDeliveryMethodInvalid(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error").value(is(ApiErrors.ERR_JSON_PROCESSING.getError())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Rejects update request for dissolved company if good standing or company objects requested")
    void testRejectUpdateRequestForDissolvedCompanyIfGoodStandingOrCompanyObjectsRequested() throws Exception {
        // Given
        when(companyService.getCompanyProfile(any())).thenReturn(companyProfileResource);
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptionsRequest options = new CertificateItemOptionsRequest();
        options.setIncludeCompanyObjectsInformation(true);
        options.setIncludeGoodStandingInformation(true);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        CertificateItemOptions savedOptions = new CertificateItemOptions();
        savedOptions.setCertificateType(CertificateType.DISSOLUTION);
        savedOptions.setCompanyType("limited");
        savedOptions.setCompanyStatus(CompanyStatus.DISSOLVED.getStatusName());
        savedItem.setItemOptions(savedOptions);
        repository.save(savedItem);

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "update"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error").value(is(ApiErrors.ERR_INCLUDE_COMPANY_OBJECTS_INFORMATION_SUPPLIED.getError())))
                .andExpect(jsonPath("$.errors[1].error").value(is(ApiErrors.ERR_INCLUDE_GOOD_STANDING_INFORMATION_SUPPLIED.getError())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Rejects update request with include email copy and default delivery timescale")
    void updateCertificateItemRejectsRequestWithIncludeEmailCopyAndDefaultDeliveryTimescale() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptionsRequest options = new CertificateItemOptionsRequest();
        options.setIncludeEmailCopy(true);
        itemUpdate.setItemOptions(options);

        when(companyService.getCompanyProfile(any())).thenReturn(companyProfileResource);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        CertificateItemOptions savedOptions = new CertificateItemOptions();
        savedOptions.setCompanyType(CompanyType.LIMITED_COMPANY.getCompanyType());
        savedOptions.setCompanyStatus(CompanyStatus.ACTIVE.getStatusName());
        savedItem.setItemOptions(savedOptions);
        repository.save(savedItem);

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "update"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error").value(is(ApiErrors.ERR_INCLUDE_EMAIL_COPY_NOT_ALLOWED.getError())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Rejects update request with include email copy updating standard delivery timescale item")
    void updateCertificateItemRejectsRequestWithIncludeEmailCopyUpdatingStandardDeliveryTimescaleItem()
            throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptionsRequest options = new CertificateItemOptionsRequest();
        options.setIncludeEmailCopy(true);
        itemUpdate.setItemOptions(options);
        when(companyService.getCompanyProfile(any())).thenReturn(companyProfileResource);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        final CertificateItemOptions savedOptions = new CertificateItemOptions();
        savedOptions.setCompanyType(CompanyType.LIMITED_COMPANY.getCompanyType());
        savedOptions.setCompanyStatus(CompanyStatus.ACTIVE.getStatusName());
        savedOptions.setDeliveryTimescale(DeliveryTimescale.STANDARD);
        savedItem.setItemOptions(savedOptions);
        repository.save(savedItem);

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "update"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content(objectMapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error").value(is(ApiErrors.ERR_INCLUDE_EMAIL_COPY_NOT_ALLOWED.getError())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Rejects update request containing an invalid include DOB type")
    void updateCertificateItemRejectsInvalidIncludeDobType() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final DirectorOrSecretaryDetails director = new DirectorOrSecretaryDetails();
        director.setIncludeDobType(INCLUDE_DOB_TYPE);
        final CertificateItemOptionsRequest options = new CertificateItemOptionsRequest();
        options.setDirectorDetails(director);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "update"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content(makePartialIncludeDobTypeInvalid(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error", is(ApiErrors.ERR_JSON_PROCESSING.getError())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Rejects update request containing an invalid include address records type")
    void updateCertificateItemRejectsInvalidIncludeAddressRecordsType() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final RegisteredOfficeAddressDetails registeredOfficeAddressDetails = new RegisteredOfficeAddressDetails();
        registeredOfficeAddressDetails.setIncludeAddressRecordsType(INCLUDE_ADDRESS_RECORDS_TYPE);
        final CertificateItemOptionsRequest options = new CertificateItemOptionsRequest();
        options.setRegisteredOfficeAddressDetails(registeredOfficeAddressDetails);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        repository.save(savedItem);

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "update"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content(makeCurrentIncludeAddressRecordsTypeInvalid(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error").value(is(ApiErrors.ERR_JSON_PROCESSING.getError())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Rejects update request with conflicting details settings")
    void updateCertificateItemRejectsConflictingDetailsSettings() throws Exception {
        // Given
        final PatchValidationCertificateItemDTO itemUpdate = new PatchValidationCertificateItemDTO();
        final CertificateItemOptionsRequest options = new CertificateItemOptionsRequest();
        options.setDirectorDetails(CONFLICTING_DIRECTOR_OR_SECRETARY_DETAILS);
        options.setSecretaryDetails(CONFLICTING_DIRECTOR_OR_SECRETARY_DETAILS);
        itemUpdate.setItemOptions(options);

        final CertificateItem savedItem = new CertificateItem();
        savedItem.setId(EXPECTED_ITEM_ID);
        savedItem.setQuantity(QUANTITY);
        savedItem.setUserId(ERIC_IDENTITY_VALUE);
        CertificateItemOptions savedOptions = new CertificateItemOptions();
        savedOptions.setCompanyType("limited");
        savedOptions.setCompanyStatus("active");
        savedItem.setItemOptions(savedOptions);
        repository.save(savedItem);
        when(companyService.getCompanyProfile(any())).thenReturn(companyProfileResource);

        final ApiError expectedValidationError =
                new ApiError(BAD_REQUEST,
                        asList(CONFLICTING_DIRECTOR_DETAILS_MESSAGE, CONFLICTING_SECRETARY_DETAILS_MESSAGE));

        // When and then
        mockMvc.perform(patch(CERTIFICATES_URL + EXPECTED_ITEM_ID)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "update"))
                        .contentType(PatchMediaType.APPLICATION_MERGE_PATCH)
                        .content(makeCurrentIncludeAddressRecordsTypeInvalid(itemUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error").value(is("include-address-error")))
                .andExpect(jsonPath("$.errors[1].error").value(is("include-nationality-error")))
                .andExpect(jsonPath("$.errors[2].error").value(is("include-occupation-error")))
                .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @MethodSource("provideLiquidatorsDetailsErrorFixtures")
    void correctErrorsWhenCertificateLiquidatorsDetailsAreSupplied(CertificateItemsFixture fixture)
            throws Exception {
        // Given
        certificateItemCreate.setCompanyNumber(COMPANY_NUMBER);
        certificateItemCreate.setItemOptions(certificateItemOptions);
        certificateItemCreate.setQuantity(QUANTITY);

        certificateItemOptions.setLiquidatorsDetails(fixture.getLiquidatorsDetails());
        certificateItemOptions.setIncludeGoodStandingInformation(fixture.getIncludeGoodStandingInformation());

        when(companyProfileResource.getCompanyStatus()).thenReturn(fixture.getCompanyStatus());
        when(companyService.getCompanyProfile(any())).thenReturn(companyProfileResource);
        when(companyProfileResource.getCompanyType()).thenReturn(fixture.getCompanyType().getCompanyType());
        when(companyProfileResource.getCompanyStatus()).thenReturn(fixture.getCompanyStatus());
        when(certificateTypeMapperIF.mapToCertificateType(any())).thenReturn(new CertificateTypeMapResult(CertificateType.INCORPORATION));

        final ApiResponse<Object> expectedValidationError = new ApiResponse<>(fixture.getExpectedErrors());

        // When and Then
        mockMvc.perform(post(CERTIFICATES_URL)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(certificateItemCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedValidationError)))
                .andDo(MockMvcResultHandlers.print());

        // Then
        assertItemWasNotSaved(EXPECTED_ITEM_ID);
    }

    @Test
    void whenInitialCertificateItemRequestShouldRespondWithCreatedCertificateItem() throws Exception {
        //given
        CertificateItemInitial certificateItemInitial = new CertificateItemInitial();
        certificateItemInitial.setCompanyNumber("123456");

        when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.getEnumValue("active"));
        when(companyProfileResource.getCompanyType()).thenReturn("ltd");
        when(companyProfileResource.getCompanyName()).thenReturn("ACME Limited");
        when(companyService.getCompanyProfile(any())).thenReturn(companyProfileResource);
        when(idGeneratorService.autoGenerateId()).thenReturn(EXPECTED_ITEM_ID);
        when(certificateTypeMapperIF.mapToCertificateType(any())).thenReturn(new CertificateTypeMapResult(CertificateType.INCORPORATION));

        //when
        ResultActions resultActions = mockMvc.perform(post(INITIAL_CERTIFICATE_URL)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                        .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                        .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                        .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                        .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(certificateItemInitial)));

        //then
        resultActions
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(EXPECTED_ITEM_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.company_name").value("ACME Limited"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.company_number").value("123456"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item_options").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.item_options.company_status").value("active"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item_options.company_type").value("ltd"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user_id").value(ERIC_IDENTITY_VALUE));
    }

    @Test
    void whenInitialItemRequestWithNullCompanyNumberShouldRespondWithBadRequest()
            throws Exception {
        //given
        CertificateItemInitial certificateItemInitial = new CertificateItemInitial();

        //when
        ResultActions resultActions = mockMvc.perform(post(INITIAL_CERTIFICATE_URL)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(certificateItemInitial)));

        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error", is("company-number-is-null")))
                .andExpect(jsonPath("$.errors[0].location", is("company_number")))
                .andExpect(jsonPath("$.errors[0].location_type", is("string")))
                .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenInitialItemRequestWithCompanyProfileApiNotFoundShouldReturnCompanyNotFoundError()
            throws Exception {
        //given
        CertificateItemInitial certificateItemInitial = new CertificateItemInitial();
        certificateItemInitial.setCompanyNumber("12345678");

        when(companyService.getCompanyProfile(any())).thenThrow(new CompanyNotFoundException("Error getting "
                + "company name for company number 12345678"));

        //when
        ResultActions resultActions = mockMvc.perform(post(INITIAL_CERTIFICATE_URL)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(certificateItemInitial)));

        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error", is("company-not-found")))
                .andExpect(jsonPath("$.errors[0].location", is("company_number")))
                .andExpect(jsonPath("$.errors[0].location_type", is("string")))
                .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void whenInitialItemRequestWithCompanyStatusControllerReturnsBadRequestCompanyStatusIsInvalid()
            throws Exception {
        //given
        CertificateItemInitial certificateItemInitial = new CertificateItemInitial();
        certificateItemInitial.setCompanyNumber("12345678");

        when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
        when(certificateTypeMapperIF.mapToCertificateType(companyProfileResource)).thenReturn(new CertificateTypeMapResult(ApiErrors.ERR_COMPANY_STATUS_INVALID));
        when(companyService.getCompanyProfile(any())).thenReturn(companyProfileResource);

        //when
        ResultActions resultActions = mockMvc.perform(post(INITIAL_CERTIFICATE_URL)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(REQUEST_ID_HEADER_NAME, TOKEN_REQUEST_ID_VALUE)
                .header(ERIC_IDENTITY_TYPE_HEADER_NAME, ERIC_IDENTITY_TYPE_OAUTH2_VALUE)
                .header(ERIC_IDENTITY_HEADER_NAME, ERIC_IDENTITY_VALUE)
                .header(ERIC_AUTHORISED_USER_HEADER_NAME, ERIC_AUTHORISED_USER_VALUE)
                .header(ERIC_AUTHORISED_TOKEN_PERMISSIONS_HEADER_NAME, String.format(TOKEN_PERMISSION_VALUE, "create"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(certificateItemInitial)));

        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].error", is("company-status-invalid")))
                .andExpect(jsonPath("$.errors[0].location", is("company_status")))
                .andExpect(jsonPath("$.errors[0].location_type", is("string")))
                .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * Generates the costs we expect to be calculated given the quantity of certificates and the delivery timescale.
     *
     * @param quantity  the quantity of certificates
     * @param timescale the delivery timescale, standard or same day
     * @return the expected costs
     */
    private List<ItemCosts> generateExpectedCosts(final int quantity, final DeliveryTimescale timescale) {
        final List<ItemCosts> costs = new ArrayList<>();
        final int certificateCost =
                timescale == DeliveryTimescale.SAME_DAY ? SAME_DAY_INDIVIDUAL_CERTIFICATE_COST : STANDARD_INDIVIDUAL_CERTIFICATE_COST;
        final int extraCertificateDiscount =
                timescale == DeliveryTimescale.SAME_DAY ? SAME_DAY_EXTRA_CERTIFICATE_DISCOUNT : STANDARD_EXTRA_CERTIFICATE_DISCOUNT;
        for (int certificateNumber = 1; certificateNumber <= quantity; certificateNumber++) {
            final ItemCosts cost = new ItemCosts();
            final int expectedDiscountApplied = certificateNumber > 1 ? extraCertificateDiscount : 0;
            cost.setDiscountApplied(Integer.toString(expectedDiscountApplied));
            cost.setItemCost(Integer.toString(certificateCost));
            cost.setCalculatedCost((Integer.toString(certificateCost - expectedDiscountApplied)));
            cost.setProductType(getProductType(certificateNumber, timescale));
            costs.add(cost);
        }
        return costs;
    }

    /**
     * Derives the product type from the certificate number and the delivery timescale.
     *
     * @param certificateNumber the number of the certificate (1 is the first, > 1 => additional)
     * @param timescale         the delivery timescale, standard or same day
     * @return the derived product type
     */
    private ProductType getProductType(final int certificateNumber, final DeliveryTimescale timescale) {
        if (timescale.equals(DeliveryTimescale.SAME_DAY)) {
            return certificateNumber > 1 ? ProductType.CERTIFICATE_ADDITIONAL_COPY : ProductType.CERTIFICATE_SAME_DAY;
        }
        return certificateNumber > 1 ? ProductType.CERTIFICATE_ADDITIONAL_COPY : ProductType.CERTIFICATE;
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "incorporation" certificate type value with "unknown" for validation testing purposes.
     *
     * @param newItem the item to be serialised to JSON with an incorrect certificate type value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makeIncorporationCertificateTypeInvalid(final CertificateItemCreate newItem)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(newItem).replace("incorporation", "unknown");
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "incorporation" certificate type value with "unknown" for validation testing purposes.
     *
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
     *
     * @param newItem the item to be serialised to JSON with an incorrect delivery method value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makeBelfastCollectionLocationInvalid(final CertificateItemCreate newItem)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(newItem).replace("belfast", "unknown");
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "belfast" collection location value with "unknown" for validation testing purposes.
     *
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
     *
     * @param newItem the item to be serialised to JSON with an incorrect delivery method value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makePostalDeliveryMethodInvalid(final CertificateItemCreate newItem)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(newItem).replace("postal", "unknown");
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "postal" certificate type value with "unknown" for validation testing purposes.
     *
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
     * the "same-day" delivery timescale value with "unknown" for validation testing purposes.
     *
     * @param newItem the item to be serialised to JSON with an incorrect delivery timescale value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makeSameDayDeliveryTimescaleInvalid(final CertificateItemCreate newItem)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(newItem).replace("same-day", "unknown");
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "same-day" delivery timescale value with "unknown" for validation testing purposes.
     *
     * @param itemUpdate the item to be serialised to JSON with an incorrect delivery timescale value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makeSameDayDeliveryTimescaleInvalid(final PatchValidationCertificateItemDTO itemUpdate)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(itemUpdate).replace("same-day", "unknown");
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "partial" include DOB type value with "unknown" for validation testing purposes.
     *
     * @param newItem the item to be serialised to JSON with an incorrect include DOB type value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makePartialIncludeDobTypeInvalid(final CertificateItemCreate newItem)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(newItem).replace("partial", "unknown");
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "partial" include DOB type value with "unknown" for validation testing purposes.
     *
     * @param itemUpdate the item to be serialised to JSON with an incorrect include DOB type value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makePartialIncludeDobTypeInvalid(final PatchValidationCertificateItemDTO itemUpdate)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(itemUpdate).replace("partial", "unknown");
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "current" include address records type value with "unknown" for validation testing purposes.
     *
     * @param newItem the item to be serialised to JSON with an incorrect include DOB type value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makeCurrentIncludeAddressRecordsTypeInvalid(final CertificateItemCreate newItem)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(newItem).replace("current", "unknown");
    }

    /**
     * Utility that gets the item passed it as its equivalent JSON representation, BUT replaces
     * the "current" include address records type value with "unknown" for validation testing purposes.
     *
     * @param itemUpdate the item to be serialised to JSON with an incorrect include DOB type value
     * @return the corrupted JSON representation of the item
     * @throws JsonProcessingException should something unexpected happen
     */
    private String makeCurrentIncludeAddressRecordsTypeInvalid(final PatchValidationCertificateItemDTO itemUpdate)
            throws JsonProcessingException {
        return objectMapper.writeValueAsString(itemUpdate).replace("current", "unknown");
    }

    /**
     * Verifies that the item assumed to have been created by the create item POST request can be retrieved
     * from the database using its expected ID value. Also verifies that item costs have NOT been saved to the DB.
     *
     * @param expectedItemId the expected ID of the newly created item
     */
    private void assertItemSavedCorrectly(final String expectedItemId) {
        final Optional<CertificateItem> retrievedCertificateItem = repository.findById(expectedItemId);
        assertThat(retrievedCertificateItem.isPresent(), is(true));
        assertThat(retrievedCertificateItem.get().getId(), is(expectedItemId));

        // Costs are calculated on the fly and are NOT to be saved to the DB.
        assertThat(retrievedCertificateItem.get().getItemCosts(), is(nullValue()));
        assertThat(retrievedCertificateItem.get().getPostageCost(), is(nullValue()));
        assertThat(retrievedCertificateItem.get().getTotalItemCost(), is(nullValue()));

        assertItemOptionsEnumValueNamesSavedCorrectly(ITEM_OPTIONS_ENUM_FIELDS);
    }

    /**
     * Verifies that the item that could have been created by the create item POST request cannot in fact be retrieved
     * from the database.
     *
     * @param expectedItemId the expected ID of the newly created item
     */
    private void assertItemWasNotSaved(final String expectedItemId) {
        final Optional<CertificateItem> retrievedCertificateItem = repository.findById(expectedItemId);
        assertThat(retrievedCertificateItem.isPresent(), is(false));
    }

    /**
     * Checks that the enum values have been saved in the expected format.
     *
     * @param enumFieldNames the item options enum fields to be checked
     */
    private void assertItemOptionsEnumValueNamesSavedCorrectly(final List<String> enumFieldNames) {
        final Document certificate = mongoTemplate.findById(EXPECTED_ITEM_ID, Document.class, "certificates");
        assertNotNull(certificate);
        final Document data = (Document) certificate.get("data");
        final Document itemOptions = (Document) data.get("item_options");
        for (final String field : enumFieldNames) {
            final String fieldValue = itemOptions.getString(field);
            assertThat("Enum " + field + " value not of expected format!", fieldValue, is(fieldValue.toLowerCase()));
        }
    }

    /**
     * Utility that calculates the expected total item cost for the item costs and postage cost provided.
     *
     * @param costs       the item costs
     * @param postageCost the postage cost
     * @return the expected total item cost (as a String)
     */
    private String calculateExpectedTotalItemCost(final List<ItemCosts> costs, final String postageCost) {
        final int total = costs.stream()
                .map(itemCosts -> Integer.parseInt(itemCosts.getCalculatedCost()))
                .reduce(0, Integer::sum) + Integer.parseInt(postageCost);
        return "" + total;
    }

    /**
     * Extends {@link PatchValidationCertificateItemDTO} to introduce a field that is unknown to the implementation.
     */
    private static class TestDTO extends PatchValidationCertificateItemDTO {
        private final String unknownField;

        private TestDTO(String unknownField) {
            this.unknownField = unknownField;
        }

        public String getUnknownField() {
            return unknownField;
        }
    }
}
