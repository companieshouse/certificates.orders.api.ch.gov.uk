package uk.gov.companieshouse.items.orders.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An instance of this represents the item's costs.
 */
public class ItemCosts {

    @JsonProperty("discount_applied")
    private String discountApplied;

    @JsonProperty("individual_item_cost")
    private String individualItemCost;

    @JsonProperty("postage_cost")
    private String postageCost;

    @JsonProperty("total_cost")
    private String totalCost;

    public String getDiscountApplied() {
        return discountApplied;
    }

    public void setDiscountApplied(String discountApplied) {
        this.discountApplied = discountApplied;
    }

    public String getIndividualItemCost() {
        return individualItemCost;
    }

    public void setIndividualItemCost(String individualItemCost) {
        this.individualItemCost = individualItemCost;
    }

    public String getPostageCost() {
        return postageCost;
    }

    public void setPostageCost(String postageCost) {
        this.postageCost = postageCost;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    @Override
    public String toString() {
        return "ItemCosts{" +
                "discountApplied='" + discountApplied + '\'' +
                ", individualItemCost='" + individualItemCost + '\'' +
                ", postageCost='" + postageCost + '\'' +
                ", totalCost='" + totalCost + '\'' +
                '}';
    }
}
