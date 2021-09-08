package uk.gov.companieshouse.certificates.orders.api.model;

import java.util.Objects;

public class CompanyProfileResource {

    private String companyName;
    private String companyType;

    public CompanyProfileResource(String companyName, String companyType) {
        this.companyName = companyName;
        this.companyType = companyType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyType() {
        return companyType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CompanyProfileResource that = (CompanyProfileResource) o;
        return Objects.equals(companyName, that.companyName) && Objects.equals(companyType, that.companyType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName, companyType);
    }
}
