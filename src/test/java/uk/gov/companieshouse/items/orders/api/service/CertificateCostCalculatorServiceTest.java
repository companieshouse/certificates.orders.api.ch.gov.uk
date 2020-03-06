package uk.gov.companieshouse.items.orders.api.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Unit tests the {@link CertificateCostCalculatorService} class.
 */
public class CertificateCostCalculatorServiceTest {

    private static final String POSTAGE_COST = "0";
    private static final String NO_DISCOUNT = "0";
    private static final String STANDARD_INDIVIDUAL_CERTIFICATE_COST = "15";
    private static final String SAME_DAY_INDIVIDUAL_CERTIFICATE_COST = "50";

    private static final int STANDARD_EXTRA_CERTIFICATE_DISCOUNT = 5;
    private static final int SAME_DAY_EXTRA_CERTIFICATE_DISCOUNT = 40;

    private final CertificateCostCalculatorService calculatorUnderTest = new CertificateCostCalculatorService();

    @Test
    @DisplayName("Calculates standard delivery single certificate cost correctly")
    void calculatesStandardSingleCertificateCostCorrectly() {

        // Given and when
        final List<ItemCosts> costs = calculatorUnderTest.calculateCosts(1, DeliveryTimescale.STANDARD);
        final ItemCosts cost = costs.get(0);

        // Then
        assertThat(cost.getIndividualItemCost(), is(STANDARD_INDIVIDUAL_CERTIFICATE_COST));
        assertThat(cost.getDiscountApplied(), is(NO_DISCOUNT));
        assertThat(cost.getPostageCost(), is(POSTAGE_COST));
        assertThat(cost.getTotalCost(), is(STANDARD_INDIVIDUAL_CERTIFICATE_COST));

    }

    @Test
    @DisplayName("Calculates standard delivery multiple certificate cost correctly")
    void calculatesStandardMultipleCertificateCostCorrectly() {

        // Given and when
        final List<ItemCosts> costs = calculatorUnderTest.calculateCosts(3, DeliveryTimescale.STANDARD);
        final ItemCosts cost = costs.get(0);

        // Then
        assertThat(cost.getIndividualItemCost(), is(STANDARD_INDIVIDUAL_CERTIFICATE_COST));

        final int expectedDiscountApplied = (3 - 1) * STANDARD_EXTRA_CERTIFICATE_DISCOUNT;
        assertThat(cost.getDiscountApplied(), is(Integer.toString(expectedDiscountApplied)));

        assertThat(cost.getPostageCost(), is(POSTAGE_COST));

        final String expectedTotalCost =
                Integer.toString(3 * Integer.parseInt(STANDARD_INDIVIDUAL_CERTIFICATE_COST) - expectedDiscountApplied);
        assertThat(cost.getTotalCost(), is(expectedTotalCost));

    }

    @Test
    @DisplayName("Calculates same day delivery single certificate cost correctly")
    void calculatesSameDaySingleCertificateCostCorrectly() {

        // Given and when
        final List<ItemCosts> costs = calculatorUnderTest.calculateCosts(1, DeliveryTimescale.SAME_DAY);
        final ItemCosts cost = costs.get(0);

        // Then
        assertThat(cost.getIndividualItemCost(), is(SAME_DAY_INDIVIDUAL_CERTIFICATE_COST));
        assertThat(cost.getDiscountApplied(), is(NO_DISCOUNT));
        assertThat(cost.getPostageCost(), is(POSTAGE_COST));
        assertThat(cost.getTotalCost(), is(SAME_DAY_INDIVIDUAL_CERTIFICATE_COST));

    }

    @Test
    @DisplayName("Calculates same day delivery multiple certificate cost correctly")
    void calculatesSameDayMultipleCertificateCostCorrectly() {

        // Given and when
        final List<ItemCosts> costs = calculatorUnderTest.calculateCosts(3, DeliveryTimescale.SAME_DAY);
        final ItemCosts cost = costs.get(0);

        // Then
        assertThat(cost.getIndividualItemCost(), is(SAME_DAY_INDIVIDUAL_CERTIFICATE_COST));

        final int expectedDiscountApplied = (3 - 1) * SAME_DAY_EXTRA_CERTIFICATE_DISCOUNT;
        assertThat(cost.getDiscountApplied(), is(Integer.toString(expectedDiscountApplied)));

        assertThat(cost.getPostageCost(), is(POSTAGE_COST));

        final String expectedTotalCost =
                Integer.toString(3 * Integer.parseInt(SAME_DAY_INDIVIDUAL_CERTIFICATE_COST) - expectedDiscountApplied);
        assertThat(cost.getTotalCost(), is(expectedTotalCost));

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

}
