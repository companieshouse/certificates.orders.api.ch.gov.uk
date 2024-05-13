package uk.gov.companieshouse.certificates.orders.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.Gson;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptionsRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * An instance of this represents the JSON serializable certificate item for use in REST requests and responses.
 */
@JsonPropertyOrder(alphabetic = true)
public class CertificateItemCreate {
    @NotNull
    @JsonProperty("item_options")
    private CertificateItemOptionsRequest itemOptions;

    @NotNull
    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("customer_reference")
    private String customerReference;

    @JsonProperty("kind")
    private String kind;

    @Min(1)
    @JsonProperty("quantity")
    private int quantity;

    public CertificateItemOptionsRequest getItemOptions() {
        return itemOptions;
    }

    public void setItemOptions(CertificateItemOptionsRequest itemOptions) {
        this.itemOptions = itemOptions;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public String getCustomerReference() {
        return customerReference;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() { return new Gson().toJson(this); }

}
