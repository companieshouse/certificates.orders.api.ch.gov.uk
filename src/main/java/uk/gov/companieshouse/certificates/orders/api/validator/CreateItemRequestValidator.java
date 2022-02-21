package uk.gov.companieshouse.certificates.orders.api.validator;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.controller.ApiErrors;
import uk.gov.companieshouse.certificates.orders.api.util.ApiErrorBuilder;
import uk.gov.companieshouse.certificates.orders.api.util.FieldNameConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements validation of the request payload specific to the the create item request only.
 */
@Component
public class CreateItemRequestValidator extends RequestValidator {

    /**
     * Constructor.
     * @param converter the converter this uses to present field names as they appear in the request JSON payload
     */
    public CreateItemRequestValidator(FieldNameConverter converter,
                                      CertificateOptionsValidator certificateOptionsValidator,
                                      BasicInformationIncludeableValidator basicInformationIncludeableValidator,
                                      DateOfBirthIncludeableValidator dateOfBirthIncludeableValidator) {
        super(certificateOptionsValidator, basicInformationIncludeableValidator, dateOfBirthIncludeableValidator);
    }

    /**
     * Validates the item provided, returning any errors found.
     * @param requestValidatable to be validated
     * @return the errors found, which will be empty if the item is found to be valid
     */
    public List<ApiError> getValidationErrors(final RequestValidatable requestValidatable) {
        final List<ApiError> errors = new ArrayList<>();
        if (requestValidatable.getCertificateId() != null) {
            errors.add(ApiErrorBuilder.builder(ApiErrors.ERR_CERTIFICATE_ID_SUPPLIED)
                    .withErrorMessage("id: must be null in a create item request").build());
        }
        errors.addAll(super.getValidationErrors(requestValidatable));
        return errors;
    }
}
