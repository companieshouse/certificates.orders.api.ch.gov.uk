package uk.gov.companieshouse.items.orders.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;

import java.util.Map;

/**
 * An instance of this represents the JSON serializable item for use in REST requests and responses.
 */
public class ItemDTO {

    private String id;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("description")
    private String description;

    @JsonProperty("description_identifier")
    private String descriptionIdentifier;

    @JsonProperty("description_values")
    private Map<String, String> descriptionValues;

    @JsonProperty("item_costs")
    private ItemCosts itemCosts;

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("postal_delivery")
    private boolean isPostalDelivery;

    @JsonProperty("quantity")
    private int quantity;

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionIdentifier() {
        return descriptionIdentifier;
    }

    public void setDescriptionIdentifier(String descriptionIdentifier) {
        this.descriptionIdentifier = descriptionIdentifier;
    }

    public Map<String, String> getDescriptionValues() {
        return descriptionValues;
    }

    public void setDescriptionValues(Map<String, String> descriptionValues) {
        this.descriptionValues = descriptionValues;
    }

    public ItemCosts getItemCosts() {
        return itemCosts;
    }

    public void setItemCosts(ItemCosts itemCosts) {
        this.itemCosts = itemCosts;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public boolean isPostalDelivery() {
        return isPostalDelivery;
    }

    public void setPostalDelivery(boolean postalDelivery) {
        isPostalDelivery = postalDelivery;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ItemDTO{" +
                "id='" + id + '\'' +
                ", companyNumber='" + companyNumber + '\'' +
                ", description='" + description + '\'' +
                ", descriptionIdentifier='" + descriptionIdentifier + '\'' +
                ", descriptionValues=" + descriptionValues +
                ", itemCosts=" + itemCosts +
                ", kind='" + kind + '\'' +
                ", isPostalDelivery=" + isPostalDelivery +
                ", quantity=" + quantity +
                '}';
    }
}
