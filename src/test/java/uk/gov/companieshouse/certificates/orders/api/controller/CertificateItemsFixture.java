package uk.gov.companieshouse.certificates.orders.api.controller;

import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.model.LiquidatorsDetails;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;

import java.util.List;

class CertificateItemsFixture {
    private final String companyType;
    private final CompanyStatus companyStatus;
    private final Boolean includeGoodStandingInformation;
    private final LiquidatorsDetails liquidatorsDetails;
    private final List<ApiError> expectedErrors;

    private CertificateItemsFixture(Builder builder) {
        companyType = builder.companyType;
        companyStatus = builder.companyStatus;
        includeGoodStandingInformation = builder.includeGoodStandingInformation;
        liquidatorsDetails = builder.liquidatorsDetails;
        expectedErrors = builder.expectedErrors;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String companyType;
        private CompanyStatus companyStatus;
        private Boolean includeGoodStandingInformation;
        private LiquidatorsDetails liquidatorsDetails;
        private List<ApiError> expectedErrors;

        private Builder() {
        }

        public Builder withCompanyType(String companyType) {
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

        public CertificateItemsFixture build() {
            return new CertificateItemsFixture(this);
        }
    }

    public String getCompanyType() {
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

    public List<ApiError> getExpectedErrors() {
        return expectedErrors;
    }
}
