package uk.gov.companieshouse.items.orders.api.util;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FieldNameConverter {

    /**
     * Converts the field name provided to its corresponding snake case representation.
     * Currently limited to a maximum of two words in the name.
     * @param fieldName the name of the field to be converted (typically camel case)
     * @return the field name's snake case representation
     */
    public String toSnakeCase(final String fieldName) {
        final Pattern pattern = Pattern.compile("(.+?)([A-Z][a-z]+)");
        final Matcher matcher = pattern.matcher(fieldName);
        if (!matcher.matches()) {
            return fieldName;
        }
        return matcher.group(1) + "_" + matcher.group(2).toLowerCase();
    }

}
