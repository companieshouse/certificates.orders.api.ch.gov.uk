package uk.gov.companieshouse.certificates.orders.api.validator;

import org.hamcrest.core.IsIterableContaining;
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

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.certificates.orders.api.model.CertificateType.DISSOLUTION;
import static uk.gov.companieshouse.certificates.orders.api.model.IncludeDobType.PARTIAL;

@ExtendWith(MockitoExtension.class)
class OptionsValidatorHelperTest {
    private CertificateItemOptions certificateItemOptions;

    private LiquidatorsDetails liquidatorsDetails;

    private AdministratorsDetails administratorsDetails;

    @Mock
    private RequestValidatable requestValidatable;

    @Mock
    private FeatureOptions featureOptions;

    @BeforeEach
    void beforeEach() {
        certificateItemOptions = new CertificateItemOptions();
        liquidatorsDetails = new LiquidatorsDetails();
        administratorsDetails = new AdministratorsDetails();
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
    }

    @Test
    void correctlyValidatesCompanyTypeNotNull() {
        // Given
        certificateItemOptions.setCompanyType("limited");
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);

        // When
        boolean result = helper.companyTypeIsNull();
        List<ApiError> errors = helper.getErrors();

        // Then
        assertThat(result, is(false));
        assertThat(errors, is(empty()));
    }

    @Test
    void correctlyErrorsWhenCompanyTypeIsNull() {
        // Given
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);

        // When
        boolean result = helper.companyTypeIsNull();
        List<ApiError> errors = helper.getErrors();

        // Then
        assertThat(result, is(true));
        assertThat(errors, containsInAnyOrder(ApiErrors.raiseError(ApiErrors.ERR_COMPANY_TYPE_REQUIRED, "company type: is a mandatory field")));
    }

    public static <T> org.hamcrest.Matcher<java.lang.Iterable<? super T>> hasItem(T item) {
        return IsIterableContaining.hasItem(item);
    }

    @Test
    void correctlyErrorsWhenLimitedCompanyNotInLiquidationAndLiquidatorsDetailsSuppliedAndFeatureFlagEnabled() {
        // Given
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetails);
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);
        when(featureOptions.isLiquidatedCompanyCertificateEnabled()).thenReturn(Boolean.TRUE);
        ApiError expected = ApiErrorBuilder.builder(ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED)
                .withErrorMessage("include_liquidators_details: must not exist when company status is active").build();

        // When
        helper.validateLimitedCompanyOptions();
        List<ApiError> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem(expected));
    }

    @Test
    void correctlyErrorsWhenLimitedCompanyInAdministrationAndGoodStandingInformationSuppliedAndFeatureFlagEnabled() {
        // Given
        certificateItemOptions.setIncludeGoodStandingInformation(true);
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ADMINISTRATION);
        when(featureOptions.isLiquidatedCompanyCertificateEnabled()).thenReturn(Boolean.TRUE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);
        ApiError expectedError = ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_GOOD_STANDING_INFORMATION_SUPPLIED,
                "include_good_standing_information: must not exist when company status is administration");

        // When
        helper.validateLimitedCompanyOptions();
        List<ApiError> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem(expectedError));
    }

    @Test
    void correctlyErrorsWhenLLPCompanyNotInLiquidationAndLiquidatorsDetailsSuppliedAndFeatureFlagEnabled() {
        // Given
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetails);
        certificateItemOptions.setCompanyType("llp");
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ADMINISTRATION);
        when(featureOptions.isLiquidatedCompanyCertificateEnabled()).thenReturn(Boolean.TRUE);

        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);
        ApiError expectedError = ApiErrors.raiseError(ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED,
                "include_liquidators_details: must not exist when company status is administration");

        // When
        helper.validateLimitedLiabilityPartnershipOptions();
        List<ApiError> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem(expectedError));
    }

    @Test
    void correctlyErrorsWhenLiquidatedLLPCompanyAndGoodStandingInformationSuppliedAndFeatureFlagEnabled() {
        // Given
        certificateItemOptions.setIncludeGoodStandingInformation(true);
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.LIQUIDATION);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);
        when(featureOptions.isLiquidatedCompanyCertificateEnabled()).thenReturn(Boolean.TRUE);
        ApiError expectedError = ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_GOOD_STANDING_INFORMATION_SUPPLIED,
                "include_good_standing_information: must not exist when company status is liquidation");

        // When
        helper.validateLimitedLiabilityPartnershipOptions();
        List<ApiError> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem(expectedError));
    }

    @Test
    void correctlyErrorsWhenLPCompanyAndLiquidatorsDetailsSuppliedAndFeatureFlagEnabled() {
        // Given
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetails);
        certificateItemOptions.setCompanyType("limited-partnership");
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
        when(featureOptions.isLiquidatedCompanyCertificateEnabled()).thenReturn(Boolean.TRUE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);
        ApiError expectedError = ApiErrors.raiseError(ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED,
                "include_liquidators_details: must not exist when company type is limited-partnership");

        // When
        helper.validateLimitedPartnershipOptions();
        List<ApiError> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem(expectedError));
    }

    @Test
    void correctlyErrorsWhenLPCompanyAndLiquidatorsDetailsSuppliedAndFeatureFlagDisabled() {
        // Given
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetails);
        certificateItemOptions.setCompanyType("limited-partnership");
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);
        ApiError expectedError = ApiErrors.raiseError(ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED,
                "include_liquidators_details: must not exist");

        // When
        helper.validateLimitedPartnershipOptions();
        List<ApiError> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem(expectedError));
    }

    @Test
    void correctlyErrorsWhenLPCompanyAndAdministratorsDetailsSuppliedAndFeatureFlagEnabled() {
        // Given
        certificateItemOptions.setAdministratorsDetails(administratorsDetails);
        certificateItemOptions.setCompanyType("limited-partnership");
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
        when(featureOptions.isAdministratorCompanyCertificateEnabled()).thenReturn(Boolean.TRUE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);
        ApiError expectedError = ApiErrors.raiseError(ApiErrors.ERR_ADMINISTRATORS_DETAILS_SUPPLIED,
                "include_administrators_details: must not exist when company type is limited-partnership");

        // When
        helper.validateLimitedPartnershipOptions();
        List<ApiError> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem(expectedError));
    }

    @Test
    void correctlyErrorsWhenLPCompanyAndAdministratorsDetailsSuppliedAndFeatureFlagDisabled() {
        // Given
        certificateItemOptions.setAdministratorsDetails(administratorsDetails);
        certificateItemOptions.setCompanyType("limited-partnership");
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);
        ApiError expectedError = ApiErrors.raiseError(ApiErrors.ERR_ADMINISTRATORS_DETAILS_SUPPLIED,
                "include_administrators_details: must not exist");

        // When
        helper.validateLimitedPartnershipOptions();
        List<ApiError> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem(expectedError));
    }

    @Test
    void correctlyErrorsWhenLPCompanyAndCompanyStatusNotActive() {
        // Given
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetails);
        certificateItemOptions.setCompanyType("limited-partnership");
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.LIQUIDATION);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);
        ApiError expectedError = ApiErrors.raiseError(ApiErrors.ERR_COMPANY_STATUS_INVALID,
                "company_status: liquidation not valid for company type limited-partnership");

        // When
        helper.validateLimitedPartnershipOptions();
        List<ApiError> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem(expectedError));
    }

    @Test
    void itemOptionsValidIfAdministratorsDetailsRequestedForCompanyInAdministrationAndFeatureFlagEnabled() {
        // given
        certificateItemOptions.setAdministratorsDetails(administratorsDetails);
        certificateItemOptions.setCompanyType("ltd");
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ADMINISTRATION);
        when(featureOptions.isAdministratorCompanyCertificateEnabled()).thenReturn(Boolean.TRUE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);

        //when
        helper.validateLimitedCompanyOptions();
        List<ApiError> errors = helper.getErrors();

        //then
        assertTrue(errors.isEmpty());
    }

    @Test
    void itemOptionsInvalidIfAdministratorsDetailsRequestedForCompanyNotInAdministrationAndFeatureFlagEnabled() {
        // given
        certificateItemOptions.setAdministratorsDetails(administratorsDetails);
        certificateItemOptions.setCompanyType("ltd");
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
        when(featureOptions.isAdministratorCompanyCertificateEnabled()).thenReturn(Boolean.TRUE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);
        ApiError expectedError = ApiErrors.raiseError(ApiErrors.ERR_ADMINISTRATORS_DETAILS_SUPPLIED,
                "include_administrators_details: must not exist when company status is active");

        //when
        helper.validateLimitedCompanyOptions();
        List<ApiError> errors = helper.getErrors();

        //then
        assertThat(errors, hasItem(expectedError));
    }

    @Test
    void itemOptionsInvalidIfAdministratorsDetailsRequestedAndFeatureFlagDisabled() {
        //given
        certificateItemOptions.setAdministratorsDetails(administratorsDetails);
        certificateItemOptions.setCompanyType("ltd");
        when(featureOptions.isAdministratorCompanyCertificateEnabled()).thenReturn(Boolean.FALSE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);
        ApiError expectedError = ApiErrors.raiseError(ApiErrors.ERR_ADMINISTRATORS_DETAILS_SUPPLIED,
                "include_administrators_details: must not exist");

        //when
        helper.validateLimitedCompanyOptions();
        List<ApiError> errors = helper.getErrors();

        //then
        assertThat(errors, hasItem(expectedError));
    }

    @Test
    void itemOptionsInvalidIfLiquidatorsDetailsRequestedAndFeatureFlagDisabled() {
        //given
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetails);
        certificateItemOptions.setCompanyType("ltd");
        when(featureOptions.isLiquidatedCompanyCertificateEnabled()).thenReturn(Boolean.FALSE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);
        ApiError expectedError = ApiErrors.raiseError(ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED,
                "include_liquidators_details: must not exist");

        //when
        helper.validateLimitedCompanyOptions();
        List<ApiError> errors = helper.getErrors();

        //then
        assertThat(errors, hasItem(expectedError));
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
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.DISSOLVED);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);

        // When
        helper.validateLimitedCompanyOptions();

        // Then
        assertThat(helper.getErrors(), containsInAnyOrder(
                ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_COMPANY_OBJECTS_INFORMATION_SUPPLIED, "include_company_objects_information: must not exist when company status is dissolved"),
                ApiErrors.raiseError(ApiErrors.ERR_REGISTERED_OFFICE_ADDRESS_DETAILS_SUPPLIED, "include_registered_office_address_details: must not exist when company status is dissolved"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_SUPPLIED, "include_secretary_details: must not exist when company status is dissolved"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_SUPPLIED, "include_director_details: must not exist when company status is dissolved"),
                ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_GOOD_STANDING_INFORMATION_SUPPLIED, "include_good_standing_information: must not exist when company status is dissolved")));
    }
}
