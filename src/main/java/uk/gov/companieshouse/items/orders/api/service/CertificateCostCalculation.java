package uk.gov.companieshouse.items.orders.api.service;

import uk.gov.companieshouse.items.orders.api.model.ItemCosts;

import java.util.List;

/**
 * An instance of this represents the outcome of a certificates cost calculation.
 */
public class CertificateCostCalculation {

    private final List<ItemCosts> itemCosts;
   private final String postageCost;

    public CertificateCostCalculation(List<ItemCosts> itemCosts, String postageCost) {
        this.itemCosts = itemCosts;
        this.postageCost = postageCost;
    }

    public List<ItemCosts> getItemCosts() {
        return itemCosts;
    }

    public String getPostageCost() {
        return postageCost;
    }

}
