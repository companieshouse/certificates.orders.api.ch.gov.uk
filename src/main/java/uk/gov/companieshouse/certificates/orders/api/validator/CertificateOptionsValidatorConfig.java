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
            strategy = (OptionsValidationHelper optionsValidationHelper) -> {
                String companyType = optionsValidationHelper.getCompanyType();
                if (CompanyType.LIMITED_PARTNERSHIP.equals(companyType)) {
                    optionsValidationHelper.validateLimitedPartnershipOptions();
                } else if (CompanyType.LIMITED_LIABILITY_PARTNERSHIP.equals(companyType)) {
                    optionsValidationHelper.validateLimitedLiabilityPartnershipOptions();
                } else {
                    optionsValidationHelper.validateLimitedCompanyOptions();
                }
            };
        } else if (featureOptions.isLpCertificateOrdersEnabled()) {
            strategy = (OptionsValidationHelper optionsValidationHelper) -> {
                if (CompanyType.LIMITED_PARTNERSHIP.equals(optionsValidationHelper.getCompanyType())) {
                    optionsValidationHelper.validateLimitedPartnershipOptions();
                } else {
                    optionsValidationHelper.validateLimitedCompanyOptions();
                }
            };
        } else if (featureOptions.isLlpCertificateOrdersEnabled()) {
            strategy = (OptionsValidationHelper optionsValidationHelper) -> {
                if (CompanyType.LIMITED_LIABILITY_PARTNERSHIP.equals(optionsValidationHelper.getCompanyType())) {
                    optionsValidationHelper.validateLimitedLiabilityPartnershipOptions();
                } else {
                    optionsValidationHelper.validateLimitedCompanyOptions();
                }
            };
        } else {
            strategy = (OptionsValidationHelper optionsValidationHelper) -> {
                optionsValidationHelper.validateLimitedCompanyOptions();
            };
        }
        return new CertificateOptionsValidator(strategy);
    }
}
