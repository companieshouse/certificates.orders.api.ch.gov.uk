package uk.gov.companieshouse.certificates.orders.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptionsRequest;

import javax.validation.constraints.Min;

/**
 * Instantiated from PATCH request JSON body to facilitate PATCH request validation.
 */
public class PatchValidationCertificateItemDTO {

    @JsonProperty("item_options")
    private CertificateItemOptionsRequest itemOptions;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("customer_reference")
    private String customerReference;

    @Min(1)
    @JsonProperty("quantity")
    private Integer quantity;

    public void setItemOptions(CertificateItemOptionsRequest itemOptions) {
        this.itemOptions = itemOptions;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
