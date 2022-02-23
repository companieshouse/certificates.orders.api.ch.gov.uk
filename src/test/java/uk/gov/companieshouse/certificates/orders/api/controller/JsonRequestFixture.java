package uk.gov.companieshouse.certificates.orders.api.controller;

import org.apache.commons.io.IOUtils;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.model.AdministratorsDetails;
import uk.gov.companieshouse.certificates.orders.api.model.LiquidatorsDetails;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JsonRequestFixture {

    private final String jsonRequestResourceName;
    private final String jsonResponseResourceName;
    private final CompanyType companyType;
    private final CompanyStatus companyStatus;

    public JsonRequestFixture(Builder builder) {
        this.jsonRequestResourceName = builder.jsonRequestResourceName;
        this.jsonResponseResourceName = builder.jsonResponseResourceName;
        this.companyType = builder.companyType;
        this.companyStatus = builder.companyStatus;
    }

    public CompanyType getCompanyType() {
        return companyType;
    }

    public CompanyStatus getCompanyStatus() {
        return companyStatus;
    }

    public String getRequest() {
        try {
            return IOUtils.resourceToString(this.jsonRequestResourceName, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getExpectedResponse() {
        try {
            return IOUtils.resourceToString(this.jsonResponseResourceName, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private String jsonRequestResourceName;
        private String jsonResponseResourceName;
        private CompanyType companyType;
        private CompanyStatus companyStatus;

        private Builder() {
        }

        public Builder withRequestResource(String requestResource) {
            this.jsonRequestResourceName = requestResource;
            return this;
        }

        public Builder withExpectedResponseResource(String responseResource) {
            this.jsonResponseResourceName = responseResource;
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

        public JsonRequestFixture build() {
            return new JsonRequestFixture(this);
        }
    }
}
