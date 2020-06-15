package uk.gov.companieshouse.certificates.orders.api.util;

import com.google.common.base.CaseFormat;
import org.springframework.stereotype.Component;

@Component
public class FieldNameConverter {

    /**
     * Converts the field name provided to its corresponding snake case representation.
     * @param fieldName the name of the field to be converted (typically camel case)
     * @return the field name's snake case representation minus any <code>is_</code>
     * string, assumed to be a prefix.
     */
    public String toSnakeCase(final String fieldName) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName).replace("is_", "");
    }

}
