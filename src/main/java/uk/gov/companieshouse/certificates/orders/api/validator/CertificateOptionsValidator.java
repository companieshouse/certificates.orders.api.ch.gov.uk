package uk.gov.companieshouse.certificates.orders.api.validator;

import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptions;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;

import java.util.List;
import java.util.function.Consumer;

/**
 * <p>Performs field validation on supplied {@link CertificateItemOptions}.</p>
 *
 * <p>Note: this validator checks that the supplied company type is not null; <em>all</em> additional field validation
 * is achieved by supplying a custom validation strategy consumer e.g.</p>
 *
 * <pre>
 * void customValidator(OptionsValidatorHelper helper) {
 *    ...
 *    helper.validateLimitedCompanyOptions();
 *    ...
 * }
 *
 * CertificateItemOptions itemOptions = ...
 * ...
 * CertificateOptionsValidator validator = new CertificateOptionsValidator(this::customValidator);
 * List<String> errors = validator.validate(itemOptions);
 * ...
 * </pre>
 *
 * @see CertificateOptionsValidatorConfig
 */
class CertificateOptionsValidator {
    private final Consumer<OptionsValidationHelper> strategy;
    private final OptionsValidationHelperFactory optionsValidationHelperFactory;

    /**
     * Creates a new CertificateOptionsValidator
     *
     * @param strategy consumer (method) that accepts {@link OptionsValidationHelper}
     */
    public CertificateOptionsValidator(Consumer<OptionsValidationHelper> strategy, OptionsValidationHelperFactory optionsValidationHelperFactory) {
        this.strategy = strategy;
        this.optionsValidationHelperFactory = optionsValidationHelperFactory;
    }

    /**
     * Performs field validation on the supplied certificate item options
     *
     * @param requestValidatable to be validated
     * @return list containing any errors; an empty list if no validation errors occur
     */
    List<String> validate(RequestValidatable requestValidatable) {
        OptionsValidationHelper optionsValidationHelper = this.optionsValidationHelperFactory.createOptionsValidationHelper(requestValidatable);
        if (optionsValidationHelper.notCompanyTypeIsNull()) {

            // Delegate additional validation to supplied validation strategy
            strategy.accept(optionsValidationHelper);
        }
        return optionsValidationHelper.getErrors();
    }
}
