package uk.gov.companieshouse.items.orders.api.validator;

import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.companieshouse.items.orders.api.model.CertificateType.DISSOLUTION_LIQUIDATION;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryMethod.COLLECTION;

/**
 * Implements common request payload validation.
 */
public class RequestValidator {

    /**
     * Validates the options provided, returning any errors found.
     * @param options the options to be validated
     * @return the errors found, which will be empty if the item is found to be valid
     */
    List<String> getValidationErrors(final CertificateItemOptions options) {
        final List<String> errors = new ArrayList<>();
        if (options == null) {
            return errors;
        }
        if (options.getDeliveryMethod() == COLLECTION &&
                options.getCollectionLocation() == null) {
            errors.add("collection_location: must not be null when delivery method is collection");
        }
        if (options.getCertificateType() == DISSOLUTION_LIQUIDATION && options.getIncludeCompanyObjectsInformation()) {
            errors.add(
                    "include_company_objects_information: must not be true when certificate type is dissolution_liquidation");
        }
        return errors;
    }

}
