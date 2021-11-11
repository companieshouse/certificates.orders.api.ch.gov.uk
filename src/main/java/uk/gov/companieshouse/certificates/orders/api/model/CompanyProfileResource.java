package uk.gov.companieshouse.certificates.orders.api.model;

import java.util.Objects;

public class CompanyProfileResource {

    private String companyName;
    private String companyType;
    private String companyStatus;

    public CompanyProfileResource(String companyName, String companyType, String companyStatus) {
        this.companyName = companyName;
        this.companyType = companyType;
        this.companyStatus = companyStatus;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyType() {
        return companyType;
    }

    public String getCompanyStatus() {
        return companyStatus;
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
        return Objects.equals(companyName, that.companyName) && Objects.equals(companyType,
                that.companyType) && Objects.equals(companyStatus, that.companyStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName, companyType, companyStatus);
    }
}
