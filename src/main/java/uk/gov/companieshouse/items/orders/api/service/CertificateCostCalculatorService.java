package uk.gov.companieshouse.items.orders.api.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.items.orders.api.config.CostsConfig;
import uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;
import uk.gov.companieshouse.items.orders.api.model.ProductType;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that calculates certificate item costs.
 */
@Service
public class CertificateCostCalculatorService {

    private static final String POSTAGE_COST = "0";

    private final CostsConfig costs;

    /**
     * Constructor.
     * @param costs the configured costs used by this in its calculations
     */
    public CertificateCostCalculatorService(final CostsConfig costs) {
        this.costs = costs;
    }

    /**
     * Calculates the certificate item costs given the quantity and delivery timescale.
     * @param quantity the quantity of certificate items specified. Assumed to be >= 1.
     * @param deliveryTimescale the delivery time scale specified
     * @return the outcome of the costs calculations
     */
    public CertificateCostCalculation calculateCosts(final int quantity,
                                                     final DeliveryTimescale deliveryTimescale) {
        checkArguments(quantity, deliveryTimescale);
        final List<ItemCosts> itemCosts = new ArrayList<>();
        for (int certificateNumber = 1; certificateNumber <= quantity; certificateNumber++) {
            final ItemCosts cost = calculateSingleItemCosts(certificateNumber, deliveryTimescale);
            itemCosts.add(cost);
        }
        final String totalItemCost = calculateTotalItemCost(itemCosts, POSTAGE_COST);
        return new CertificateCostCalculation(itemCosts, POSTAGE_COST, totalItemCost);
    }

    /**
     * Calculates the costs for a single certificate.
     * @param certificateNumber the number of the certificate, used to determine whether the certificate is the first
     *                          and therefore full price, or an additional certificate, and therefore discounted
     * @param deliveryTimescale the delivery timescale
     * @return the costs for the certificate
     */
    private ItemCosts calculateSingleItemCosts(final int certificateNumber, final DeliveryTimescale deliveryTimescale) {
        final ItemCosts cost = new ItemCosts();
        final int discountApplied = certificateNumber > 1 ? deliveryTimescale.getExtraCertificateDiscount(costs) : 0;
        cost.setDiscountApplied(Integer.toString(discountApplied));
        cost.setItemCost(Integer.toString(deliveryTimescale.getIndividualCertificateCost(costs)));
        final int calculatedCost = deliveryTimescale.getIndividualCertificateCost(costs) - discountApplied;
        cost.setCalculatedCost(Integer.toString(calculatedCost));
        final ProductType productType =
                certificateNumber > 1 ? deliveryTimescale.getAdditionalCertificatesProductType() :
                        deliveryTimescale.getFirstCertificateProductType();
        cost.setProductType(productType);
        return cost;
    }

    /**
     * Utility that calculates the total item cost from the item costs and postage cost provided. This is the total
     * cost of all of the certificates, including postage.
     * @param costs the item costs
     * @param postageCost the postage cost
     * @return the total item cost (as a String)
     */
    private String calculateTotalItemCost(final List<ItemCosts> costs, final String postageCost) {
        final int total = costs.stream()
                .map(itemCosts -> Integer.parseInt(itemCosts.getCalculatedCost()))
                .reduce(0, Integer::sum) + Integer.parseInt(postageCost);
        return Integer.toString(total);
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
