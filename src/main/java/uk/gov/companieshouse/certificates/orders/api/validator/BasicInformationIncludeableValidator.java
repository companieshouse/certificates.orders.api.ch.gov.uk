package uk.gov.companieshouse.certificates.orders.api.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.controller.ApiErrors;
import uk.gov.companieshouse.certificates.orders.api.model.BasicInformationIncludable;
import uk.gov.companieshouse.certificates.orders.api.util.ApiErrorBuilder;
import uk.gov.companieshouse.certificates.orders.api.util.FieldNameConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;

@Component
public class BasicInformationIncludeableValidator {
    private final FieldNameConverter fieldNameConverter;

    @Autowired
    public BasicInformationIncludeableValidator(FieldNameConverter fieldNameConverter) {
        this.fieldNameConverter = fieldNameConverter;
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
        final List<ApiError> errors = new ArrayList<>();
        if (details == null || TRUE.equals(details.getIncludeBasicInformation())) {
            return errors;
        }
        FindByValueVisitor valueFinder = new FindByValueVisitor(TRUE);
        details.accept(valueFinder);
        final List<String> incorrectlySetFields = valueFinder.getKeys();
        if (!incorrectlySetFields.isEmpty()) {
            errors.addAll(incorrectlySetFields.stream().map(field -> ApiErrorBuilder.builder(
                            new ApiError(fieldNameConverter.toLowerHyphenCase(field) + "-error", detailsFieldName + "." + field, ApiErrors.BOOLEAN_LOCATION_TYPE, ApiErrors.ERROR_TYPE_VALIDATION))
                    .withErrorMessage(detailsFieldName + "." + field + ": must not be true when include_basic_information is false")
                    .build()).collect(Collectors.toList()));
        }
        return errors;
    }
}
