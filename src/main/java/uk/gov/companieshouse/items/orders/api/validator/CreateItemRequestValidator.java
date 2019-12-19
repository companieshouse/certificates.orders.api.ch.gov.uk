package uk.gov.companieshouse.items.orders.api.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.items.orders.api.dto.ItemDTO;
import uk.gov.companieshouse.items.orders.api.dto.PatchValidationCertificateItemDTO;

import javax.json.JsonMergePatch;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implements validation of the request payload specific to the the create item request only.
 */
@Component
public class CreateItemRequestValidator {

    private final ObjectMapper objectMapper;
    private final Validator validator;

    /**
     * Constructor.
     * @param objectMapper the object mapper this relies upon to deserialise JSON
     * @param validator the validator this relies upon to validate DTOs
     */
    public CreateItemRequestValidator(ObjectMapper objectMapper, Validator validator) {
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    /**
     * Validates the item provided, returning any errors found.
     * @param item the item to be validated
     * @return the errors found, which will be empty if the item is found to be valid
     */
    public List<String> getValidationErrors(final ItemDTO item) {
        final List<String> errors = new ArrayList<>();
        if (item.getId() != null) {
            errors.add("id: must be null in a create item request");
        }
        return errors;
    }

    /**
     * TODO Move method or re-purpose this class?
     * Validates the patch provided, returning any errors found.
     * @param patch the item to be validated
     * @return the errors found, which will be empty if the item is found to be valid
     * @throws IOException TODO should this actually be an error with the input => Bad Request?
     */
    public List<String> getValidationErrors(final JsonMergePatch patch) throws IOException {
        final PatchValidationCertificateItemDTO dto =
                objectMapper.readValue(patch.toJsonValue().toString(), PatchValidationCertificateItemDTO.class);
        final Set<ConstraintViolation<PatchValidationCertificateItemDTO>> violations = validator.validate(dto);
        return violations.stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());
    }

}
