package uk.gov.companieshouse.certificates.orders.api.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.controller.ApiErrors;
import uk.gov.companieshouse.certificates.orders.api.dto.PatchValidationCertificateItemDTO;
import uk.gov.companieshouse.certificates.orders.api.util.ApiErrorBuilder;
import uk.gov.companieshouse.certificates.orders.api.util.FieldNameConverter;

import javax.json.JsonMergePatch;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonList;

/**
 * Implements validation of the request payload specific to the the patch item request only.
 */
@Component
public class PatchItemRequestValidator {

    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final FieldNameConverter converter;

    /**
     * Constructor.
     * @param objectMapper the object mapper this relies upon to deserialise JSON
     * @param validator the validator this relies upon to validate DTOs
     * @param converter the converter this uses to present field names as they appear in the request JSON payload
     */
    public PatchItemRequestValidator(final ObjectMapper objectMapper,
                                     final Validator validator,
                                     final FieldNameConverter converter) {
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.converter = converter;
    }

    /**
     * Validates the patch provided, returning any errors found.
     * @param patch the item to be validated
     * @return the errors found, which will be empty if the item is found to be valid
     */
    public List<ApiError> getValidationErrors(final JsonMergePatch patch) {
        try {
            final PatchValidationCertificateItemDTO dto =
                    objectMapper.readValue(patch.toJsonValue().toString(), PatchValidationCertificateItemDTO.class);
            final Set<ConstraintViolation<PatchValidationCertificateItemDTO>> violations = validator.validate(dto);
            return violations.stream()
                    .sorted(Comparator.comparing(a -> a.getPropertyPath().toString()))
                    .map(this::raiseError)
                    .collect(Collectors.toList());
        } catch (JsonProcessingException jpe) {
            return singletonList(ApiErrors.ERR_JSON_PROCESSING);
        }
    }

    private ApiError raiseError(ConstraintViolation<PatchValidationCertificateItemDTO> violation) {
        String fieldName = violation.getPropertyPath().toString();
        String snakeCaseFieldName = converter.toSnakeCase(fieldName);
        return ApiErrorBuilder.builder(new ApiError(converter.toLowerHyphenCase(snakeCaseFieldName) + "-error", snakeCaseFieldName, ApiErrors.OBJECT_LOCATION_TYPE, ApiErrors.ERROR_TYPE_VALIDATION))
                .withErrorMessage(snakeCaseFieldName + ": " + violation.getMessage())
                .build();
    }
}
