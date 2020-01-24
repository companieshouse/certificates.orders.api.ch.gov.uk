package uk.gov.companieshouse.items.orders.api.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;

/**
 * Service that calculates certificate item costs.
 */
@Service
public class CertificateCostCalculatorService {

    private static final String POSTAGE_COST = "0";

    /**
     * Calculates the certificate item costs given the quantity and delivery timescale.
     * @param quantity the quantity of certificate items specified
     * @param deliveryTimescale the delivery time scale specified
     * @return the {@link ItemCosts} calculated
     */
    public ItemCosts calculateCosts(final int quantity, final DeliveryTimescale deliveryTimescale) {
        checkArguments(quantity, deliveryTimescale);
        final ItemCosts costs = new ItemCosts();
        final int discountApplied = (quantity - 1) * deliveryTimescale.getExtraCertificateDiscount();
        costs.setDiscountApplied(Integer.toString(discountApplied));
        costs.setIndividualItemCost(Integer.toString(deliveryTimescale.getIndividualCertificateCost()));
        costs.setPostageCost(POSTAGE_COST);
        costs.setTotalCost(
                Integer.toString(quantity * deliveryTimescale.getIndividualCertificateCost() - discountApplied));
        return costs;
    }

    /**
     * Utility method that checks the arguments provided to it. Throws an {@link IllegalArgumentException} should these
     * be outside of the range of reasonable values.
     * @param quantity the quantity of certificate items specified
     * @param deliveryTimescale the delivery time scale specified
     */
    private void checkArguments(final int quantity, final DeliveryTimescale deliveryTimescale) {
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be greater than or equal to 1!");
        }
        if (deliveryTimescale == null) {
            throw new IllegalArgumentException("deliveryTimescale must not be null!");
        }
    }
}
