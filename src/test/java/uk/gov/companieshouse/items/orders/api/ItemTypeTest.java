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
class ItemTypeTest {

    @Test
    @DisplayName("Certificate item populated correctly")
    void certificateItemPopulatedCorrectly() {
        itemPopulatedCorrrectly(CERTIFICATE, "certificate");
    }

    @Test
    @DisplayName("Certified copy item populated correctly")
    void certifiedCopyItemPopulatedCorrectly() {
        itemPopulatedCorrrectly(CERTIFIED_COPY, "certified-copy");
    }

    @Test
    @DisplayName("Scan on demand item populated correctly")
    void scanOnDemandItemPopulatedCorrectly() {
        itemPopulatedCorrrectly(SCAN_ON_DEMAND, "scan-on-demand");
    }

    /**
     * Utility method that calls {@link ItemType#populateReadOnlyFields(Item)} and verifies
     * the impact on the item is that expected.
     * @param type the {@link ItemType}
     * @param expectedDescriptionFieldsValue the expected description field values
     */
    private void itemPopulatedCorrrectly(final ItemType type, final String expectedDescriptionFieldsValue) {
        // Given
        final Item item = new Item();

        // When
        type.populateReadOnlyFields(item);

        // Then
        verifyDescriptionFields(item, expectedDescriptionFieldsValue);
        verifyCostsFields(item);
        verifyPostalDelivery(item, type);
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
