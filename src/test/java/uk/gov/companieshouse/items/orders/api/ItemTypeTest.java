package uk.gov.companieshouse.items.orders.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.items.orders.api.model.Item;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;
import uk.gov.companieshouse.items.orders.api.service.DescriptionProviderService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static uk.gov.companieshouse.items.orders.api.ItemType.*;

import static org.mockito.Mockito.verify;

/**
 * Unit tests the {@link ItemType} enum.
 */
@ExtendWith(MockitoExtension.class)
class ItemTypeTest {

    @Mock
    private DescriptionProviderService descriptions;

    @Test
    @DisplayName("Certificate item populated correctly")
    void certificateItemPopulatedCorrectly() {
        itemPopulatedCorrectly(CERTIFICATE, "certificate");
    }

    @Test
    @DisplayName("Certified copy item populated correctly")
    void certifiedCopyItemPopulatedCorrectly() {
        itemPopulatedCorrectly(CERTIFIED_COPY, "certified-copy");
    }

    @Test
    @DisplayName("Scan on demand item populated correctly")
    void scanOnDemandItemPopulatedCorrectly() {
        itemPopulatedCorrectly(SCAN_ON_DEMAND, "scan-on-demand");
    }

    @Test
    @DisplayName("Derived description fields are populated correctly")
    void derivedDescriptionFieldsPopulatedCorrectly() {
        // Given
        final Item item = new Item();

        // When
        CERTIFICATE.populateDerivedDescriptionFields(item, descriptions);

        verifyDerivedDescriptionFields(item);
    }

    /**
     * Utility method that calls {@link ItemType#populateReadOnlyFields(Item, DescriptionProviderService)} and verifies
     * the impact on the item is that expected.
     * @param type the {@link ItemType}
     * @param expectedDescriptionFieldsValue the expected description field values
     */
    private void itemPopulatedCorrectly(final ItemType type, final String expectedDescriptionFieldsValue) {
        // Given
        final Item item = new Item();

        // When
        type.populateReadOnlyFields(item, descriptions);

        // Then
        verifyDescriptionFields(item, expectedDescriptionFieldsValue);
        verifyCostsFields(item);
        verifyPostalDelivery(item, type);
    }

    private void verifyDescriptionFields(final Item item, final String value) {
        assertThat(item.getDescriptionIdentifier(), is(value));
        assertThat(item.getKind(), is(value));
        verifyDerivedDescriptionFields(item);
    }

    private void verifyDerivedDescriptionFields(final Item item) {
        verify(descriptions).getDescription(item.getCompanyNumber());
        verify(descriptions).getDescriptionValues(item.getCompanyNumber());
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
