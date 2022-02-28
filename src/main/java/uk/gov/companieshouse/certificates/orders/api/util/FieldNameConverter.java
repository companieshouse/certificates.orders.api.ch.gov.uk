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
    public String fromUpperCamelToSnakeCase(final String fieldName) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName).replace("is_", "");
    }

    public String fromLowerUnderscoreToLowerHyphenCase(final String fieldName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, fieldName).replace("is_", "");
    }

    public String fromCamelToLowerHyphenCase(final String fieldName) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, fieldName).replace("is_", "");
    }
}
