package uk.gov.companieshouse.items.orders.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
        final CertificateItemDTO certificateItemDTO = new CertificateItemDTO();

        // When
        CERTIFICATE.populateReadOnlyFields(certificateItemDTO);

        // Then
        verifyDescriptionFields(certificateItemDTO, "certificate");
        verifyCostsFields(certificateItemDTO);
        verifyPostalDelivery(certificateItemDTO, CERTIFICATE);
    }

    @Test
    @DisplayName("Certified copy item populated correctly")
    void certifiedCopyItemPopulatedCorrectly() {

        // Given
        final CertificateItemDTO certificateItemDTO = new CertificateItemDTO();

        // When
        CERTIFIED_COPY.populateReadOnlyFields(certificateItemDTO);

        // Then
        verifyDescriptionFields(certificateItemDTO, "certified-copy");
        verifyCostsFields(certificateItemDTO);
        verifyPostalDelivery(certificateItemDTO, CERTIFIED_COPY);
    }

    @Test
    @DisplayName("Scan on demand item populated correctly")
    void scanOnDemandItemPopulatedCorrectly() {

        // Given
        final CertificateItemDTO certificateItemDTO = new CertificateItemDTO();

        // When
        SCAN_ON_DEMAND.populateReadOnlyFields(certificateItemDTO);

        // Then
        verifyDescriptionFields(certificateItemDTO, "scan-on-demand");
        verifyCostsFields(certificateItemDTO);
        verifyPostalDelivery(certificateItemDTO, SCAN_ON_DEMAND);
    }

    private void verifyDescriptionFields(final CertificateItemDTO certificateItemDTO, final String value) {
        assertThat(certificateItemDTO.getDescriptionIdentifier(), is(value));
        assertThat(certificateItemDTO.getKind(), is(value));
    }

    private void verifyCostsFields(final CertificateItemDTO certificateItemDTO) {
        final ItemCosts costs = certificateItemDTO.getItemCosts();
        assertThat(costs, is(notNullValue()));
        assertThat(costs.getDiscountApplied(), is("1"));
        assertThat(costs.getIndividualItemCost(), is("2"));
        assertThat(costs.getPostageCost(), is("3"));
        assertThat(costs.getTotalCost(), is("4"));
    }

    private void verifyPostalDelivery(final CertificateItemDTO certificateItemDTO, final ItemType type) {
        assertThat(certificateItemDTO.isPostalDelivery(), is(type != SCAN_ON_DEMAND));
    }
}
