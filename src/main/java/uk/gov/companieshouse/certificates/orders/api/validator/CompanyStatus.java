package uk.gov.companieshouse.certificates.orders.api.validator;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CompanyStatus {
    ACTIVE("active"),
    LIQUIDATION("liquidation"),
    DISSOLVED("dissolved"),
    ADMINISTRATION("administration");

    private static final Map<String, CompanyStatus> enumValues;

    static {
        enumValues = Arrays.stream(values())
                .collect(Collectors.toMap(CompanyStatus::toString, Function.identity()));
    }

    private final String statusName;

    CompanyStatus(String companyStatus) {
        this.statusName = companyStatus;
    }

    public static CompanyStatus getEnumValue(String companyStatus) {
        return companyStatus != null ? enumValues.get(companyStatus) : null;
    }

    public String getStatusName() {
        return statusName;
    }

    @Override
    public String toString() {
        return statusName;
    }
}
