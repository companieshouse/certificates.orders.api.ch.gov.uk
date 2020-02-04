package uk.gov.companieshouse.items.orders.api.validator;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements validation of the request payload specific to the the create item request only.
 */
@Component
public class CreateItemRequestValidator extends RequestValidator {

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
        errors.addAll(getValidationErrors(options));
        return errors;
    }

}
