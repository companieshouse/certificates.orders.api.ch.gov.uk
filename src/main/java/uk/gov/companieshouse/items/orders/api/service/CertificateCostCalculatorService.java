package uk.gov.companieshouse.items.orders.api.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;

@Service
public class CertificateCostCalculatorService {
    public ItemCosts calculateCosts(final int quantity, final DeliveryTimescale deliveryTimescale) {
        // TODO PCI-506 Calculate the actual costs.
        final ItemCosts costs = new ItemCosts();
        costs.setDiscountApplied("1");
        costs.setIndividualItemCost("2");
        costs.setPostageCost("3");
        costs.setTotalCost("4");
        return costs;
    }
}
