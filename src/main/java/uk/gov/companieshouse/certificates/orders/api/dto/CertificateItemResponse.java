package uk.gov.companieshouse.certificates.orders.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.Gson;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * An instance of this represents the JSON serializable certificate item for use in REST requests and responses.
 */
@JsonPropertyOrder(alphabetic = true)
public class CertificateItemResponse extends ItemDTO {
    @NotNull
    @JsonProperty("id")
    private String id;

    @NotNull
    @JsonProperty("item_options")
    private CertificateItemOptions itemOptions;

    @Null
    @JsonProperty("company_name")
    private String companyName;

    @NotNull
    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("customer_reference")
    private String customerReference;

    @JsonProperty("user_id")
    private String userId;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public CertificateItemOptions getItemOptions() {
        return itemOptions;
    }

    public void setItemOptions(CertificateItemOptions itemOptions) {
        this.itemOptions = itemOptions;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() { return new Gson().toJson(this); }

}
