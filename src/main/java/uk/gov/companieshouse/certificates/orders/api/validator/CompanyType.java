package uk.gov.companieshouse.certificates.orders.api.validator;

import java.util.HashMap;
import java.util.Map;

public enum CompanyType {

    LIMITED_PARTNERSHIP("limited-partnership"),
    LIMITED_LIABILITY_PARTNERSHIP("llp"),
    LIMITED_COMPANY("ltd"),
    PUBLIC_LIMITED_COMPANY("plc"),
    OLD_PUBLIC_COMPANY("old-public-company"),
    PRIVATE_LIMITED_GUARANT_NSC("private-limited-guarant-nsc"),
    PRIVATE_LIMITED_GUARANT_NSC_LIMITED_EXEMPTION("private-limited-guarant-nsc-limited-exemption"),
    PRIVATE_LIMITED_SHARES_SECTION_30_EXEMPTION("private-limited-shares-section-30-exemption"),
    PRIVATE_UNLIMITED("private-unlimited"),
    PRIVATE_UNLIMITED_NSC("private-unlimited-nsc");

    private static final Map<String, CompanyType> enumValues = new HashMap<>();

    static {
        for (CompanyType companyType : values()) {
            enumValues.put(companyType.name, companyType);
        }
    }

    private final String name;

    CompanyType(String companyType) {
        this.name = companyType;
    }

    public static CompanyType getEnumValue(String companyType) {
        return enumValues.get(companyType);
    }

    public String getCompanyType() {
        return name;
    }
}
