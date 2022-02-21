package uk.gov.companieshouse.certificates.orders.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptionsRequest;
import uk.gov.companieshouse.certificates.orders.api.model.ItemCosts;

import javax.validation.constraints.Min;
import javax.validation.constraints.Null;

/**
 * Instantiated from PATCH request JSON body to facilitate PATCH request validation.
 */
public class PatchValidationCertificateItemDTO extends AbstractItemDTO {

    @JsonProperty("item_options")
    private CertificateItemOptionsRequest itemOptions;

    @Null
    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("customer_reference")
    private String customerReference;

    @Null
    @JsonProperty("item_costs")
    private ItemCosts itemCosts;

    @Null
    @JsonProperty("etag")
    private String etag;

    @Null
    @JsonProperty("kind")
    private String kind;

    @Null
    @JsonProperty("postal_delivery")
    private Boolean isPostalDelivery;

    @Min(1)
    @JsonProperty("quantity")
    private Integer quantity;

    @Null
    @JsonProperty("user_id")
    private String userId;

    @Override
    public String toString() { return new Gson().toJson(this); }

    @Null
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setItemOptions(CertificateItemOptionsRequest itemOptions) {
        this.itemOptions = itemOptions;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public void setItemCosts(ItemCosts itemCosts) {
        this.itemCosts = itemCosts;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setPostalDelivery(Boolean postalDelivery) {
        isPostalDelivery = postalDelivery;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
