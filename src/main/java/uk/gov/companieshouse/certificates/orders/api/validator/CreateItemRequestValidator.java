package uk.gov.companieshouse.certificates.orders.api.validator;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.util.FieldNameConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements validation of the request payload specific to the the create item request only.
 */
@Component
public class CreateItemRequestValidator extends RequestValidator {

    private final FieldNameConverter converter;

    /**
     * Constructor.
     * @param converter the converter this uses to present field names as they appear in the request JSON payload
     */
    public CreateItemRequestValidator(FieldNameConverter converter, CertificateOptionsValidator certificateOptionsValidator) {
        super(certificateOptionsValidator);
        this.converter = converter;
    }

    /**
     * Validates the item provided, returning any errors found.
     * @param item the item to be validated
     * @return the errors found, which will be empty if the item is found to be valid
     */
    public List<String> getValidationErrors(final CertificateItemDTO item) {
        final List<String> errors = new ArrayList<>();
        if (item.getId() != null) {
            errors.add("id: must be null in a create item request");
        }
        final CertificateItemOptions options = item.getItemOptions();
        errors.addAll(getValidationErrors(options, converter));
        return errors;
    }

}
