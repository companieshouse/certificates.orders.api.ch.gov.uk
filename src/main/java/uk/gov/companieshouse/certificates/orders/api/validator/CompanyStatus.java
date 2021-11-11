package uk.gov.companieshouse.certificates.orders.api.validator;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CompanyStatus {
    ACTIVE("active"),
    LIQUIDATION("liquidation"),
    OTHER("other");

    private static final Map<String, CompanyStatus> enumValues;

    static {
        enumValues = Arrays.stream(values()).filter(value -> value != OTHER).collect(
                Collectors.toMap(CompanyStatus::toString, Function.identity())
        );
    }

    private final String name;

    CompanyStatus(String companyStatus) {
        this.name = companyStatus;
    }

    public static CompanyStatus getEnumValue(String companyStatus) {
        return companyStatus != null ? enumValues.getOrDefault(companyStatus, OTHER) : null;
    }

    @Override
    public String toString() {
        return name;
    }
}
