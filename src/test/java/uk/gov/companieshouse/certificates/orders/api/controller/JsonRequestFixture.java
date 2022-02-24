package uk.gov.companieshouse.certificates.orders.api.controller;

import uk.gov.companieshouse.certificates.orders.api.service.CompanyServiceException;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyType;

public class JsonRequestFixture {

    private final String savedResource;
    private final String requestBody;
    private final int expectedResponseCode;
    private final String expectedResponseBody;
    private final CompanyType companyType;
    private final CompanyStatus companyStatus;
    private final String description;
    private final CompanyServiceException companyServiceException;

    public JsonRequestFixture(Builder builder) {
        this.savedResource = builder.savedResource;
        this.requestBody = builder.requestBody;
        this.expectedResponseCode = builder.expectedResponseCode;
        this.expectedResponseBody = builder.expectedResponseBody;
        this.companyType = builder.companyType;
        this.companyStatus = builder.companyStatus;
        this.description = builder.description;
        this.companyServiceException = builder.companyServiceException;
    }

    public String getSavedResource() {
        return savedResource;
    }

    public CompanyType getCompanyType() {
        return companyType;
    }

    public CompanyStatus getCompanyStatus() {
        return companyStatus;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getExpectedResponseBody() {
        return expectedResponseBody;
    }

    public int getExpectedResponseCode() {
        return expectedResponseCode;
    }

    public CompanyServiceException getCompanyServiceException() {
        return companyServiceException;
    }

    @Override
    public String toString() {
        return this.description;
    }

    public static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private String savedResource;
        private String requestBody;
        private String expectedResponseBody;
        private int expectedResponseCode;
        private CompanyType companyType;
        private CompanyStatus companyStatus;
        private String description;
        private CompanyServiceException companyServiceException;

        private Builder() {
        }

        public Builder withSavedResource(String savedResource) {
            this.savedResource = savedResource;
            return this;
        }

        public Builder withRequestBody(String requestResource) {
            this.requestBody = requestResource;
            return this;
        }

        public Builder withExpectedResponseBody(String responseResource) {
            this.expectedResponseBody = responseResource;
            return this;
        }

        public Builder withExpectedResponseCode(int responseCode) {
            this.expectedResponseCode = responseCode;
            return this;
        }

        public Builder withCompanyType(CompanyType companyType) {
            this.companyType = companyType;
            return this;
        }

        public Builder withCompanyStatus(CompanyStatus companyStatus) {
            this.companyStatus = companyStatus;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withCompanyServiceException(CompanyServiceException exception) {
            this.companyServiceException = exception;
            return this;
        }

        public JsonRequestFixture build() {
            return new JsonRequestFixture(this);
        }
    }
}
