package uk.gov.companieshouse.items.orders.api.service;

import uk.gov.companieshouse.items.orders.api.model.ItemCosts;

import java.util.List;

/**
 * An instance of this represents the outcome of a certificates cost calculation.
 */
public class CertificateCostCalculation {

    private final List<ItemCosts> itemCosts;
    private final String postageCost;
    private final String totalItemCost;

    public CertificateCostCalculation(List<ItemCosts> itemCosts, String postageCost, String totalItemCost) {
        this.itemCosts = itemCosts;
        this.postageCost = postageCost;
        this.totalItemCost = totalItemCost;
    }

    public List<ItemCosts> getItemCosts() {
        return itemCosts;
    }

    public String getPostageCost() {
        return postageCost;
    }

    public String getTotalItemCost() {
        return totalItemCost;
    }
}
