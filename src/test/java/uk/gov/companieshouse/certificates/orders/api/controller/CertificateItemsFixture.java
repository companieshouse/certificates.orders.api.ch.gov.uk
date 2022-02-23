package uk.gov.companieshouse.certificates.orders.api.controller;

import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.model.AdministratorsDetails;
import uk.gov.companieshouse.certificates.orders.api.model.LiquidatorsDetails;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyType;

import java.util.List;

class CertificateItemsFixture {
    private final CompanyType companyType;
    private final CompanyStatus companyStatus;
    private final Boolean includeGoodStandingInformation;
    private final LiquidatorsDetails liquidatorsDetails;
    private final List<ApiError> expectedErrors;
    private final AdministratorsDetails administratorsDetails;

    private CertificateItemsFixture(Builder builder) {
        companyType = builder.companyType;
        companyStatus = builder.companyStatus;
        includeGoodStandingInformation = builder.includeGoodStandingInformation;
        liquidatorsDetails = builder.liquidatorsDetails;
        expectedErrors = builder.expectedErrors;
        administratorsDetails = builder.administratorsDetails;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private CompanyType companyType;
        private CompanyStatus companyStatus;
        private Boolean includeGoodStandingInformation;
        private LiquidatorsDetails liquidatorsDetails;
        private List<ApiError> expectedErrors;
        private AdministratorsDetails administratorsDetails;

        private Builder() {
        }

        public Builder withCompanyType(CompanyType companyType) {
            this.companyType = companyType;
            return this;
        }

        public Builder withCompanyStatus(CompanyStatus companyStatus) {
            this.companyStatus = companyStatus;
            return this;
        }

        public Builder withIncludeGoodStandingInformation(Boolean includeGoodStandingInformation) {
            this.includeGoodStandingInformation = includeGoodStandingInformation;
            return this;
        }

        public Builder withLiquidatorsDetails(LiquidatorsDetails liquidatorsDetails) {
            this.liquidatorsDetails = liquidatorsDetails;
            return this;
        }

        public Builder withExpectedErrors(List<ApiError> expectedErrors) {
            this.expectedErrors = expectedErrors;
            return this;
        }

        public Builder withAdministratorsDetails(AdministratorsDetails administratorsDetails) {
            this.administratorsDetails = administratorsDetails;
            return this;
        }

        public CertificateItemsFixture build() {
            return new CertificateItemsFixture(this);
        }
    }

    public CompanyType getCompanyType() {
        return companyType;
    }

    public CompanyStatus getCompanyStatus() {
        return companyStatus;
    }

    public Boolean getIncludeGoodStandingInformation() {
        return includeGoodStandingInformation;
    }

    public LiquidatorsDetails getLiquidatorsDetails() {
        return liquidatorsDetails;
    }

    public AdministratorsDetails getAdministratorsDetails() {
        return administratorsDetails;
    }

    public List<ApiError> getExpectedErrors() {
        return expectedErrors;
    }
}
