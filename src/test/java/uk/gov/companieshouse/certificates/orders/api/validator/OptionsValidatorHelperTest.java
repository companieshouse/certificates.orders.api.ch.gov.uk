package uk.gov.companieshouse.certificates.orders.api.validator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.LiquidatorsDetails;

@ExtendWith(MockitoExtension.class)
class OptionsValidatorHelperTest {
    CertificateItemOptions certificateItemOptions;

    LiquidatorsDetails liquidatorsDetails;

    @Mock
    RequestValidatable requestValidatable;

    @BeforeEach
    void beforeEach() {
        certificateItemOptions = new CertificateItemOptions();
        liquidatorsDetails = new LiquidatorsDetails();
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
    }

    @Test
    void correctlyValidatesCompanyTypeNotNull() {
        // Given
        certificateItemOptions.setCompanyType("limited");
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable);

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
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable);

        // When
        boolean result = helper.notCompanyTypeIsNull();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(result, is(false));
        assertThat(errors, containsInAnyOrder("company type: is a mandatory field"));
    }

    @Test
    void correctlyErrorsWhenActiveLimitedCompanyAndLiquidatorsDetailsSupplied() {
        // Given
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetails);
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable);

        // When
        helper.validateLimitedCompanyOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_liquidators_details: must not exist when company status is active"));
    }

    @Test
    void correctlyErrorsWhenLiquidatedLimitedCompanyAndGoodStandingInformationSupplied() {
        // Given
        certificateItemOptions.setIncludeGoodStandingInformation(true);
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.LIQUIDATION);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable);

        // When
        helper.validateLimitedCompanyOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_good_standing_information: must not exist when company status is liquidation"));
    }

    @Test
    void correctlyErrorsWhenActiveLLPCompanyAndLiquidatorsDetailsSupplied() {
        // Given
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetails);
        certificateItemOptions.setCompanyType("llp");
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);

        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable);

        // When
        helper.validateLimitedLiabilityPartnershipOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_liquidators_details: must not exist when company status is active"));
    }

    @Test
    void correctlyErrorsWhenLiquidatedLLPCompanyAndGoodStandingInformationSupplied() {
        // Given
        certificateItemOptions.setIncludeGoodStandingInformation(true);
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.LIQUIDATION);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable);

        // When
        helper.validateLimitedLiabilityPartnershipOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_good_standing_information: must not exist when company status is liquidation"));
    }

    @Test
    void correctlyErrorsWhenLPCompanyAndLiquidatorsDetailsSupplied() {
        // Given
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetails);
        certificateItemOptions.setCompanyType("limited-partnership");
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable);

        // When
        helper.validateLimitedPartnershipOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_liquidators_details: must not exist when company type is limited-partnership"));
    }

    @Test
    void correctlyErrorsWhenLPCompanyAndCompanyStatusLiquidation() {
        // Given
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetails);
        certificateItemOptions.setCompanyType("limited-partnership");
        when(requestValidatable.getCompanyStatus()).thenReturn(CompanyStatus.LIQUIDATION);
        OptionsValidationHelper helper = new OptionsValidationHelper(requestValidatable);

        // When
        helper.validateLimitedPartnershipOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("company_status: liquidation not valid for company type limited-partnership"));
    }
}
