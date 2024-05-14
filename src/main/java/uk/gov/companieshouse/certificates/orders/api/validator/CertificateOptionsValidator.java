package uk.gov.companieshouse.certificates.orders.api.validator;

import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.controller.ApiErrors;
import uk.gov.companieshouse.certificates.orders.api.model.BasicInformationIncludable;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.DateOfBirthIncludable;
import uk.gov.companieshouse.certificates.orders.api.util.ApiErrorBuilder;
import uk.gov.companieshouse.certificates.orders.api.util.FieldNameConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.nonNull;
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
public class CertificateOptionsValidator {
    private static final Predicate<Map.Entry<String, Object>> INCLUDEABLE_FIELDS =
            entry -> entry.getValue() == TRUE;
    private static final Predicate<Map.Entry<String, Object>> INCLUDE_DOB_TYPE_FIELD =
            entry -> "include_dob_type".equals(entry.getKey()) && nonNull(entry.getValue());

    private final Consumer<OptionsValidationHelper> strategy;
    private final OptionsValidationHelperFactory optionsValidationHelperFactory;
    private final FieldNameConverter fieldNameConverter;

    /**
     * Creates a new CertificateOptionsValidator
     *
     * @param strategy consumer (method) that accepts {@link OptionsValidationHelper}
     */
    public CertificateOptionsValidator(Consumer<OptionsValidationHelper> strategy,
                                       OptionsValidationHelperFactory optionsValidationHelperFactory,
                                       FieldNameConverter fieldNameConverter) {
        this.strategy = strategy;
        this.optionsValidationHelperFactory = optionsValidationHelperFactory;
        this.fieldNameConverter = fieldNameConverter;
    }

    /**
     * Performs field validation on the supplied certificate item options
     *
     * @param requestValidatable to be validated
     * @return list containing any errors; an empty list if no validation errors occur
     */
    List<ApiError> validateCertificateOptions(RequestValidatable requestValidatable) {
        List<ApiError> errors = new ArrayList<>();
        OptionsValidationHelper optionsValidationHelper = this.optionsValidationHelperFactory.createOptionsValidationHelper(requestValidatable);
        if (nonNull(requestValidatable.itemOptions()) && !optionsValidationHelper.companyTypeIsNull()) {
            errors.addAll(validateDeliveryMethod(requestValidatable.itemOptions()));
            errors.addAll(validateDeliveryTimescale(requestValidatable.itemOptions()));
            // Delegate additional validation to supplied validation strategy
            strategy.accept(optionsValidationHelper);
        }
        errors.addAll(optionsValidationHelper.getErrors());
        return errors;
    }

    /**
     * Validates the options provided, returning any errors found.
     *
     * @param requestValidatable to be validated
     * @return the errors found, which will be empty if the item is found to be valid
     */
    public List<ApiError> getValidationErrors(final RequestValidatable requestValidatable) {
        final List<ApiError> errors = new ArrayList<>();
        CertificateItemOptions options = requestValidatable.itemOptions();
        if (options == null) {
            return errors;
        }
        errors.addAll(validateCertificateOptions(requestValidatable));

        errors.addAll(getValidationErrors(options.getDirectorDetails(), "director_details"));
        errors.addAll(getValidationErrors(options.getSecretaryDetails(), "secretary_details"));
        errors.addAll(getValidationErrors(options.getDesignatedMemberDetails(), "designated_member_details"));
        errors.addAll(getValidationErrors(options.getMemberDetails(), "member_details"));
        errors.addAll(getValidationErrors(options.getGeneralPartnerDetails(), "general_partner_details"));
        errors.addAll(getValidationErrors(options.getLimitedPartnerDetails(), "limited_partner_details"));
        return errors;
    }

    /**
     * Validates the collection delivery related fields on the options provided.
     *
     * @param options the options to be validated
     * @return the resulting errors, which will be empty if the fields are found to be valid
     */
    List<ApiError> validateDeliveryMethod(final CertificateItemOptions options) {
        final List<ApiError> errors = new ArrayList<>();
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
        return errors;
    }

    public List<ApiError> validateDeliveryTimescale(final CertificateItemOptions options) {
        final List<ApiError> errors = new ArrayList<>();
        if (TRUE.equals(options.getIncludeEmailCopy()) &&
                (options.getDeliveryTimescale() != SAME_DAY)) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_INCLUDE_EMAIL_COPY_NOT_ALLOWED,
                    "include_email_copy: can only be true when delivery timescale is same_day");
        }
        return errors;
    }

    /**
     * Validates the details provided, returning any errors found, prefixed with the supplied details field name.
     *
     * @param details          the details to be validated
     * @param detailsFieldName the field name of the details to be validated
     * @return the resulting errors, which will be empty if the details are found to be valid
     */
    List<ApiError> getValidationErrors(final BasicInformationIncludable<Map<String, Object>> details,
                                       final String detailsFieldName) {
        if (details == null || TRUE.equals(details.getIncludeBasicInformation())) {
            return Collections.emptyList();
        }
        return getBasicInformationFieldErrors(details, detailsFieldName, INCLUDEABLE_FIELDS);
    }

    List<ApiError> getValidationErrors(final DateOfBirthIncludable<Map<String, Object>> details, final String detailsFieldName) {
        if (details == null || TRUE.equals(details.getIncludeBasicInformation())) {
            return Collections.emptyList();
        }
        return getBasicInformationFieldErrors(details, detailsFieldName, INCLUDEABLE_FIELDS.or(INCLUDE_DOB_TYPE_FIELD));
    }

    private List<ApiError> getBasicInformationFieldErrors(final BasicInformationIncludable<Map<String, Object>> details,
                                                          final String detailsFieldName,
                                                          final Predicate<Map.Entry<String, Object>> targetPredicate) {
        FindByValueVisitor incorrectlySetFieldsValueFinder = new FindByValueVisitor(targetPredicate);
        details.accept(incorrectlySetFieldsValueFinder);
        final List<String> incorrectlySetFields = incorrectlySetFieldsValueFinder.getKeys();
        final List<ApiError> errors = new ArrayList<>();
        if (!incorrectlySetFields.isEmpty()) {
            errors.addAll(incorrectlySetFields.stream()
                    .map(field -> {
                        String fieldName = fieldNameConverter.fromLowerUnderscoreToLowerHyphenCase(field);
                        String errorMessage = detailsFieldName + "." + field + ": must not be set when include_basic_information is false";

                        return ApiErrorBuilder.builder(
                                new ApiError(fieldName + "-error",
                                            detailsFieldName + "." + field,
                                            ApiErrors.BOOLEAN_LOCATION_TYPE,
                                            ApiErrors.ERROR_TYPE_VALIDATION))
                                .withErrorMessage(errorMessage)
                                .build();
                    })
                    .toList());
        }
        return errors;
    }
}
