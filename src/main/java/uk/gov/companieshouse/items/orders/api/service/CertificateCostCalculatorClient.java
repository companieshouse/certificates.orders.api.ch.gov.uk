package uk.gov.companieshouse.items.orders.api.service;

import uk.gov.companieshouse.items.orders.api.model.ItemCosts;

import java.util.List;

/**
 * Represents the client side of the contract between the {@link CertificateCostCalculatorService} and its
 * client.
 */
public interface CertificateCostCalculatorClient {
    Integer getQuantity();
    void setPostageCost(String postageCost);
    void setItemCosts(List<ItemCosts> itemCosts);
}
