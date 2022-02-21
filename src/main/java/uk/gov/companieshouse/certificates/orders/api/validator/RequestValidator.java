package uk.gov.companieshouse.certificates.orders.api.validator;

import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.controller.ApiErrors;
import uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants;
import uk.gov.companieshouse.certificates.orders.api.model.BasicInformationIncludable;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.DateOfBirthIncludable;
import uk.gov.companieshouse.certificates.orders.api.util.ApiErrorBuilder;
import uk.gov.companieshouse.certificates.orders.api.util.FieldNameConverter;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static java.util.Arrays.stream;
import static org.apache.commons.lang.StringUtils.isBlank;
import static uk.gov.companieshouse.certificates.orders.api.model.CertificateType.DISSOLUTION;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryMethod.COLLECTION;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale.SAME_DAY;

/**
 * Implements common request payload validation.
 */
public class RequestValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingConstants.APPLICATION_NAMESPACE);

    private final CertificateOptionsValidator certificateOptionsValidator;

    public RequestValidator(CertificateOptionsValidator certificateOptionsValidator) {
        this.certificateOptionsValidator = certificateOptionsValidator;
    }

    /**
     * Validates the options provided, returning any errors found.
     *
     * @param requestValidatable to be validated
     * @param converter the converter this uses to present field names as they appear in the request JSON payload
     * @return the errors found, which will be empty if the item is found to be valid
     */
    List<ApiError> getValidationErrors(final RequestValidatable requestValidatable,
                                       final FieldNameConverter converter) {
        final List<ApiError> errors = new ArrayList<>();
        CertificateItemOptions options = requestValidatable.getItemOptions();
        if (options == null) {
            return errors;
        }
        errors.addAll(getCollectionDeliveryValidationErrors(options));

        if (options.getCertificateType() == DISSOLUTION) {
            if (options.getIncludeCompanyObjectsInformation() != null) {
                ApiErrors.raiseError(errors, ApiErrors.ERR_INCLUDE_COMPANY_OBJECTS_INFORMATION_SUPPLIED,
                        "include_company_objects_information: must not exist when certificate type is dissolution");
            }
            if (options.getIncludeGeneralNatureOfBusinessInformation() != null) {
                ApiErrors.raiseError(errors, ApiErrors.ERR_INCLUDE_GENERAL_NATURE_OF_BUSINESS_INFORMATION_SUPPLIED,
                        "include_general_nature_of_business_information: must not exist when certificate type is dissolution");
            }
            if (options.getIncludeGoodStandingInformation() != null) {
                ApiErrors.raiseError(errors, ApiErrors.ERR_INCLUDE_GOOD_STANDING_INFORMATION_SUPPLIED,
                        "include_good_standing_information: must not exist when certificate type is dissolution");
            }
            if (options.getRegisteredOfficeAddressDetails() != null) {
                ApiErrors.raiseError(errors, ApiErrors.ERR_REGISTERED_OFFICE_ADDRESS_DETAILS_SUPPLIED,
                        "include_registered_office_address_details: must not exist when certificate type is dissolution");
            }
            if (options.getSecretaryDetails() != null) {
                ApiErrors.raiseError(errors, ApiErrors.ERR_SECRETARY_DETAILS_SUPPLIED,
                        "include_secretary_details: must not exist when certificate type is dissolution");
            }
            if (options.getDirectorDetails() != null) {
                ApiErrors.raiseError(errors, ApiErrors.ERR_DIRECTOR_DETAILS_SUPPLIED,
                        "include_director_details: must not exist when certificate type is dissolution");
            }
            if (options.getDesignatedMemberDetails() != null) {
                ApiErrors.raiseError(errors, ApiErrors.ERR_DESIGNATED_MEMBERS_DETAILS_SUPPLIED,
                        "include_designated_member_details: must not exist when certificate type is dissolution");
            }
            if (options.getMemberDetails() != null) {
                ApiErrors.raiseError(errors, ApiErrors.ERR_MEMBERS_DETAILS_SUPPLIED,
                        "include_member_details: must not exist when certificate type is dissolution");
            }
            if (options.getGeneralPartnerDetails() != null) {
                ApiErrors.raiseError(errors, ApiErrors.ERR_GENERAL_PARTNER_DETAILS_SUPPLIED,
                        "include_general_partner_details: must not exist when certificate type is dissolution");
            }
            if (options.getLimitedPartnerDetails() != null) {
                ApiErrors.raiseError(errors, ApiErrors.ERR_LIMITED_PARTNER_DETAILS_SUPPLIED,
                        "include_limited_partner_details: must not exist when certificate type is dissolution");
            }
            if (options.getPrincipalPlaceOfBusinessDetails() != null) {
                ApiErrors.raiseError(errors, ApiErrors.ERR_PRINCIPAL_PLACE_OF_BUSINESS_DETAILS_SUPPLIED,
                        "include_principal_place_of_business_details: must not exist when certificate type is dissolution");
            }
        }

        if (TRUE.equals(options.getIncludeEmailCopy()) &&
                (options.getDeliveryTimescale() != SAME_DAY)) {
            ApiErrors.raiseError(errors, ApiErrors.ERR_INCLUDE_EMAIL_COPY_NOT_ALLOWED,
                    "include_email_copy: can only be true when delivery timescale is same_day");
        }

        errors.addAll(certificateOptionsValidator.validate(requestValidatable));

        errors.addAll(getValidationErrors(options.getDirectorDetails(), "director_details", converter));
        errors.addAll(getValidationErrors(options.getSecretaryDetails(), "secretary_details", converter));
        errors.addAll(getValidationErrors(options.getDesignatedMemberDetails(), "designated_member_details", converter));
        errors.addAll(getValidationErrors(options.getMemberDetails(), "member_details", converter));
        errors.addAll(getValidationErrors(options.getGeneralPartnerDetails(), "general_partner_details", converter));
        errors.addAll(getValidationErrors(options.getLimitedPartnerDetails(), "limited_partner_details", converter));
        return errors;
    }

    /**
     * Validates the details provided, returning any errors found, prefixed with the supplied details field name.
     *
     * @param details          the details to be validated
     * @param detailsFieldName the field name of the details to be validated
     * @param converter        the converter this uses to present field names as they appear in the request JSON payload
     * @return the resulting errors, which will be empty if the details are found to be valid
     */
    List<ApiError> getValidationErrors(final BasicInformationIncludable details,
                                     final String detailsFieldName,
                                     final FieldNameConverter converter) {
        final List<ApiError> errors = new ArrayList<>();
        if (details == null || TRUE.equals(details.getIncludeBasicInformation())) {
            return errors;
        }
        final Field[] fields = details.getClass().getDeclaredFields();
        final List<String> incorrectlySetFields = stream(fields)
                .filter(field ->
                        !field.getName().equals("includeBasicInformation") &&
                                field.getType().equals(Boolean.class) &&
                                isTrue(field, details))
                .map(field -> converter.toSnakeCase(field.getName()))
                .collect(Collectors.toList());
        if (!incorrectlySetFields.isEmpty()) {
            errors.addAll(incorrectlySetFields.stream().map(field -> ApiErrorBuilder.builder(
                            new ApiError(converter.toLowerHyphenCase(field) + "-error", detailsFieldName + "." + field, ApiErrors.BOOLEAN_LOCATION_TYPE, ApiErrors.ERROR_TYPE_VALIDATION))
                    .withErrorMessage(detailsFieldName + "." + field + ": must not be true when include_basic_information is false")
                    .build()).collect(Collectors.toList()));
        }
        return errors;
    }

    List<ApiError> getValidationErrors(final DateOfBirthIncludable details, final String detailsFieldName,
                                     final FieldNameConverter converter) {
        List<ApiError> errors = new ArrayList<>();
        if (details == null || TRUE.equals(details.getIncludeBasicInformation())) {
            return errors;
        }
        errors = getValidationErrors((BasicInformationIncludable) details, detailsFieldName, converter);
        if (details.getIncludeDobType() != null) {
            errors.add(ApiErrorBuilder.builder(
                    new ApiError(ApiErrors.INCLUDE_DOB_TYPE_REQUIRED_ERROR, detailsFieldName + ".include_dob_type", ApiErrors.BOOLEAN_LOCATION_TYPE, ApiErrors.ERROR_TYPE_VALIDATION))
                    .withErrorMessage(detailsFieldName + ".include_dob_type: must not be non-null when include_basic_information is false")
                    .build());
        }
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

    /**
     * Determines whether the value of the field on the details object is equivalent to <code>true</code> or not.
     *
     * @param field   the reflective representation of the details field
     * @param details the details object
     * @return whether the field value is equivalent to <code>true</code> (<code>true</code>), or not
     * (<code>false</code>)
     */
    boolean isTrue(final Field field, final BasicInformationIncludable details) {
        Boolean include;
        try {
            include = (Boolean) new PropertyDescriptor(field.getName(), details.getClass())
                    .getReadMethod().invoke(details);
        } catch (Exception e) {
            // This should only arise should someone alter or remove the getter for a field.
            LOGGER.error("Error invoking getter for " + details.getClass().getSimpleName() + "." + field.getName(), e);
            include = null;
        }
        return TRUE.equals(include);
    }
}
