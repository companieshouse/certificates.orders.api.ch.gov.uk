package uk.gov.companieshouse.certificates.orders.api.validator;

import java.util.HashMap;
import java.util.Map;

public enum CompanyType {
    LIMITED_PARTNERSHIP("limited-partnership"),
    LIMITED_LIABILITY_PARTNERSHIP("llp");

    private static final Map<String, CompanyType> enumValues = new HashMap<>();

    static {
        for (CompanyType companyType : values()) {
            enumValues.put(companyType.companyType, companyType);
        }
    }

    private final String companyType;

    CompanyType(String companyType) {
        this.companyType = companyType;
    }

    public static CompanyType getEnumValue(String companyType) {
        return enumValues.get(companyType);
    }

    @Override
    public String toString() {
        return companyType;
    }
}
