package uk.gov.companieshouse.certificates.orders.api.validator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptions;
import uk.gov.companieshouse.certificates.orders.api.model.AdministratorsDetails;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.LiquidatorsDetails;

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
        boolean result = helper.notCompanyTypeIsNull();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(result, is(true));
        assertThat(errors, is(empty()));
    }

    @Test
    void correctlyErrorsWhenCompanyTypeIsNull() {
        // Given
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);

        // When
        boolean result = helper.notCompanyTypeIsNull();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(result, is(false));
        assertThat(errors, containsInAnyOrder("company type: is a mandatory field"));
    }

    @Test
    void correctlyErrorsWhenLimitedCompanyNotInLiquidationAndLiquidatorsDetailsSuppliedAndFeatureFlagEnabled() {
        // Given
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetails);
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);
        when(featureOptions.isLiquidatedCompanyCertificateEnabled()).thenReturn(Boolean.TRUE);

        // When
        helper.validateLimitedCompanyOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_liquidators_details: must not exist when company status is active"));
    }

    @Test
    void correctlyErrorsWhenLimitedCompanyInAdministrationAndGoodStandingInformationSuppliedAndFeatureFlagEnabled() {
        // Given
        certificateItemOptions.setIncludeGoodStandingInformation(true);
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ADMINISTRATION);
        when(featureOptions.isLiquidatedCompanyCertificateEnabled()).thenReturn(Boolean.TRUE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);

        // When
        helper.validateLimitedCompanyOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_good_standing_information: must not exist when company status is administration"));
    }

    @Test
    void correctlyErrorsWhenLLPCompanyNotInLiquidationAndLiquidatorsDetailsSuppliedAndFeatureFlagEnabled() {
        // Given
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetails);
        certificateItemOptions.setCompanyType("llp");
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ADMINISTRATION);
        when(featureOptions.isLiquidatedCompanyCertificateEnabled()).thenReturn(Boolean.TRUE);

        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);

        // When
        helper.validateLimitedLiabilityPartnershipOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_liquidators_details: must not exist when company status is administration"));
    }

    @Test
    void correctlyErrorsWhenLiquidatedLLPCompanyAndGoodStandingInformationSuppliedAndFeatureFlagEnabled() {
        // Given
        certificateItemOptions.setIncludeGoodStandingInformation(true);
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.LIQUIDATION);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);
        when(featureOptions.isLiquidatedCompanyCertificateEnabled()).thenReturn(Boolean.TRUE);

        // When
        helper.validateLimitedLiabilityPartnershipOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_good_standing_information: must not exist when company status is liquidation"));
    }

    @Test
    void correctlyErrorsWhenLPCompanyAndLiquidatorsDetailsSuppliedAndFeatureFlagEnabled() {
        // Given
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetails);
        certificateItemOptions.setCompanyType("limited-partnership");
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
        when(featureOptions.isLiquidatedCompanyCertificateEnabled()).thenReturn(Boolean.TRUE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);

        // When
        helper.validateLimitedPartnershipOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_liquidators_details: must not exist when company type is limited-partnership"));
    }

    @Test
    void correctlyErrorsWhenLPCompanyAndLiquidatorsDetailsSuppliedAndFeatureFlagDisabled() {
        // Given
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetails);
        certificateItemOptions.setCompanyType("limited-partnership");
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);

        // When
        helper.validateLimitedPartnershipOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_liquidators_details: must not exist"));
    }

    @Test
    void correctlyErrorsWhenLPCompanyAndAdministratorsDetailsSuppliedAndFeatureFlagEnabled() {
        // Given
        certificateItemOptions.setAdministratorsDetails(administratorsDetails);
        certificateItemOptions.setCompanyType("limited-partnership");
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
        when(featureOptions.isAdministratorCompanyCertificateEnabled()).thenReturn(Boolean.TRUE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);

        // When
        helper.validateLimitedPartnershipOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_administrators_details: must not exist when company type is limited-partnership"));
    }

    @Test
    void correctlyErrorsWhenLPCompanyAndAdministratorsDetailsSuppliedAndFeatureFlagDisabled() {
        // Given
        certificateItemOptions.setAdministratorsDetails(administratorsDetails);
        certificateItemOptions.setCompanyType("limited-partnership");
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);

        // When
        helper.validateLimitedPartnershipOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_administrators_details: must not exist"));
    }

    @Test
    void correctlyErrorsWhenLPCompanyAndCompanyStatusNotActive() {
        // Given
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetails);
        certificateItemOptions.setCompanyType("limited-partnership");
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.LIQUIDATION);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);

        // When
        helper.validateLimitedPartnershipOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("company_status: liquidation not valid for company type limited-partnership"));
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
        List<String> errors = helper.getErrors();

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

        //when
        helper.validateLimitedCompanyOptions();
        List<String> errors = helper.getErrors();

        //then
        assertThat(errors, hasItem("include_administrators_details: must not exist when company status is active"));
    }

    @Test
    void itemOptionsInvalidIfAdministratorsDetailsRequestedAndFeatureFlagDisabled() {
        //given
        certificateItemOptions.setAdministratorsDetails(administratorsDetails);
        certificateItemOptions.setCompanyType("ltd");
        when(featureOptions.isAdministratorCompanyCertificateEnabled()).thenReturn(Boolean.FALSE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);

        //when
        helper.validateLimitedCompanyOptions();
        List<String> errors = helper.getErrors();

        //then
        assertThat(errors, hasItem("include_administrators_details: must not exist"));
    }

    @Test
    void itemOptionsInvalidIfLiquidatorsDetailsRequestedAndFeatureFlagDisabled() {
        //given
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetails);
        certificateItemOptions.setCompanyType("ltd");
        when(featureOptions.isLiquidatedCompanyCertificateEnabled()).thenReturn(Boolean.FALSE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable, featureOptions);

        //when
        helper.validateLimitedCompanyOptions();
        List<String> errors = helper.getErrors();

        //then
        assertThat(errors, hasItem("include_liquidators_details: must not exist"));
    }
}
