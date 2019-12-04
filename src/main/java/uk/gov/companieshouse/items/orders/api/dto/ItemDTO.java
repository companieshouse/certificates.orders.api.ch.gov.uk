package uk.gov.companieshouse.items.orders.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;

import javax.validation.constraints.Min;
import javax.validation.constraints.Null;
import java.util.Map;
import java.util.Objects;

/**
 * An instance of this represents the JSON serializable item for use in REST requests and responses.
 */
public class ItemDTO {

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

    @Null
    @JsonProperty("item_costs")
    private ItemCosts itemCosts;

    @JsonProperty("kind")
    private String kind;

    private boolean isPostalDelivery;

    @Min(1)
    @JsonProperty("quantity")
    private int quantity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @JsonProperty("postal_delivery")
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemDTO)) return false;
        ItemDTO itemDTO = (ItemDTO) o;
        return isPostalDelivery == itemDTO.isPostalDelivery &&
                quantity == itemDTO.quantity &&
                Objects.equals(id, itemDTO.id) &&
                Objects.equals(description, itemDTO.description) &&
                Objects.equals(descriptionIdentifier, itemDTO.descriptionIdentifier) &&
                Objects.equals(descriptionValues, itemDTO.descriptionValues) &&
                Objects.equals(itemCosts, itemDTO.itemCosts) &&
                Objects.equals(kind, itemDTO.kind);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, descriptionIdentifier, descriptionValues, itemCosts, kind, isPostalDelivery, quantity);
    }
}
