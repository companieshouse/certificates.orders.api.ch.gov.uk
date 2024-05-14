package uk.gov.companieshouse.certificates.orders.api.model;

import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.SnakeCaseFieldNamingStrategy;
import org.springframework.lang.NonNull;

/**
 * Extends {@link SnakeCaseFieldNamingStrategy} to change the naming strategy for boolean fields, to remove the
 * "is_" prefix from the field name where present.
 */
public class NoIsSnakeCaseFieldNamingStrategy extends SnakeCaseFieldNamingStrategy {

    @Override
    @NonNull
    public String getFieldName(@NonNull final PersistentProperty<?> property) {
        final String snakeCaseName = getSnakeCaseFieldName(property);
        final Class<?> fieldType = property.getType();
        if ((fieldType == Boolean.class || fieldType == boolean.class) && snakeCaseName.startsWith("is_")) {
            return snakeCaseName.substring(3);
        }
        return snakeCaseName;
    }

    protected String getSnakeCaseFieldName(final PersistentProperty<?> property) {
        return super.getFieldName(property);
    }

}
