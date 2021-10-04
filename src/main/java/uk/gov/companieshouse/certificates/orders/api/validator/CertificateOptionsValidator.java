package uk.gov.companieshouse.certificates.orders.api.validator;

import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;

import java.util.List;
import java.util.function.Consumer;

class CertificateOptionsValidator {
    private Consumer<OptionsValidationHelper> strategy;

    public CertificateOptionsValidator(Consumer<OptionsValidationHelper> strategy) {
        this.strategy = strategy;
    }

    List<String> validate(CertificateItemOptions certificateItemOptions) {
        OptionsValidationHelper optionsValidationHelper = new OptionsValidationHelper(certificateItemOptions);
        if (optionsValidationHelper.notCompanyTypeIsNull()) {
            strategy.accept(optionsValidationHelper);
        }
        return optionsValidationHelper.getErrors();
    }
}
