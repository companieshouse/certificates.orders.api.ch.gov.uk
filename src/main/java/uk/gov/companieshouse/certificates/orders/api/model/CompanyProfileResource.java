package uk.gov.companieshouse.certificates.orders.api.model;

import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;

public record CompanyProfileResource(String companyName, String companyType, CompanyStatus companyStatus) {

    @Override
    public String toString() {
        return "CompanyProfileResource{" +
                "companyName='" + companyName + '\'' +
                ", companyType='" + companyType + '\'' +
                ", companyStatus=" + companyStatus +
                '}';
    }
}
