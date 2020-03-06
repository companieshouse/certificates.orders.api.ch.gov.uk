package uk.gov.companieshouse.items.orders.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

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

    @JsonProperty("calculated_cost")
    private String calculatedCost;

    public ItemCosts() {
    }

    public ItemCosts(String discountApplied, String individualItemCost, String postageCost, String calculatedCost) {
        this.discountApplied = discountApplied;
        this.individualItemCost = individualItemCost;
        this.postageCost = postageCost;
        this.calculatedCost = calculatedCost;
    }

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

    public String getCalculatedCost() {
        return calculatedCost;
    }

    public void setCalculatedCost(String calculatedCost) {
        this.calculatedCost = calculatedCost;
    }

    @Override
    public String toString() { return new Gson().toJson(this); }

}
