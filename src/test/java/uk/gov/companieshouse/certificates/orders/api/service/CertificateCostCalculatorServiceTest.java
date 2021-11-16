package uk.gov.companieshouse.certificates.orders.api.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale;
import uk.gov.companieshouse.certificates.orders.api.model.ItemCosts;
import uk.gov.companieshouse.certificates.orders.api.model.ProductType;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.gov.companieshouse.certificates.orders.api.model.ProductType.*;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.*;

/**
 * Unit/integration tests the {@link CertificateCostCalculatorService} class.
 */
@SpringBootTest
class CertificateCostCalculatorServiceTest {

    private static final String POSTAGE_COST = "0";
    private static final String NO_DISCOUNT = "0";
    private static final String STANDARD_INDIVIDUAL_CERTIFICATE_COST_STRING =
            Integer.toString(STANDARD_INDIVIDUAL_CERTIFICATE_COST);
    private static final String SAME_DAY_INDIVIDUAL_CERTIFICATE_COST_STRING =
            Integer.toString(SAME_DAY_INDIVIDUAL_CERTIFICATE_COST);

    private static final int MULTIPLE_QUANTITY = 3;
    private static final int SINGLE_QUANTITY = 1;

    @Autowired
    private CertificateCostCalculatorService calculatorUnderTest;

    @Test
    @DisplayName("Calculates standard delivery single certificate cost correctly")
    void calculatesStandardSingleCertificateCostCorrectly() {

        // Given and when
        final CertificateCostCalculation calculation =
                calculatorUnderTest.calculateCosts(SINGLE_QUANTITY, DeliveryTimescale.STANDARD);
        final List<ItemCosts> costs = calculation.getItemCosts();

        // Then
        assertThat(costs.size(), is(SINGLE_QUANTITY));
        final ItemCosts cost = costs.get(0);
        assertThat(cost.getItemCost(), is(STANDARD_INDIVIDUAL_CERTIFICATE_COST_STRING));
        assertThat(cost.getDiscountApplied(), is(NO_DISCOUNT));
        assertThat(cost.getCalculatedCost(), is(STANDARD_INDIVIDUAL_CERTIFICATE_COST_STRING));
        assertThat(cost.getProductType(), is(CERTIFICATE));
        assertThat(calculation.getPostageCost(), is(POSTAGE_COST));
        assertThat(calculation.getTotalItemCost(), is(calculateExpectedTotalItemCost(costs, POSTAGE_COST)));

    }

    @Test
    @DisplayName("Calculates standard delivery multiple certificate cost correctly")
    void calculatesStandardMultipleCertificateCostCorrectly() {

        // Given and when
        final CertificateCostCalculation calculation =
                calculatorUnderTest.calculateCosts(MULTIPLE_QUANTITY, DeliveryTimescale.STANDARD);
        final List<ItemCosts> costs = calculation.getItemCosts();

        // Then
        assertThat(costs.size(), is(MULTIPLE_QUANTITY));
        for (int index = 0; index < MULTIPLE_QUANTITY; index++) {
            final ItemCosts cost = costs.get(index);

            assertThat(cost.getItemCost(), is(STANDARD_INDIVIDUAL_CERTIFICATE_COST_STRING));

            final int expectedDiscountApplied = index > 0 ? STANDARD_EXTRA_CERTIFICATE_DISCOUNT : 0;
            assertThat(cost.getDiscountApplied(), is(Integer.toString(expectedDiscountApplied)));

            final String expectedCalculatedCost =
                    Integer.toString(Integer.parseInt(STANDARD_INDIVIDUAL_CERTIFICATE_COST_STRING) - expectedDiscountApplied);
            assertThat(cost.getCalculatedCost(), is(expectedCalculatedCost));
            final ProductType expectedProductType = index > 0 ? CERTIFICATE_ADDITIONAL_COPY : CERTIFICATE;
            assertThat(cost.getProductType(), is(expectedProductType));
        }

        assertThat(calculation.getPostageCost(), is(POSTAGE_COST));
        assertThat(calculation.getTotalItemCost(), is(calculateExpectedTotalItemCost(costs, POSTAGE_COST)));

    }

    @Test
    @DisplayName("Calculates same day delivery single certificate cost correctly")
    void calculatesSameDaySingleCertificateCostCorrectly() {

        // Given and when
        final CertificateCostCalculation calculation =
            calculatorUnderTest.calculateCosts(SINGLE_QUANTITY, DeliveryTimescale.SAME_DAY);
        final List<ItemCosts> costs = calculation.getItemCosts();

        // Then
        assertThat(costs.size(), is(SINGLE_QUANTITY));
        final ItemCosts cost = costs.get(0);
        assertThat(cost.getItemCost(), is(SAME_DAY_INDIVIDUAL_CERTIFICATE_COST_STRING));
        assertThat(cost.getDiscountApplied(), is(NO_DISCOUNT));
        assertThat(cost.getCalculatedCost(), is(SAME_DAY_INDIVIDUAL_CERTIFICATE_COST_STRING));
        assertThat(cost.getProductType(), is(CERTIFICATE_SAME_DAY));
        assertThat(calculation.getPostageCost(), is(POSTAGE_COST));
        assertThat(calculation.getTotalItemCost(), is(calculateExpectedTotalItemCost(costs, POSTAGE_COST)));

    }

    @Test
    @DisplayName("Calculates same day delivery multiple certificate cost correctly")
    void calculatesSameDayMultipleCertificateCostCorrectly() {

        // Given and when
        final CertificateCostCalculation calculation =
            calculatorUnderTest.calculateCosts(MULTIPLE_QUANTITY, DeliveryTimescale.SAME_DAY);
        final List<ItemCosts> costs = calculation.getItemCosts();

        // Then
        assertThat(costs.size(), is(MULTIPLE_QUANTITY));
        for (int index = 0; index < MULTIPLE_QUANTITY; index++) {

            final ItemCosts cost = costs.get(index);

            assertThat(cost.getItemCost(), is(SAME_DAY_INDIVIDUAL_CERTIFICATE_COST_STRING));

            final int expectedDiscountApplied = index > 0 ? SAME_DAY_EXTRA_CERTIFICATE_DISCOUNT : 0;
            assertThat(cost.getDiscountApplied(), is(Integer.toString(expectedDiscountApplied)));

            final String expectedCalculatedCost =
                    Integer.toString(Integer.parseInt(SAME_DAY_INDIVIDUAL_CERTIFICATE_COST_STRING) - expectedDiscountApplied);
            assertThat(cost.getCalculatedCost(), is(expectedCalculatedCost));
            final ProductType expectedProductType = index > 0 ? CERTIFICATE_ADDITIONAL_COPY : CERTIFICATE_SAME_DAY;
            assertThat(cost.getProductType(), is(expectedProductType));
        }

        assertThat(calculation.getPostageCost(), is(POSTAGE_COST));
        assertThat(calculation.getTotalItemCost(), is(calculateExpectedTotalItemCost(costs, POSTAGE_COST)));

    }

    @Test
    @DisplayName("Too few items result in an IllegalArgumentException")
    void tooFewItemsTriggerIllegalArgumentException() {
        final IllegalArgumentException exception =
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> calculatorUnderTest.calculateCosts(0, DeliveryTimescale.STANDARD));
        assertThat(exception.getMessage(), is("quantity must be greater than or equal to 1!"));
    }

    @Test
    @DisplayName("null delivery timescale results in an IllegalArgumentException")
    void noDeliveryTimescaleTriggersIllegalArgumentException() {
        final IllegalArgumentException exception =
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> calculatorUnderTest.calculateCosts(1, null));
        assertThat(exception.getMessage(), is("deliveryTimescale must not be null!"));
    }

    /**
     * Utility that calculates the expected total item cost for the item costs and postage cost provided.
     * @param costs the item costs
     * @param postageCost the postage cost
     * @return the expected total item cost (as a String)
     */
    private String calculateExpectedTotalItemCost(final List<ItemCosts> costs, final String postageCost) {
        final Integer total = costs.stream()
                               .map(itemCosts -> Integer.parseInt(itemCosts.getCalculatedCost()))
                               .reduce(0, Integer::sum) + Integer.parseInt(postageCost);
        return total.toString();
    }

}
