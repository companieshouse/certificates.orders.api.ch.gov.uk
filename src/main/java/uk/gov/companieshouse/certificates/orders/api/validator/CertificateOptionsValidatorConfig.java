package uk.gov.companieshouse.certificates.orders.api.validator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptions;

import java.util.function.Consumer;

@Configuration
class CertificateOptionsValidatorConfig {

    @Bean
    CertificateOptionsValidator certificateOptionsValidator(FeatureOptions featureOptions) {
        Consumer<OptionsValidationHelper> strategy;
        if (featureOptions.isLlpCertificateOrdersEnabled() && featureOptions.isLpCertificateOrdersEnabled()) {
            strategy = this::AllFeatureOptionsEnabledStrategy;
        } else if (featureOptions.isLpCertificateOrdersEnabled()) {
            strategy = this::lpFeatureOptionEnabledStrategy;
        } else if (featureOptions.isLlpCertificateOrdersEnabled()) {
            strategy = this::llpFeatureOptionEnabledStrategy;
        } else {
            strategy = this::noFeatureOptionsEnabledStrategy;
        }
        return new CertificateOptionsValidator(strategy);
    }

    private void noFeatureOptionsEnabledStrategy(OptionsValidationHelper optionsValidationHelper) {
        optionsValidationHelper.validateLimitedCompanyOptions();
    }

    private void llpFeatureOptionEnabledStrategy(OptionsValidationHelper optionsValidationHelper) {
        if (CompanyType.LIMITED_LIABILITY_PARTNERSHIP.equals(optionsValidationHelper.getCompanyType())) {
            optionsValidationHelper.validateLimitedLiabilityPartnershipOptions();
        } else {
            optionsValidationHelper.validateLimitedCompanyOptions();
        }
    }

    private void lpFeatureOptionEnabledStrategy(OptionsValidationHelper optionsValidationHelper) {
        if (CompanyType.LIMITED_PARTNERSHIP.equals(optionsValidationHelper.getCompanyType())) {
            optionsValidationHelper.validateLimitedPartnershipOptions();
        } else {
            optionsValidationHelper.validateLimitedCompanyOptions();
        }
    }

    private void AllFeatureOptionsEnabledStrategy(OptionsValidationHelper optionsValidationHelper) {
        String companyType = optionsValidationHelper.getCompanyType();
        if (CompanyType.LIMITED_PARTNERSHIP.equals(companyType)) {
            optionsValidationHelper.validateLimitedPartnershipOptions();
        } else if (CompanyType.LIMITED_LIABILITY_PARTNERSHIP.equals(companyType)) {
            optionsValidationHelper.validateLimitedLiabilityPartnershipOptions();
        } else {
            optionsValidationHelper.validateLimitedCompanyOptions();
        }
    }
}
