package uk.gov.companieshouse.certificates.orders.api.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.controller.ApiErrors;
import uk.gov.companieshouse.certificates.orders.api.model.DateOfBirthIncludable;
import uk.gov.companieshouse.certificates.orders.api.util.ApiErrorBuilder;
import uk.gov.companieshouse.certificates.orders.api.util.FieldNameConverter;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.TRUE;

@Component
public class DateOfBirthIncludeableValidator {
    private final FieldNameConverter fieldNameConverter;
    private final BasicInformationIncludeableValidator basicInformationIncludeableValidator;

    @Autowired
    public DateOfBirthIncludeableValidator(FieldNameConverter fieldNameConverter,
                                           BasicInformationIncludeableValidator basicInformationIncludeableValidator) {
        this.fieldNameConverter = fieldNameConverter;
        this.basicInformationIncludeableValidator = basicInformationIncludeableValidator;
    }

    List<ApiError> getValidationErrors(final DateOfBirthIncludable details, final String detailsFieldName) {
        List<ApiError> errors = new ArrayList<>();
        if (details == null || TRUE.equals(details.getIncludeBasicInformation())) {
            return errors;
        }
        errors = basicInformationIncludeableValidator.getValidationErrors(details, detailsFieldName);
        if (details.getIncludeDobType() != null) {
            errors.add(ApiErrorBuilder.builder(
                            new ApiError(ApiErrors.INCLUDE_DOB_TYPE_REQUIRED_ERROR, detailsFieldName + ".include_dob_type", ApiErrors.BOOLEAN_LOCATION_TYPE, ApiErrors.ERROR_TYPE_VALIDATION))
                    .withErrorMessage(detailsFieldName + ".include_dob_type: must not be non-null when include_basic_information is false")
                    .build());
        }
        return errors;
    }
}
