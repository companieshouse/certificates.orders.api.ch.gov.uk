package uk.gov.companieshouse.certificates.orders.api.validator;

import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.controller.ApiErrors;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang.StringUtils.isBlank;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryMethod.COLLECTION;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale.SAME_DAY;

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
    List<ApiError> validate(RequestValidatable requestValidatable) {
        List<ApiError> errors = new ArrayList<>();
        OptionsValidationHelper optionsValidationHelper = this.optionsValidationHelperFactory.createOptionsValidationHelper(requestValidatable);
        if (Objects.nonNull(requestValidatable.getItemOptions()) && !optionsValidationHelper.companyTypeIsNull()) {
            validateDeliveryMethod(errors, requestValidatable.getItemOptions());
            validateDeliveryTimescale(errors, requestValidatable.getItemOptions());
            // Delegate additional validation to supplied validation strategy
            strategy.accept(optionsValidationHelper);
        }
        errors.addAll(optionsValidationHelper.getErrors());
        return errors;
    }

    private void validateDeliveryMethod(List<ApiError> errors, final CertificateItemOptions options) {
        if (options.getDeliveryMethod() == COLLECTION) {
            if (options.getCollectionLocation() == null) {
                ApiErrors.raiseError(errors,
                        ApiErrors.ERR_COLLECTION_LOCATION_REQUIRED,
                        "collection_location: must not be null when delivery method is collection");
            }
            if (isBlank(options.getForename())) {
                ApiErrors.raiseError(errors,
                        ApiErrors.ERR_FORENAME_REQUIRED,
                        "forename: must not be blank when delivery method is collection");
            }
            if (isBlank(options.getSurname())) {
                ApiErrors.raiseError(errors,
                        ApiErrors.ERR_SURNAME_REQUIRED,
                        "surname: must not be blank when delivery method is collection");
            }
        }
    }

    private void validateDeliveryTimescale(List<ApiError> errors, final CertificateItemOptions options) {
        if (TRUE.equals(options.getIncludeEmailCopy()) &&
                (options.getDeliveryTimescale() != SAME_DAY)) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_INCLUDE_EMAIL_COPY_NOT_ALLOWED,
                    "include_email_copy: can only be true when delivery timescale is same_day");
        }
    }
}
