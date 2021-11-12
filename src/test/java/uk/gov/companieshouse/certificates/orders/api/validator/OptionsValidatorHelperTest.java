package uk.gov.companieshouse.certificates.orders.api.validator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.util.List;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.LiquidatorDetails;

class OptionsValidatorHelperTest {

    @Test
    void correctlyValidatesCompanyTypeNotNull() {
        // Given
        CertificateItemOptions certificateItemOptions = new CertificateItemOptions();
        certificateItemOptions.setCompanyType("limited");
        OptionsValidationHelper helper =
                new OptionsValidationHelper(new RequestValidatableImpl(certificateItemOptions));
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
        CertificateItemOptions certificateItemOptions = new CertificateItemOptions();
        OptionsValidationHelper helper =
                new OptionsValidationHelper(new RequestValidatableImpl(certificateItemOptions));
        // When
        boolean result = helper.notCompanyTypeIsNull();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(result, is(false));
        assertThat(errors, containsInAnyOrder("company type: is a mandatory field"));
    }

    @Test
    void correctlyErrorsWhenActiveLimitedCompanyAndLiquidatorDetailsSupplied() {
        // Given
        final CertificateItemOptions options = new CertificateItemOptions();
        LiquidatorDetails liquidatorDetails = new LiquidatorDetails();
        options.setLiquidatorDetails(liquidatorDetails);
        OptionsValidationHelper helper =
                new OptionsValidationHelper(
                        new RequestValidatableImpl(CompanyStatus.ACTIVE, options));

        // When
        helper.validateLimitedCompanyOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_liquidator_details: must not exist when "
                + "company status is active"));
    }

    @Test
    void correctlyErrorsWhenLiquidatedLimitedCompanyAndGoodStandingInformationSupplied() {
        // Given
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setIncludeGoodStandingInformation(true);
        OptionsValidationHelper helper =
                new OptionsValidationHelper(
                        new RequestValidatableImpl(CompanyStatus.LIQUIDATION, options));

        // When
        helper.validateLimitedCompanyOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_good_standing_information: must not exist when "
                + "company status is liquidation"));
    }

    @Test
    void correctlyErrorsWhenActiveLLPCompanyAndLiquidatorDetailsSupplied() {
        // Given
        final CertificateItemOptions options = new CertificateItemOptions();
        LiquidatorDetails liquidatorDetails = new LiquidatorDetails();
        options.setLiquidatorDetails(liquidatorDetails);
        options.setCompanyType("llp");
        OptionsValidationHelper helper =
                new OptionsValidationHelper(
                        new RequestValidatableImpl(CompanyStatus.ACTIVE, options));

        // When
        helper.validateLimitedLiabilityPartnershipOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_liquidator_details: must not exist when "
                + "company status is active"));
    }

    @Test
    void correctlyErrorsWhenLiquidatedLLPCompanyAndGoodStandingInformationSupplied() {
        // Given
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setIncludeGoodStandingInformation(true);
        OptionsValidationHelper helper =
                new OptionsValidationHelper(
                        new RequestValidatableImpl(CompanyStatus.LIQUIDATION, options));

        // When
        helper.validateLimitedLiabilityPartnershipOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_good_standing_information: must not exist when "
                + "company status is liquidation"));
    }

    @Test
    void correctlyErrorsWhenLPCompanyAndLiquidatorDetailsSupplied() {
        // Given
        final CertificateItemOptions options = new CertificateItemOptions();
        LiquidatorDetails liquidatorDetails = new LiquidatorDetails();
        options.setLiquidatorDetails(liquidatorDetails);
        options.setCompanyType("limited-partnership");
        OptionsValidationHelper helper =
                new OptionsValidationHelper(
                        new RequestValidatableImpl(CompanyStatus.ACTIVE, options));

        // When
        helper.validateLimitedPartnershipOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("include_liquidator_details: must not exist when "
                + "company type is limited-partnership"));
    }

    @Test
    void correctlyErrorsWhenLPCompanyAndCompanyStatusLiquidation() {
        // Given
        final CertificateItemOptions options = new CertificateItemOptions();
            LiquidatorDetails liquidatorDetails = new LiquidatorDetails();
            options.setLiquidatorDetails(liquidatorDetails);
            options.setCompanyType("limited-partnership");
        OptionsValidationHelper helper =
                new OptionsValidationHelper(
                        new RequestValidatableImpl(CompanyStatus.LIQUIDATION, options));

        // When
        helper.validateLimitedPartnershipOptions();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(errors, hasItem("company_status: liquidation not valid for company "
                + "type limited-partnership"));
    }
}
