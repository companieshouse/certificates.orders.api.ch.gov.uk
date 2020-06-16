package uk.gov.companieshouse.certificates.orders.api.converter;

/**
 * Factors out common enum name conversion  to and from their JSON representations.
 * Ideally this class would be a bean, but the code to inject into an enum is very verbose in Java.
 */
public class EnumValueNameConverter {

    private EnumValueNameConverter() { }

    public static String convertEnumValueJsonToName(final String enumValueJson) {
        return enumValueJson.toUpperCase().replace("-", "_");
    }

    public static String convertEnumValueNameToJson(final Enum value) {
        return value.name().toLowerCase().replace("_", "-");
    }

}
