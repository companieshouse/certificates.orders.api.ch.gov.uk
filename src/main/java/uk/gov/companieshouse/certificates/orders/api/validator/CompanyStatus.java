package uk.gov.companieshouse.certificates.orders.api.validator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CompanyStatus {
    ACTIVE("active"),
    LIQUIDATION("liquidation"),
    OTHER("other");

    private static final Map<String, CompanyStatus> enumValues = new HashMap<>();

    static {
        Arrays.stream(values()).filter(
                value -> value != OTHER
        ).collect(Collectors.toMap(
                CompanyStatus::toString, Function.identity()
        ));

        /*for (CompanyStatus companyStatus : values()) {
            enumValues.put(companyStatus.name, companyStatus);
        }*/
    }

    private final String name;

    CompanyStatus(String companyStatus) {
        this.name = companyStatus;
    }

    public static CompanyStatus getEnumValue(String companyStatus) {
        return enumValues.getOrDefault(companyStatus, OTHER);
    }
}
