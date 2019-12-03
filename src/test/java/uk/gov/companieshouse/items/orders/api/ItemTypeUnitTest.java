package uk.gov.companieshouse.items.orders.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.items.orders.api.model.Item;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static uk.gov.companieshouse.items.orders.api.ItemType.*;

/**
 * Unit tests the {@link ItemType} enum.
 */
class ItemTypeUnitTest {

    @Test
    @DisplayName("Certificate item populated correctly")
    void certificateItemPopulatedCorrectly() {

        // Given
        final Item item = new Item();

        // When
        CERTIFICATE.populateReadOnlyFields(item);

        // Then
        verifyDescriptionFields(item, "certificate");
        verifyCostsFields(item);
        verifyPostalDelivery(item, CERTIFICATE);
    }

    @Test
    @DisplayName("Certified copy item populated correctly")
    void certifiedCopyItemPopulatedCorrectly() {

        // Given
        final Item item = new Item();

        // When
        CERTIFIED_COPY.populateReadOnlyFields(item);

        // Then
        verifyDescriptionFields(item, "certified-copy");
        verifyCostsFields(item);
        verifyPostalDelivery(item, CERTIFIED_COPY);
    }

    @Test
    @DisplayName("Scan on demand item populated correctly")
    void scanOnDemandItemPopulatedCorrectly() {

        // Given
        final Item item = new Item();

        // When
        SCAN_ON_DEMAND.populateReadOnlyFields(item);

        // Then
        verifyDescriptionFields(item, "scan-on-demand");
        verifyCostsFields(item);
        verifyPostalDelivery(item, SCAN_ON_DEMAND);
    }

    private void verifyDescriptionFields(final Item item, final String value) {
        assertThat(item.getDescriptionIdentifier(), is(value));
        assertThat(item.getKind(), is(value));
    }

    private void verifyCostsFields(final Item item) {
        final ItemCosts costs = item.getItemCosts();
        assertThat(costs, is(notNullValue()));
        assertThat(costs.getDiscountApplied(), is("1"));
        assertThat(costs.getIndividualItemCost(), is("2"));
        assertThat(costs.getPostageCost(), is("3"));
        assertThat(costs.getTotalCost(), is("4"));
    }

    private void verifyPostalDelivery(final Item item, final ItemType type) {
        assertThat(item.isPostalDelivery(), is(type != SCAN_ON_DEMAND));
    }
}
