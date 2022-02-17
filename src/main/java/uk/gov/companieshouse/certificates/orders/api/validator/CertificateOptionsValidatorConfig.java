package uk.gov.companieshouse.certificates.orders.api.validator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptions;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;

import java.util.function.Consumer;

/**
 * <p>Configures a {@link CertificateOptionsValidator} with a {@link CertificateItemOptions} field validation strategy;
 * the field validation strategy is chosen according to the application's {@link FeatureOptions} as follows:</p>
 *
 * <table>
 *     <thead>
 *         <th width="20%">LP Feature Option</th><th width="20%">LLP Feature Option</th><th>Validation strategy</th>
 *     </thead>
 *     <tr>
 *         <td>false</td><td>false</td><td>Standard limited company field validation is performed for <em>all company types</em></td>
 *     </tr>
 *     <tr>
 *         <td>true</td><td>false</td><td>Limited partnership field validation is performed for <em>limited-partnership</em> company types;
 *         standard limited company field validation is performed for <em>all other company types</em></td>
 *     </tr>
 *     <tr>
 *         <td>false</td><td>true</td><td>Limited liability partnership field validation is performed for <em>llp</em> company types;
 *         standard limited company field validation is performed for <em>all other company types</em></td>
 *     </tr>
 *     <tr>
 *         <td>true</td><td>true</td><td>Limited partnership field validation is performed for <em>limited-partnership</em> company types;
 *         limited liability partnership field validation is performed for <em>llp</em> company types and
 *         standard limited company field validation is performed for <em>all other company types</em></td>
 *     </tr>
 * </table>
 *
 * @see CertificateOptionsValidator
 */
@Configuration
class CertificateOptionsValidatorConfig {

    /**
     * Use the supplied {@link FeatureOptions} to configure a {@link CertificateOptionsValidator} with certificate an
     * options field validation strategy.
     *
     * @param featureOptions containing company type specific feature options
     * @return
     */
    @Bean
    CertificateOptionsValidator certificateOptionsValidator(FeatureOptions featureOptions, OptionsValidationHelperFactory factory) {
        Consumer<OptionsValidationHelper> strategy;
        if (featureOptions.isLlpCertificateOrdersEnabled() && featureOptions.isLpCertificateOrdersEnabled()) {
            strategy = this::allFeatureOptionsEnabledStrategy;
        } else if (featureOptions.isLpCertificateOrdersEnabled()) {
            strategy = this::lpFeatureOptionEnabledStrategy;
        } else if (featureOptions.isLlpCertificateOrdersEnabled()) {
            strategy = this::llpFeatureOptionEnabledStrategy;
        } else {
            strategy = this::noFeatureOptionsEnabledStrategy;
        }
        return new CertificateOptionsValidator(strategy, factory);
    }

    private void noFeatureOptionsEnabledStrategy(OptionsValidationHelper optionsValidationHelper) {
        optionsValidationHelper.validateLimitedCompanyOptions();
    }

    private void llpFeatureOptionEnabledStrategy(OptionsValidationHelper optionsValidationHelper) {
        if (CompanyType.LIMITED_LIABILITY_PARTNERSHIP == CompanyType.getEnumValue(optionsValidationHelper.getCompanyType())) {
            optionsValidationHelper.validateLimitedLiabilityPartnershipOptions();
        } else {
            optionsValidationHelper.validateLimitedCompanyOptions();
        }
    }

    private void lpFeatureOptionEnabledStrategy(OptionsValidationHelper optionsValidationHelper) {
        if (CompanyType.LIMITED_PARTNERSHIP == CompanyType.getEnumValue(optionsValidationHelper.getCompanyType())) {
            optionsValidationHelper.validateLimitedPartnershipOptions();
        } else {
            optionsValidationHelper.validateLimitedCompanyOptions();
        }
    }

    private void allFeatureOptionsEnabledStrategy(OptionsValidationHelper optionsValidationHelper) {
        String companyType = optionsValidationHelper.getCompanyType();
        if (CompanyType.LIMITED_PARTNERSHIP == CompanyType.getEnumValue(companyType)) {
            optionsValidationHelper.validateLimitedPartnershipOptions();
        } else if (CompanyType.LIMITED_LIABILITY_PARTNERSHIP == CompanyType.getEnumValue(companyType)) {
            optionsValidationHelper.validateLimitedLiabilityPartnershipOptions();
        } else {
            optionsValidationHelper.validateLimitedCompanyOptions();
        }
    }
}
