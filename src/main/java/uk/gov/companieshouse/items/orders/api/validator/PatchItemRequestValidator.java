package uk.gov.companieshouse.items.orders.api.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.items.orders.api.dto.PatchValidationCertificateItemDTO;

import javax.json.JsonMergePatch;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implements validation of the request payload specific to the the patch item request only.
 */
@Component
public class PatchItemRequestValidator {

    private final ObjectMapper objectMapper;
    private final Validator validator;

    /**
     * Constructor.
     * @param objectMapper the object mapper this relies upon to deserialise JSON
     * @param validator the validator this relies upon to validate DTOs
     */
    public PatchItemRequestValidator(ObjectMapper objectMapper, Validator validator) {
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    /**
     * Validates the patch provided, returning any errors found.
     * @param patch the item to be validated
     * @return the errors found, which will be empty if the item is found to be valid
     */
    public List<String> getValidationErrors(final JsonMergePatch patch) {
        try {
            final PatchValidationCertificateItemDTO dto =
                    objectMapper.readValue(patch.toJsonValue().toString(), PatchValidationCertificateItemDTO.class);
            final Set<ConstraintViolation<PatchValidationCertificateItemDTO>> violations = validator.validate(dto);
            return violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            // This exception will not occur because there are no low-level IO operations here.
            return null;
        }
    }

}
