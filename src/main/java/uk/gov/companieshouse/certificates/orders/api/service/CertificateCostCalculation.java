package uk.gov.companieshouse.certificates.orders.api.service;

import uk.gov.companieshouse.certificates.orders.api.model.ItemCosts;

import java.util.List;

/**
 * An instance of this represents the outcome of a certificates cost calculation.
 */
public record CertificateCostCalculation(List<ItemCosts> itemCosts, String postageCost, String totalItemCost) {

}
