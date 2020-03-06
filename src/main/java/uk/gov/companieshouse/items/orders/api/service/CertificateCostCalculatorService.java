package uk.gov.companieshouse.items.orders.api.service;

import org.springframework.stereotype.Service;
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

    /**
     * Calculates the certificate item costs given the quantity and delivery timescale.
     * @param quantity the quantity of certificate items specified. Assumed to be >= 1.
     * @param deliveryTimescale the delivery time scale specified
     * @return the {@link List<ItemCosts>} calculated
     */
    public List<ItemCosts> calculateCosts(final int quantity, final DeliveryTimescale deliveryTimescale) {
        checkArguments(quantity, deliveryTimescale);
        final List<ItemCosts> costs = new ArrayList<>();
        for (int count = 1; count <= quantity; count++) {
            final ItemCosts cost = new ItemCosts();
            final int discountApplied = count > 1 ? deliveryTimescale.getExtraCertificateDiscount() : 0;
            cost.setDiscountApplied(Integer.toString(discountApplied));
            cost.setItemCost(Integer.toString(deliveryTimescale.getIndividualCertificateCost()));
            cost.setPostageCost(POSTAGE_COST);
            cost.setCalculatedCost(Integer.toString(deliveryTimescale.getIndividualCertificateCost() - discountApplied));
            final ProductType productType =
                    count > 1 ? deliveryTimescale.getAdditionalCertificatesProductType() :
                                deliveryTimescale.getFirstCertificateProductType();
            cost.setProductType(productType);
            costs.add(cost);
        }
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
