package uk.gov.companieshouse.certificates.orders.api.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.config.ApplicationConfiguration;
import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptionsConfig;
import uk.gov.companieshouse.certificates.orders.api.controller.ApiErrors;
import uk.gov.companieshouse.certificates.orders.api.dto.PatchValidationCertificateItemDTO;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.DeliveryMethod;
import uk.gov.companieshouse.certificates.orders.api.model.DirectorOrSecretaryDetails;
import uk.gov.companieshouse.certificates.orders.api.model.IncludeAddressRecordsType;
import uk.gov.companieshouse.certificates.orders.api.model.ItemCosts;
import uk.gov.companieshouse.certificates.orders.api.model.RegisteredOfficeAddressDetails;
import uk.gov.companieshouse.certificates.orders.api.util.FieldNameConverter;
import uk.gov.companieshouse.certificates.orders.api.util.TestMergePatchFactory;

import javax.json.JsonMergePatch;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.certificates.orders.api.model.CertificateType.DISSOLUTION;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale.SAME_DAY;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale.STANDARD;
import static uk.gov.companieshouse.certificates.orders.api.model.IncludeDobType.PARTIAL;

/**
 * Unit tests the {@link PatchItemRequestValidator} class.
 */
@SpringBootTest
@ActiveProfiles("feature-flags-disabled")
class PatchItemRequestValidatorTest {
    @Import({CertificateOptionsValidatorConfig.class, FeatureOptionsConfig.class})
    @Configuration
    public static class Config {
        @Bean
        public ObjectMapper objectMapper() {
            return new ApplicationConfiguration().objectMapper();
        }

        @Bean
        public Validator validator() {
            final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            return factory.getValidator();
        }

        @Bean
        public FieldNameConverter converter() {
            return new FieldNameConverter();
        }

        @Bean
        public PatchItemRequestValidator patchItemRequestValidator(CertificateOptionsValidator certificateOptionsValidator) {
            return new PatchItemRequestValidator(objectMapper(), validator(), converter(), certificateOptionsValidator);
        }

        @Bean
        TestMergePatchFactory patchFactory() {
            return new TestMergePatchFactory(objectMapper());
        }
    }

    private static final int TOKEN_QUANTITY = 2;
    private static final int INVALID_QUANTITY = 0;
    private static final String TOKEN_STRING = "TOKEN VALUE";
    static final Map<String, String> TOKEN_VALUES = new HashMap<>();
    private static final ItemCosts TOKEN_ITEM_COSTS = new ItemCosts();
    private static final boolean TOKEN_POSTAL_DELIVERY_VALUE = true;

    @Autowired
    private PatchItemRequestValidator validatorUnderTest;

    @Autowired
    private TestMergePatchFactory patchFactory;

    private PatchValidationCertificateItemDTO itemUpdate;

    private CertificateItemOptions certificateItemOptions;

    @MockBean
    private RequestValidatable requestValidatable;

    @BeforeEach
    void setUp() {
        itemUpdate = new PatchValidationCertificateItemDTO();
        certificateItemOptions = new CertificateItemOptions();
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
    }

    @Test
    @DisplayName("No errors")
    void getValidationErrorsReturnsNoErrors() throws IOException {
        // Given
        itemUpdate.setQuantity(TOKEN_QUANTITY);
        final JsonMergePatch patch = patchFactory.patchFromPojo(itemUpdate);

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(patch);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("ID is read only")
    void getValidationErrorsRejectsReadOnlyId() throws IOException {
        itemUpdate.setId(TOKEN_STRING);
        assertFieldMustBeNullErrorProduced(ApiErrors.raiseError(ApiErrors.ERR_CERTIFICATE_ID_SUPPLIED, "id: must be null"));
    }

    @Test
    @DisplayName("Description is read only")
    void getValidationErrorsRejectsReadOnlyDescription() throws IOException {
        itemUpdate.setDescription(TOKEN_STRING);
        assertFieldMustBeNullErrorProduced(ApiErrors.raiseError(ApiErrors.ERR_DESCRIPTION_VALUES_SUPPLIED, "description: must be null"));
    }

    @Test
    @DisplayName("Description identifier is read only")
    void getValidationErrorsRejectsReadOnlyDescriptionIdentifier() throws IOException {
        itemUpdate.setDescriptionIdentifier(TOKEN_STRING);
        assertFieldMustBeNullErrorProduced(ApiErrors.raiseError(ApiErrors.ERR_DESCRIPTION_IDENTIFIER, "description_identifier: must be null"));
    }

    @Test
    @DisplayName("Description values are read only")
    void getValidationErrorsRejectsReadOnlyDescriptionValues() throws IOException {
        itemUpdate.setDescriptionValues(TOKEN_VALUES);
        assertFieldMustBeNullErrorProduced(ApiErrors.raiseError(ApiErrors.ERR_DESCRIPTION_VALUES_SUPPLIED, "description_values: must be null"));
    }

    @Test
    @DisplayName("Item costs are read only")
    void getValidationErrorsRejectsReadOnlyItemCosts() throws IOException {
        itemUpdate.setItemCosts(TOKEN_ITEM_COSTS);
        assertFieldMustBeNullErrorProduced(ApiErrors.raiseError(ApiErrors.ERR_ITEM_COSTS_SUPPLIED, "item_costs: must be null"));
    }

    @Test
    @DisplayName("Kind is read only")
    void getValidationErrorsRejectsReadOnlyKind() throws IOException {
        itemUpdate.setKind(TOKEN_STRING);
        assertFieldMustBeNullErrorProduced(ApiErrors.raiseError(ApiErrors.ERR_KIND_SUPPLIED_LOCATION, "kind: must be null"));
    }

    @Test
    @DisplayName("Etag is read only")
    void getValidationErrorsRejectsReadOnlyEtag() throws IOException {
        itemUpdate.setEtag(TOKEN_STRING);
        assertFieldMustBeNullErrorProduced(ApiErrors.raiseError(ApiErrors.ERR_ETAG_SUPPLIED, "etag: must be null"));
    }

    @Test
    @DisplayName("Postal delivery is read only")
    void getValidationErrorsRejectsReadOnlyPostalDelivery() throws IOException {
        itemUpdate.setPostalDelivery(TOKEN_POSTAL_DELIVERY_VALUE);
        assertFieldMustBeNullErrorProduced(ApiErrors.raiseError(ApiErrors.ERR_POSTAL_DELIVERY, "postal_delivery: must be null"));
    }

    @Test
    @DisplayName("Multiple read only fields rejected")
    void getValidationErrorsRejectsMultipleReadOnlyFields() throws IOException {
        // Given
        itemUpdate.setDescriptionValues(TOKEN_VALUES);
        itemUpdate.setItemCosts(TOKEN_ITEM_COSTS);
        itemUpdate.setKind(TOKEN_STRING);
        itemUpdate.setEtag(TOKEN_STRING);
        final JsonMergePatch patch = patchFactory.patchFromPojo(itemUpdate);

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(patch);

        // Then
        assertThat(errors,
                containsInAnyOrder(ApiErrors.raiseError(ApiErrors.ERR_DESCRIPTION_VALUES_SUPPLIED, "description_values: must be null"),
                        ApiErrors.raiseError(ApiErrors.ERR_ITEM_COSTS_SUPPLIED, "item_costs: must be null"),
                        ApiErrors.raiseError(ApiErrors.ERR_KIND_SUPPLIED_LOCATION, "kind: must be null"),
                        ApiErrors.raiseError(ApiErrors.ERR_ETAG_SUPPLIED, "etag: must be null")));
    }

    @Test
    @DisplayName("Quantity must be greater than 0")
    void getValidationErrorsRejectsZeroQuantity() throws IOException {
        // Given
        itemUpdate.setQuantity(INVALID_QUANTITY);
        final JsonMergePatch patch = patchFactory.patchFromPojo(itemUpdate);

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(patch);

        // Then
        assertThat(errors, contains(ApiErrors.raiseError(ApiErrors.ERR_QUANTITY_AMOUNT, "quantity: must be greater than or equal to 1")));
    }

    @Test
    @DisplayName("Unknown field is ignored")
    void getValidationErrorsIgnoresUnknownField() throws IOException {
        // Given
        final String jsonWithUnknownField = "{ \"idx\": \"CHS1\" }";
        final JsonMergePatch patch = patchFactory.patchFromJson(jsonWithUnknownField);

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(patch);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Collection location is optional by default")
    void collectionLocationIsOptionalByDefault() {
        // Given
        certificateItemOptions.setCompanyType("limited");
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Collection details are mandatory for collection delivery method")
    void collectionDetailsAreMandatoryForCollectionDeliveryMethod() {
        // Given
        certificateItemOptions.setDeliveryMethod(DeliveryMethod.COLLECTION);
        certificateItemOptions.setCompanyType("limited");

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, containsInAnyOrder(
                ApiErrors.raiseError(ApiErrors.ERR_COLLECTION_LOCATION_REQUIRED, "collection_location: must not be null when delivery method is collection"),
                ApiErrors.raiseError(ApiErrors.ERR_FORENAME_REQUIRED, "forename: must not be blank when delivery method is collection"),
                ApiErrors.raiseError(ApiErrors.ERR_SURNAME_REQUIRED, "surname: must not be blank when delivery method is collection")));
    }

    @Test
    @DisplayName("Company objects and good standing info may be requested by default")
    void companyObjectsAndGoodStandingInfoMayBeRequestedByDefault() {
        // Given
        certificateItemOptions.setIncludeCompanyObjectsInformation(true);
        certificateItemOptions.setIncludeGoodStandingInformation(true);
        certificateItemOptions.setCompanyType("limited");

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Company objects, good standing, registered office details, secretary details or director details" +
            "should not be requested for dissolution")
    void companyObjectsGoodStandingOfficeAddressSecretaryDetailsDirectorDetailsMustNotBeRequestedForDissolution() {
        // Given
        DirectorOrSecretaryDetails directorOrSecretaryDetails = new DirectorOrSecretaryDetails();
        directorOrSecretaryDetails.setIncludeAddress(true);
        directorOrSecretaryDetails.setIncludeAppointmentDate(false);
        directorOrSecretaryDetails.setIncludeBasicInformation(true);
        directorOrSecretaryDetails.setIncludeCountryOfResidence(false);
        directorOrSecretaryDetails.setIncludeDobType(PARTIAL);
        directorOrSecretaryDetails.setIncludeNationality(false);
        directorOrSecretaryDetails.setIncludeOccupation(true);

        RegisteredOfficeAddressDetails registeredOfficeAddressDetails = new RegisteredOfficeAddressDetails();
        registeredOfficeAddressDetails.setIncludeAddressRecordsType(IncludeAddressRecordsType.CURRENT);
        registeredOfficeAddressDetails.setIncludeDates(true);

        certificateItemOptions.setCertificateType(DISSOLUTION);
        certificateItemOptions.setIncludeCompanyObjectsInformation(true);
        certificateItemOptions.setIncludeGoodStandingInformation(true);
        certificateItemOptions.setRegisteredOfficeAddressDetails(registeredOfficeAddressDetails);
        certificateItemOptions.setSecretaryDetails(directorOrSecretaryDetails);
        certificateItemOptions.setDirectorDetails(directorOrSecretaryDetails);
        certificateItemOptions.setCompanyType("limited");

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, containsInAnyOrder(
                ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_COMPANY_OBJECTS_INFORMATION_SUPPLIED, "include_company_objects_information: must not exist when certificate type is dissolution"),
                ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_GOOD_STANDING_INFORMATION_SUPPLIED, "include_good_standing_information: must not exist when certificate type is dissolution"),
                ApiErrors.raiseError(ApiErrors.ERR_REGISTERED_OFFICE_ADDRESS_DETAILS_SUPPLIED, "include_registered_office_address_details: must not exist when certificate type is dissolution"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_SUPPLIED, "include_secretary_details: must not exist when certificate type is dissolution"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_SUPPLIED, "include_director_details: must not exist when certificate type is dissolution")));
    }

    @Test
    @DisplayName("(Only) include email copy for same day delivery timescale")
    void includeEmailCopyForSameDayDeliveryTimescale() {
        // Given
        certificateItemOptions.setDeliveryTimescale(SAME_DAY);
        certificateItemOptions.setIncludeEmailCopy(true);
        certificateItemOptions.setCompanyType("limited");

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Do not include email copy for standard delivery timescale")
    void doNotIncludeEmailCopyForStandardDeliveryTimescale() {
        // Given
        certificateItemOptions.setDeliveryTimescale(STANDARD);
        certificateItemOptions.setIncludeEmailCopy(true);
        certificateItemOptions.setCompanyType("limited");

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, contains(ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_EMAIL_COPY_NOT_ALLOWED,"include_email_copy: can only be true when delivery timescale is same_day")));
    }


    @Test
    @DisplayName("Do not include other details without basic information")
    void doNotIncludeOtherDetailsWithoutBasicInformation() {
        // Given
        DirectorOrSecretaryDetails directorOrSecretaryDetails = new DirectorOrSecretaryDetails();
        directorOrSecretaryDetails.setIncludeAddress(true);
        directorOrSecretaryDetails.setIncludeAppointmentDate(true);
        directorOrSecretaryDetails.setIncludeCountryOfResidence(true);
        directorOrSecretaryDetails.setIncludeDobType(PARTIAL);
        directorOrSecretaryDetails.setIncludeNationality(true);
        directorOrSecretaryDetails.setIncludeOccupation(true);

        certificateItemOptions.setDirectorDetails(directorOrSecretaryDetails);
        certificateItemOptions.setSecretaryDetails(directorOrSecretaryDetails);
        certificateItemOptions.setCompanyType("limited");

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, contains(
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_ADDRESS,"director_details.include_address: must not be true when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_APPOINTMENT_DATE,"director_details.include_appointment_date: must not be true when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_ERROR,"director_details.include_country_of_residence: must not be true when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_NATIONALITY_ERROR,"director_details.include_nationality: must not be true when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_OCCUPATION_ERROR,"director_details.include_occupation: must not be true when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_DOB_TYPE_SUPPLIED_ERROR,"director_details.include_dob_type: must not be non-null when include_basic_information is false"),

                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_ADDRESS,"secretary_details.include_address: must not be true when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_APPOINTMENT_DATE,"secretary_details.include_appointment_date: must not be true when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_ERROR,"secretary_details.include_country_of_residence: must not be true when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_NATIONALITY_ERROR,"secretary_details.include_nationality: must not be true when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_OCCUPATION_ERROR,"secretary_details.include_occupation: must not be true when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_DOB_TYPE_SUPPLIED_ERROR,"secretary_details.include_dob_type: must not be non-null when include_basic_information is false")));
    }

    @Test
    @DisplayName("Can include other details with basic information")
    void canIncludeOtherDetailsWithBasicInformation() {
        // Given
        DirectorOrSecretaryDetails directorOrSecretaryDetails = new DirectorOrSecretaryDetails();
        directorOrSecretaryDetails.setIncludeAddress(true);
        directorOrSecretaryDetails.setIncludeAppointmentDate(true);
        directorOrSecretaryDetails.setIncludeBasicInformation(true);
        directorOrSecretaryDetails.setIncludeCountryOfResidence(true);
        directorOrSecretaryDetails.setIncludeDobType(PARTIAL);
        directorOrSecretaryDetails.setIncludeNationality(true);
        directorOrSecretaryDetails.setIncludeOccupation(true);

        certificateItemOptions.setDirectorDetails(directorOrSecretaryDetails);
        certificateItemOptions.setSecretaryDetails(directorOrSecretaryDetails);
        certificateItemOptions.setCompanyType("limited");

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Reports only the incorrectly set fields")
    void reportsOnlyIncorrectlySetFields() {
        // Given
        DirectorOrSecretaryDetails directorOrSecretaryDetails = new DirectorOrSecretaryDetails();
        directorOrSecretaryDetails.setIncludeAddress(true);
        directorOrSecretaryDetails.setIncludeAppointmentDate(false);
        directorOrSecretaryDetails.setIncludeNationality(true);
        directorOrSecretaryDetails.setIncludeOccupation(true);

        certificateItemOptions.setDirectorDetails(directorOrSecretaryDetails);
        certificateItemOptions.setSecretaryDetails(directorOrSecretaryDetails);
        certificateItemOptions.setCompanyType("limited");

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, contains(
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_SUPPLIED,"director_details: include_address, include_nationality, include_occupation must not be true when "
                        + "include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_SUPPLIED,"secretary_details: include_address, include_nationality, include_occupation must not be true when "
                        + "include_basic_information is false")));
    }

    @Test
    @DisplayName("Handles absence of item options smoothly")
    void handlesAbsenceOfItemOptionsSmoothly() {
        // Given
        when(requestValidatable.getItemOptions()).thenReturn(null);

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Handles absence of details smoothly")
    void handlesAbsenceOfDetailsSmoothly() {
        // Given
        certificateItemOptions.setCompanyType("limited");

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    /**
     * Utility method that asserts that the validator produces a "<field name>: must be null"
     * error message.
     *
     * @throws IOException should something unexpected happen
     */
    private void assertFieldMustBeNullErrorProduced(ApiError apiError) throws IOException {
        // Given
        final JsonMergePatch patch = patchFactory.patchFromPojo(itemUpdate);
        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(patch);
        // Then
        assertThat(errors, contains(apiError));
    }
}
