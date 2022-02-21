package uk.gov.companieshouse.certificates.orders.api.validator;

import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.controller.ApiErrors;
import uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang.StringUtils.isBlank;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryMethod.COLLECTION;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale.SAME_DAY;

/**
 * Implements common request payload validation.
 */
public class RequestValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingConstants.APPLICATION_NAMESPACE);

    private final CertificateOptionsValidator certificateOptionsValidator;
    private final BasicInformationIncludeableValidator basicInformationIncludableValidator;
    private final DateOfBirthIncludeableValidator dateOfBirthIncludeableValidator;

    public RequestValidator(CertificateOptionsValidator certificateOptionsValidator,
                            BasicInformationIncludeableValidator basicInformationIncludableValidator,
                            DateOfBirthIncludeableValidator dateOfBirthIncludeableValidator) {
        this.certificateOptionsValidator = certificateOptionsValidator;
        this.basicInformationIncludableValidator = basicInformationIncludableValidator;
        this.dateOfBirthIncludeableValidator = dateOfBirthIncludeableValidator;
    }

    /**
     * Validates the options provided, returning any errors found.
     *
     * @param requestValidatable to be validated
     * @return the errors found, which will be empty if the item is found to be valid
     */
    public List<ApiError> getValidationErrors(final RequestValidatable requestValidatable) {
        final List<ApiError> errors = new ArrayList<>();
        CertificateItemOptions options = requestValidatable.getItemOptions();
        if (options == null) {
            return errors;
        }
        errors.addAll(getCollectionDeliveryValidationErrors(options));

        if (TRUE.equals(options.getIncludeEmailCopy()) &&
                (options.getDeliveryTimescale() != SAME_DAY)) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_INCLUDE_EMAIL_COPY_NOT_ALLOWED,
                    "include_email_copy: can only be true when delivery timescale is same_day");
        }

        errors.addAll(certificateOptionsValidator.validate(requestValidatable));

        errors.addAll(dateOfBirthIncludeableValidator.getValidationErrors(options.getDirectorDetails(), "director_details"));
        errors.addAll(dateOfBirthIncludeableValidator.getValidationErrors(options.getSecretaryDetails(), "secretary_details"));
        errors.addAll(dateOfBirthIncludeableValidator.getValidationErrors(options.getDesignatedMemberDetails(), "designated_member_details"));
        errors.addAll(dateOfBirthIncludeableValidator.getValidationErrors(options.getMemberDetails(), "member_details"));
        errors.addAll(basicInformationIncludableValidator.getValidationErrors(options.getGeneralPartnerDetails(), "general_partner_details"));
        errors.addAll(basicInformationIncludableValidator.getValidationErrors(options.getLimitedPartnerDetails(), "limited_partner_details"));
        return errors;
    }

    /**
     * Validates the collection delivery related fields on the options provided.
     *
     * @param options the options to be validated
     * @return the resulting errors, which will be empty if the fields are found to be valid
     */
    List<ApiError> getCollectionDeliveryValidationErrors(final CertificateItemOptions options) {
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
}
