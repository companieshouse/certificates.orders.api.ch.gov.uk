package uk.gov.companieshouse.certificates.orders.api.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import uk.gov.companieshouse.certificates.orders.api.config.ApplicationConfiguration;
import uk.gov.companieshouse.certificates.orders.api.dto.PatchValidationCertificateItemDTO;
import uk.gov.companieshouse.certificates.orders.api.model.*;
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
import static org.hamcrest.Matchers.*;
import static uk.gov.companieshouse.certificates.orders.api.model.CertificateType.DISSOLUTION;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale.SAME_DAY;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale.STANDARD;
import static uk.gov.companieshouse.certificates.orders.api.model.IncludeDobType.PARTIAL;

/**
 * Unit tests the {@link PatchItemRequestValidator} class.
 */
@ExtendWith(SpringExtension.class)
@SpringJUnitConfig(PatchItemRequestValidatorTest.Config.class)
class PatchItemRequestValidatorTest {

    @Configuration
    static class Config {
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
        public PatchItemRequestValidator patchItemRequestValidator() {
            return new PatchItemRequestValidator(objectMapper(), validator(), converter());
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
    private static final IncludeAddressRecordsType INCLUDE_ADDRESS_RECORDS_TYPE = IncludeAddressRecordsType.CURRENT;
    private static final DirectorOrSecretaryDetails DIRECTOR_OR_SECRETARY_DETAILS;
    private static final RegisteredOfficeAddressDetails REGISTERED_OFFICE_ADDRESS_DETAILS;
    private static final boolean INCLUDE_ADDRESS = true;
    private static final boolean INCLUDE_APPOINTMENT_DATE = false;
    private static final boolean INCLUDE_BASIC_INFORMATION = true;
    private static final boolean INCLUDE_COUNTRY_OF_RESIDENCE = false;
    private static final IncludeDobType INCLUDE_DOB_TYPE = IncludeDobType.PARTIAL;
    private static final boolean INCLUDE_NATIONALITY= false;
    private static final boolean INCLUDE_OCCUPATION = true;
    private static final boolean INCLUDE_DATES = true;

    static {
        DIRECTOR_OR_SECRETARY_DETAILS = new DirectorOrSecretaryDetails();
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeAddress(INCLUDE_ADDRESS);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeAppointmentDate(INCLUDE_APPOINTMENT_DATE);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeBasicInformation(INCLUDE_BASIC_INFORMATION);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeCountryOfResidence(INCLUDE_COUNTRY_OF_RESIDENCE);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeDobType(INCLUDE_DOB_TYPE);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeNationality(INCLUDE_NATIONALITY);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeOccupation(INCLUDE_OCCUPATION);

        REGISTERED_OFFICE_ADDRESS_DETAILS = new RegisteredOfficeAddressDetails();
        REGISTERED_OFFICE_ADDRESS_DETAILS.setIncludeAddressRecordsType(INCLUDE_ADDRESS_RECORDS_TYPE);
        REGISTERED_OFFICE_ADDRESS_DETAILS.setIncludeDates(INCLUDE_DATES);
    }

    @Autowired
    private PatchItemRequestValidator validatorUnderTest;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private TestMergePatchFactory patchFactory;

    private PatchValidationCertificateItemDTO itemUpdate;

    @BeforeEach
    void setUp() {
        itemUpdate = new PatchValidationCertificateItemDTO();
    }

    @Test
    @DisplayName("No errors")
    void getValidationErrorsReturnsNoErrors() throws IOException {
        // Given
        itemUpdate.setQuantity(TOKEN_QUANTITY);
        final JsonMergePatch patch = patchFactory.patchFromPojo(itemUpdate);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patch);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("ID is read only")
    void getValidationErrorsRejectsReadOnlyId() throws IOException {
        itemUpdate.setId(TOKEN_STRING);
        assertFieldMustBeNullErrorProduced("id");
    }

    @Test
    @DisplayName("Description is read only")
    void getValidationErrorsRejectsReadOnlyDescription() throws IOException {
        itemUpdate.setDescription(TOKEN_STRING);
        assertFieldMustBeNullErrorProduced("description");
    }

    @Test
    @DisplayName("Description identifier is read only")
    void getValidationErrorsRejectsReadOnlyDescriptionIdentifier() throws IOException {
        itemUpdate.setDescriptionIdentifier(TOKEN_STRING);
        assertFieldMustBeNullErrorProduced("description_identifier");
    }

    @Test
    @DisplayName("Description values are read only")
    void getValidationErrorsRejectsReadOnlyDescriptionValues() throws IOException {
        itemUpdate.setDescriptionValues(TOKEN_VALUES);
        assertFieldMustBeNullErrorProduced("description_values");
    }

    @Test
    @DisplayName("Item costs are read only")
    void getValidationErrorsRejectsReadOnlyItemCosts() throws IOException {
        itemUpdate.setItemCosts(TOKEN_ITEM_COSTS);
        assertFieldMustBeNullErrorProduced("item_costs");
    }

    @Test
    @DisplayName("Kind is read only")
    void getValidationErrorsRejectsReadOnlyKind() throws IOException {
        itemUpdate.setKind(TOKEN_STRING);
        assertFieldMustBeNullErrorProduced("kind");
    }

    @Test
    @DisplayName("Etag is read only")
    void getValidationErrorsRejectsReadOnlyEtag() throws IOException {
        itemUpdate.setEtag(TOKEN_STRING);
        assertFieldMustBeNullErrorProduced("etag");
    }

    @Test
    @DisplayName("Postal delivery is read only")
    void getValidationErrorsRejectsReadOnlyPostalDelivery() throws IOException {
        itemUpdate.setPostalDelivery(TOKEN_POSTAL_DELIVERY_VALUE);
        assertFieldMustBeNullErrorProduced("postal_delivery");
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
        final List<String> errors = validatorUnderTest.getValidationErrors(patch);

        // Then
        assertThat(errors,
                containsInAnyOrder("description_values: must be null",
                                   "item_costs: must be null",
                                   "kind: must be null",
                                   "etag: must be null"));
    }

    @Test
    @DisplayName("Quantity must be greater than 0")
    void getValidationErrorsRejectsZeroQuantity() throws IOException {
        // Given
        itemUpdate.setQuantity(INVALID_QUANTITY);
        final JsonMergePatch patch = patchFactory.patchFromPojo(itemUpdate);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patch);

        // Then
        assertThat(errors, contains("quantity: must be greater than or equal to 1"));
    }

    @Test
    @DisplayName("Unknown field is ignored")
    void getValidationErrorsIgnoresUnknownField() throws IOException {
        // Given
        final String jsonWithUnknownField = "{ \"idx\": \"CHS1\" }";
        final JsonMergePatch patch = patchFactory.patchFromJson(jsonWithUnknownField);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patch);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Collection location is optional by default")
    void collectionLocationIsOptionalByDefault() {
        // Given
        final CertificateItem patchedItem = new CertificateItem();

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patchedItem);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Collection details are mandatory for collection delivery method")
    void collectionDetailsAreMandatoryForCollectionDeliveryMethod() {
        // Given
        final CertificateItem patchedItem = new CertificateItem();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryMethod(DeliveryMethod.COLLECTION);
        patchedItem.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patchedItem);

        // Then
        assertThat(errors, containsInAnyOrder(
                "collection_location: must not be null when delivery method is collection",
                "forename: must not be blank when delivery method is collection",
                "surname: must not be blank when delivery method is collection"));
    }

    @Test
    @DisplayName("Company objects and good standing info may be requested by default")
    void companyObjectsAndGoodStandingInfoMayBeRequestedByDefault() {
        // Given
        final CertificateItem patchedItem = new CertificateItem();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setIncludeCompanyObjectsInformation(true);
        options.setIncludeGoodStandingInformation(true);
        patchedItem.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patchedItem);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Company objects, good standing, registered office details, secretary details or director details" +
                "should not be requested for dissolution")
    void companyObjectsGoodStandingOfficeAddressSecretaryDetailsDirectorDetailsMustNotBeRequestedForDissolution() {
        // Given
        final CertificateItem patchedItem = new CertificateItem();
        final CertificateItemOptions options = new CertificateItemOptions();
        final RegisteredOfficeAddressDetails registeredOfficeAddressDetails = new RegisteredOfficeAddressDetails();
        registeredOfficeAddressDetails.setIncludeAddressRecordsType(INCLUDE_ADDRESS_RECORDS_TYPE);
        options.setCertificateType(DISSOLUTION);
        options.setIncludeCompanyObjectsInformation(true);
        options.setIncludeGoodStandingInformation(true);
        options.setRegisteredOfficeAddressDetails(REGISTERED_OFFICE_ADDRESS_DETAILS);
        options.setSecretaryDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        options.setDirectorDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        patchedItem.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patchedItem);

        // Then
        assertThat(errors, containsInAnyOrder(
                "include_company_objects_information: must not exist when certificate type is dissolution",
                "include_good_standing_information: must not exist when certificate type is dissolution",
                "include_registered_office_address_details: must not exist when certificate type is dissolution",
                "include_secretary_details: must not exist when certificate type is dissolution",
                "include_director_details: must not exist when certificate type is dissolution"));
    }

    @Test
    @DisplayName("(Only) include email copy for same day delivery timescale")
    void includeEmailCopyForSameDayDeliveryTimescale() {
        // Given
        final CertificateItem patchedItem = new CertificateItem();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryTimescale(SAME_DAY);
        options.setIncludeEmailCopy(true);
        patchedItem.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patchedItem);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Do not include email copy for standard delivery timescale")
    void doNotIncludeEmailCopyForStandardDeliveryTimescale() {
        // Given
        final CertificateItem patchedItem = new CertificateItem();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryTimescale(STANDARD);
        options.setIncludeEmailCopy(true);
        patchedItem.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patchedItem);

        // Then
        assertThat(errors, contains(
                "include_email_copy: can only be true when delivery timescale is same_day"));
    }


    @Test
    @DisplayName("Do not include other details without basic information")
    void doNotIncludeOtherDetailsWithoutBasicInformation() {
        // Given
        final CertificateItem patchedItem = new CertificateItem();
        final CertificateItemOptions options = new CertificateItemOptions();
        final DirectorOrSecretaryDetails details = new DirectorOrSecretaryDetails();
        details.setIncludeAddress(true);
        details.setIncludeAppointmentDate(true);
        details.setIncludeCountryOfResidence(true);
        details.setIncludeDobType(PARTIAL);
        details.setIncludeNationality(true);
        details.setIncludeOccupation(true);
        options.setDirectorDetails(details);
        options.setSecretaryDetails(details);
        patchedItem.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patchedItem);

        // Then
        assertThat(errors, contains(
                "director_details: include_address, include_appointment_date, include_country_of_residence,"
                        + " include_nationality, include_occupation must not be true when include_basic_information"
                        + " is false",
                "director_details: include_dob_type must not be non-null when include_basic_information is false",
                "secretary_details: include_address, include_appointment_date, include_country_of_residence,"
                        + " include_nationality, include_occupation must not be true when include_basic_information"
                        + " is false",
                "secretary_details: include_dob_type must not be non-null when include_basic_information is false"));
    }

    @Test
    @DisplayName("Can include other details with basic information")
    void canIncludeOtherDetailsWithBasicInformation() {
        // Given
        final CertificateItem patchedItem = new CertificateItem();
        final CertificateItemOptions options = new CertificateItemOptions();
        final DirectorOrSecretaryDetails details = new DirectorOrSecretaryDetails();
        details.setIncludeAddress(true);
        details.setIncludeAppointmentDate(true);
        details.setIncludeBasicInformation(true);
        details.setIncludeCountryOfResidence(true);
        details.setIncludeDobType(PARTIAL);
        details.setIncludeNationality(true);
        details.setIncludeOccupation(true);
        options.setDirectorDetails(details);
        options.setSecretaryDetails(details);
        patchedItem.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patchedItem);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Reports only the incorrectly set fields")
    void reportsOnlyIncorrectlySetFields() {
        // Given
        final CertificateItem patchedItem = new CertificateItem();
        final CertificateItemOptions options = new CertificateItemOptions();
        final DirectorOrSecretaryDetails details = new DirectorOrSecretaryDetails();
        details.setIncludeAddress(true);
        details.setIncludeAppointmentDate(false);
        details.setIncludeNationality(true);
        details.setIncludeOccupation(true);
        options.setDirectorDetails(details);
        options.setSecretaryDetails(details);
        patchedItem.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patchedItem);

        // Then
        assertThat(errors, contains(
                "director_details: include_address, include_nationality, include_occupation must not be true when "
                        + "include_basic_information is false",
                "secretary_details: include_address, include_nationality, include_occupation must not be true when "
                        + "include_basic_information is false"));
    }

    @Test
    @DisplayName("Handles absence of item options smoothly")
    void handlesAbsenceOfItemOptionsSmoothly() {
        // Given
        final CertificateItem patchedItem = new CertificateItem();

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patchedItem);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Handles absence of details smoothly")
    void handlesAbsenceOfDetailsSmoothly() {
        // Given
        final CertificateItem patchedItem = new CertificateItem();
        final CertificateItemOptions options = new CertificateItemOptions();
        patchedItem.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patchedItem);

        // Then
        assertThat(errors, is(empty()));
    }

    /**
     * Utility method that asserts that the validator produces a "<field name>: must be null"
     * error message.
     * @param fieldName the name of the field for which the error is expected
     * @throws IOException should something unexpected happen
     */
    private void assertFieldMustBeNullErrorProduced(final String fieldName) throws IOException {
        // Given
        final JsonMergePatch patch = patchFactory.patchFromPojo(itemUpdate);
        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patch);
        // Then
        assertThat(errors, contains(fieldName + ": must be null"));
    }

}
