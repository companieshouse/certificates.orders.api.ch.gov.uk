package uk.gov.companieshouse.items.orders.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.items.orders.api.model.Item;
import uk.gov.companieshouse.items.orders.api.service.CertificateCostCalculatorService;
import uk.gov.companieshouse.items.orders.api.service.DescriptionProviderService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static uk.gov.companieshouse.items.orders.api.ItemType.*;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.SAME_DAY;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.STANDARD;

/**
 * Unit tests the {@link ItemType} enum.
 */
@ExtendWith(MockitoExtension.class)
class ItemTypeTest {

    @Mock
    private DescriptionProviderService descriptions;

    @Mock
    private CertificateCostCalculatorService calculator;

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

    @Test
    @DisplayName("Delivery timescale is defaulted correctly")
    void deliveryTimescaleDefaultedCorrectly() {
        final Item item = new Item();
        assertThat(CERTIFICATE.getOrDefaultDeliveryTimescale(item), is(STANDARD));
        final CertificateItemOptions options = new CertificateItemOptions();
        item.setItemOptions(options);
        assertThat(CERTIFICATE.getOrDefaultDeliveryTimescale(item), is(STANDARD));
        options.setDeliveryTimescale(STANDARD);
        assertThat(CERTIFICATE.getOrDefaultDeliveryTimescale(item), is(STANDARD));
        options.setDeliveryTimescale(SAME_DAY);
        assertThat(CERTIFICATE.getOrDefaultDeliveryTimescale(item), is(SAME_DAY));
    }

    @Test
    @DisplayName("Calculated costs are populated correctly")
    void itemCostsArePopulatedCorrectly() {
        // Given
        final Item item = new Item();

        // When
        CERTIFICATE.populateItemCosts(item, calculator);

        // Then
        verify(calculator).calculateCosts(item, STANDARD);
    }

    /**
     * Utility method that calls
     * {@link ItemType#populateReadOnlyFields(Item, DescriptionProviderService)} and
     * verifies the impact on the item is that expected.
     * @param type the {@link ItemType}
     * @param expectedDescriptionFieldsValue the expected description field values
     */
    private void itemPopulatedCorrectly(final ItemType type, final String expectedDescriptionFieldsValue) {
        // Given
        final Item item = new Item();
        item.setQuantity(1);

        // When
        type.populateReadOnlyFields(item, descriptions);

        // Then
        verifyDescriptionFields(item, expectedDescriptionFieldsValue);
        verifyPostalDelivery(item, type);
    }

    private void verifyDescriptionFields(final Item item, final String value) {
        assertThat("Description identifier not expected value!", item.getDescriptionIdentifier(), is(value));
        assertThat("Kind not expected value!", item.getKind(), is("item#" + value));
        verifyDerivedDescriptionFields(item);
    }

    private void verifyDerivedDescriptionFields(final Item item) {
        verify(descriptions).getDescription(item.getCompanyNumber());
        verify(descriptions).getDescriptionValues(item.getCompanyNumber());
    }

    private void verifyPostalDelivery(final Item item, final ItemType type) {
        assertThat(item.isPostalDelivery(), is(type != SCAN_ON_DEMAND));
    }
}
