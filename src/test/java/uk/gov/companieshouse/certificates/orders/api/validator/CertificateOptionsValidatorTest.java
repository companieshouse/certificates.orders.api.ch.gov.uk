package uk.gov.companieshouse.certificates.orders.api.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptions;
import uk.gov.companieshouse.certificates.orders.api.controller.ApiErrors;
import uk.gov.companieshouse.certificates.orders.api.model.*;
import uk.gov.companieshouse.certificates.orders.api.util.ApiErrorBuilder;
import uk.gov.companieshouse.certificates.orders.api.util.FieldNameConverter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.gov.companieshouse.certificates.orders.api.model.CertificateType.DISSOLUTION;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale.SAME_DAY;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale.STANDARD;
import static uk.gov.companieshouse.certificates.orders.api.model.IncludeDobType.PARTIAL;

@ExtendWith(MockitoExtension.class)
class CertificateOptionsValidatorTest {
    @Mock
    private Consumer<OptionsValidationHelper> optionsValidationHelperConsumer;

    @Mock
    private RequestValidatable requestValidatable;

    private CertificateItemOptions certificateItemOptions;

    @Mock
    private OptionsValidationHelperFactory optionsValidationHelperFactory;

    @Mock
    private OptionsValidationHelper optionsValidationHelper;

    private CertificateOptionsValidator validator;

    @BeforeEach
    void beforeEach() {
        certificateItemOptions = new CertificateItemOptions();
        validator = new CertificateOptionsValidator(optionsValidationHelperConsumer, optionsValidationHelperFactory, new FieldNameConverter());
    }

    @Test
    void correctlyFailsWhenCompanyTypeIsNull() {
        // Given
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);
        when(optionsValidationHelper.companyTypeIsNull()).thenReturn(true);

        // When
        validator.validateCertificateOptions(requestValidatable);

        // Then
        verifyNoInteractions(optionsValidationHelperConsumer);
    }

    @Test
    void correctlyPassesWhenCompanyTypeIsNotNull() {
        // Given
        certificateItemOptions.setCompanyType("ltd");
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);
        when(optionsValidationHelper.companyTypeIsNull()).thenReturn(false);

        // When
        List<ApiError> result = validator.validateCertificateOptions(requestValidatable);

        // Then
        assertThat(result, is(empty()));
    }

    @Test
    void correctlyPassesWhenItemOptionsNull() {
        //given
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);

        // When
        List<ApiError> result = validator.validateCertificateOptions(requestValidatable);

        // Then
        assertThat(result, is(empty()));
    }

    @Test
    @DisplayName("Collection details are mandatory for collection delivery method")
    void collectionDetailsAreMandatoryForCollectionDeliveryMethod() {
        // Given
        certificateItemOptions.setCompanyType("limited");
        certificateItemOptions.setDeliveryMethod(DeliveryMethod.COLLECTION);
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);

        // When
        final List<ApiError> errors = validator.validateCertificateOptions(requestValidatable);

        // Then
        assertThat(errors, containsInAnyOrder(
                ApiErrors.raiseError(ApiErrors.ERR_COLLECTION_LOCATION_REQUIRED, "collection_location: must not be null when delivery method is collection"),
                ApiErrors.raiseError(ApiErrors.ERR_FORENAME_REQUIRED, "forename: must not be blank when delivery method is collection"),
                ApiErrors.raiseError(ApiErrors.ERR_SURNAME_REQUIRED, "surname: must not be blank when delivery method is collection")));
    }

    @Test
    @DisplayName("Collection details have been provided for collection delivery method")
    void collectionDetailsProvidedForCollectionDeliveryMethod() {
        // Given
        certificateItemOptions.setCompanyType("limited");
        certificateItemOptions.setDeliveryMethod(DeliveryMethod.COLLECTION);
        certificateItemOptions.setForename("TOM");
        certificateItemOptions.setSurname("COBLEY");
        certificateItemOptions.setCollectionLocation(CollectionLocation.LONDON);
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);

        // When
        final List<ApiError> errors = validator.validateCertificateOptions(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    void testRaiseErrorIfEmailRequestedAndDeliveryTimescaleNotSameDay() {
        //given
        certificateItemOptions.setIncludeEmailCopy(true);
        certificateItemOptions.setDeliveryTimescale(DeliveryTimescale.STANDARD);
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);

        //when
        final List<ApiError> errors = validator.validateCertificateOptions(requestValidatable);

        //then
        assertThat(errors, contains(
                ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_EMAIL_COPY_NOT_ALLOWED, "include_email_copy: can only be true when delivery timescale is same_day")
        ));
    }

    @Test
    void testOptionsValidIfEmailRequestedAndDeliveryTimescaleSameDay() {
        //given
        certificateItemOptions.setIncludeEmailCopy(true);
        certificateItemOptions.setDeliveryTimescale(DeliveryTimescale.SAME_DAY);
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);

        //when
        final List<ApiError> errors = validator.validateCertificateOptions(requestValidatable);

        //then
        assertThat(errors, is(empty()));
    }


    @Test
    @DisplayName("Collection location is optional by default")
    void collectionLocationIsOptionalByDefault() {
        // Given
        certificateItemOptions.setCompanyType("limited");
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);

        // When
        final List<ApiError> errors = validator.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Company objects and good standing info may be requested by default")
    void companyObjectsAndGoodStandingInfoMayBeRequestedByDefault() {
        // Given
        certificateItemOptions.setIncludeCompanyObjectsInformation(true);
        certificateItemOptions.setIncludeGoodStandingInformation(true);
        certificateItemOptions.setCompanyType("limited");
        certificateItemOptions.setCompanyStatus(CompanyStatus.ACTIVE.getStatusName());
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);

        // When
        final List<ApiError> errors = validator.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("(Only) include email copy for same day delivery timescale")
    void includeEmailCopyForSameDayDeliveryTimescale() {
        // Given
        certificateItemOptions.setDeliveryTimescale(SAME_DAY);
        certificateItemOptions.setIncludeEmailCopy(true);
        certificateItemOptions.setCompanyType("limited");
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);

        // When
        final List<ApiError> errors = validator.getValidationErrors(requestValidatable);

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
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);

        // When
        final List<ApiError> errors = validator.getValidationErrors(requestValidatable);

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
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);

        // When
        final List<ApiError> errors = validator.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, contains(
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_ADDRESS,"director_details.include_address: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_APPOINTMENT_DATE,"director_details.include_appointment_date: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_ERROR,"director_details.include_country_of_residence: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_DOB_TYPE_SUPPLIED_ERROR,"director_details.include_dob_type: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_NATIONALITY,"director_details.include_nationality: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_OCCUPATION,"director_details.include_occupation: must not be set when include_basic_information is false"),

                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_ADDRESS,"secretary_details.include_address: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_APPOINTMENT_DATE,"secretary_details.include_appointment_date: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_ERROR,"secretary_details.include_country_of_residence: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_DOB_TYPE_SUPPLIED_ERROR,"secretary_details.include_dob_type: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_NATIONALITY,"secretary_details.include_nationality: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_OCCUPATION,"secretary_details.include_occupation: must not be set when include_basic_information is false")));
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
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);

        // When
        final List<ApiError> errors = validator.getValidationErrors(requestValidatable);

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
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);

        // When
        final List<ApiError> errors = validator.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, contains(
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_ADDRESS,"director_details.include_address: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_NATIONALITY,"director_details.include_nationality: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_OCCUPATION,"director_details.include_occupation: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_ADDRESS,"secretary_details.include_address: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_NATIONALITY,"secretary_details.include_nationality: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_OCCUPATION,"secretary_details.include_occupation: must not be set when include_basic_information is false")));
    }

    @Test
    @DisplayName("Handles absence of item options smoothly")
    void handlesAbsenceOfItemOptionsSmoothly() {
        // Given
        when(requestValidatable.getItemOptions()).thenReturn(null);

        // When
        final List<ApiError> errors = validator.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Handles absence of details smoothly")
    void handlesAbsenceOfDetailsSmoothly() {
        // Given
        certificateItemOptions.setCompanyType("limited");

        // When
        final List<ApiError> errors = validator.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Raise a validation error when validating an object implementing BasicInformationIncludable that" +
            "contains another field set to true")
    void testRaiseValidationErrorIfBasicInformationObjectOtherFieldValuesSet() {
        // Given
        BasicInformationIncludable<Map<String, Object>> containsBasicInformation = new BasicInformationIncludable<Map<String, Object>>() {

            @Override
            public Boolean getIncludeBasicInformation() {
                return false;
            }

            @Override
            public void accept(Visitor<Map<String, Object>> visitor) {
                visitor.visit(Collections.singletonMap("other_field", Boolean.TRUE));
            }
        };

        // When
        List<ApiError> errors = validator.getValidationErrors(containsBasicInformation, "parent");

        // Then
        assertThat(errors, contains(ApiErrorBuilder.builder(new ApiError(
                "other-field-error",
                "parent.other_field",
                ApiErrors.BOOLEAN_LOCATION_TYPE,
                ApiErrors.ERROR_TYPE_VALIDATION))
                .withErrorMessage("parent.other_field: must not be set when include_basic_information is false")
                .build()));
    }
}
