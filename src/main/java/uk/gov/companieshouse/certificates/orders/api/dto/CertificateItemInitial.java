package uk.gov.companieshouse.certificates.orders.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.validation.constraints.NotNull;

@JsonPropertyOrder(alphabetic = true)
public class CertificateItemInitial {
    @NotNull
    @JsonProperty("company_number")
    private String companyNumber;

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }
}
