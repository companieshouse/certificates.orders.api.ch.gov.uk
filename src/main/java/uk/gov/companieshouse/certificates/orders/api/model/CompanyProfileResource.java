package uk.gov.companieshouse.certificates.orders.api.model;

import java.util.Objects;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;

public class CompanyProfileResource {

    private final String companyName;
    private final String companyType;
    private final CompanyStatus companyStatus;

    public CompanyProfileResource(String companyName,
            String companyType,
            CompanyStatus companyStatus) {
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

    public CompanyStatus getCompanyStatus() {
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

    @Override
    public String toString() {
        return "CompanyProfileResource{" +
                "companyName='" + companyName + '\'' +
                ", companyType='" + companyType + '\'' +
                ", companyStatus=" + companyStatus +
                '}';
    }
}
