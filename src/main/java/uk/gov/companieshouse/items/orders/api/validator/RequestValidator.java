package uk.gov.companieshouse.items.orders.api.validator;

import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static uk.gov.companieshouse.items.orders.api.model.CertificateType.DISSOLUTION_LIQUIDATION;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryMethod.COLLECTION;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.SAME_DAY;

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
        if (options.getCertificateType() == DISSOLUTION_LIQUIDATION) {
            if (options.getIncludeCompanyObjectsInformation()) {
                errors.add(
                        "include_company_objects_information: must not be true when certificate type is dissolution_liquidation");
            }
            if (options.getIncludeGoodStandingInformation()) {
                errors.add(
                        "include_good_standing_information: must not be true when certificate type is dissolution_liquidation");
            }
        }
        if (TRUE.equals(options.getIncludeEmailCopy()) &&
                (options.getDeliveryTimescale() != SAME_DAY)) {
            errors.add("include_email_copy: can only be true when delivery timescale is same_day");
        }
        return errors;
    }

}
