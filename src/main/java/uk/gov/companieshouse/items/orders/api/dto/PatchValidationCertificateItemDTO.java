package uk.gov.companieshouse.items.orders.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Map;

/**
 * Instantiated from PATCH request JSON body to facilitate PATCH request validation.
 */
public class PatchValidationCertificateItemDTO {

    @Null
    @JsonProperty("id")
    private String id;

    @Null
    @JsonProperty("description")
    private String description;

    @Null
    @JsonProperty("description_identifier")
    private String descriptionIdentifier;

    @Null
    @JsonProperty("description_values")
    private Map<String, String> descriptionValues;

    @JsonProperty("item_options")
    private CertificateItemOptions itemOptions;

    @JsonProperty("company_number")
    private String companyNumber;

    @Null
    @JsonProperty("item_costs")
    private ItemCosts itemCosts;

    @Null
    @JsonProperty("kind")
    private String kind;

    @Null
    @JsonProperty("postal_delivery")
    private Boolean isPostalDelivery;

    @Min(1)
    @JsonProperty("quantity")
    private Integer quantity;

    @Override
    public String toString() { return new Gson().toJson(this); }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDescriptionIdentifier(String descriptionIdentifier) {
        this.descriptionIdentifier = descriptionIdentifier;
    }

    public void setDescriptionValues(Map<String, String> descriptionValues) {
        this.descriptionValues = descriptionValues;
    }

    public void setItemOptions(CertificateItemOptions itemOptions) {
        this.itemOptions = itemOptions;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public void setItemCosts(ItemCosts itemCosts) {
        this.itemCosts = itemCosts;
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
