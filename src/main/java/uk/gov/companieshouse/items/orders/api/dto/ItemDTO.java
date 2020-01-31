package uk.gov.companieshouse.items.orders.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;

import javax.validation.constraints.Min;
import javax.validation.constraints.Null;

/**
 * An instance of this represents the JSON serializable item for use in REST requests and responses.
 */
public class ItemDTO extends AbstractItemDTO {

    @Null
    @JsonProperty("item_costs")
    private ItemCosts itemCosts;

    @Null
    @JsonProperty("etag")
    private String etag;

    @JsonProperty("kind")
    private String kind;

    private boolean isPostalDelivery;

    @Min(1)
    @JsonProperty("quantity")
    private int quantity;

    public String getId() {
        return id;
    }

    public ItemCosts getItemCosts() {
        return itemCosts;
    }

    public void setItemCosts(ItemCosts itemCosts) {
        this.itemCosts = itemCosts;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
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

}
