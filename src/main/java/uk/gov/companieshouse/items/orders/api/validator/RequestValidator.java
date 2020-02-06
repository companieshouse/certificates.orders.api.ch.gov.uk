package uk.gov.companieshouse.items.orders.api.validator;

import org.springframework.util.ReflectionUtils;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.items.orders.api.model.DirectorOrSecretaryDetails;
import uk.gov.companieshouse.items.orders.api.util.FieldNameConverter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static java.util.Arrays.stream;
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
     * @param converter the converter this uses to present field names as they appear in the request JSON payload
     * @return the errors found, which will be empty if the item is found to be valid
     */
    List<String> getValidationErrors(final CertificateItemOptions options, final FieldNameConverter converter) {
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
        errors.addAll(getValidationErrors(options.getDirectorDetails(), "director_details", converter));
        errors.addAll(getValidationErrors(options.getSecretaryDetails(), "secretary_details", converter));
        return errors;
    }

    /**
     * Validates the details provided, returning any errors found, prefixed with the supplied details field name.
     * @param details the details to be validated
     * @param detailsFieldName the field name of the details to be validated
     * @param converter the converter this uses to present field names as they appear in the request JSON payload
     * @return the resulting errors, which will be empty if the details are found to be valid
     */
    List<String> getValidationErrors(final DirectorOrSecretaryDetails details,
                                     final String detailsFieldName,
                                     final FieldNameConverter converter) {
        final List<String> errors = new ArrayList<>();
        if (details == null || TRUE.equals(details.getIncludeBasicInformation())) {
            return errors;
        }
        final Field[] fields = details.getClass().getDeclaredFields();
        final List<String> incorrectlySetFields = stream(fields)
                .filter(field ->
                        !field.getName().equals("includeBasicInformation") &&
                        field.getType().equals(Boolean.class) &&
                        isTrue(field, details))
                .map(field -> converter.toSnakeCase(field.getName()))
                .collect(Collectors.toList());
        if (!incorrectlySetFields.isEmpty()) {
            final String fieldList = incorrectlySetFields.toString().replace("[", "").replace("]", "");
            errors.add(detailsFieldName + ": " + fieldList + " must not be true when include_basic_information is false");
        }
        if (details.getIncludeDobType() != null) {
            errors.add(detailsFieldName + ": include_dob_type must not be non-null when include_basic_information is false");
        }
        return errors;
    }

    /**
     * Determines whether the value of the field on the details object is equivalent to <code>true</code> or not.
     * @param field the reflective representation of the details field
     * @param details the details object
     * @return whether the field value is equivalent to <code>true</code> (<code>true</code>), or not
     * (<code>false</code>)
     */
    boolean isTrue(final Field field, final DirectorOrSecretaryDetails details) {
        field.setAccessible(true);
        final Boolean include = (Boolean) ReflectionUtils.getField(field, details);
        return TRUE.equals(include);
    }

}
