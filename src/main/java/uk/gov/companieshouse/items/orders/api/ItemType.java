package uk.gov.companieshouse.items.orders.api;

import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;

/**
 * Instances of this represent the type of the item handled by each API.
 */
public enum ItemType {

    CERTIFICATE("certificate", "certificate"),
    CERTIFIED_COPY("certified-copy", "certified-copy"),
    SCAN_ON_DEMAND("scan-on-demand", "scan-on-demand") {
        @Override
        public void populatePostalDelivery(CertificateItemDTO certificateItemDTO) {
            certificateItemDTO.setPostalDelivery(false);
        }
    };

    private String itemType;
    private String kind;

    ItemType(String itemType, String kind) {
        this.itemType = itemType;
        this.kind = kind;
    }

    /**
     * Populates the read only fields of the item DTO provided.
     * @param certificateItemDTO the DTO with read only fields
     */
    public void populateReadOnlyFields(final CertificateItemDTO certificateItemDTO) {
        populateDescriptionFields(certificateItemDTO);
        populateItemCosts(certificateItemDTO);
        populatePostalDelivery(certificateItemDTO);
    }

    /**
     * Populates the description fields to facilitate UI text rendering.
     * @param certificateItemDTO the DTO bearing text for UI rendering
     */
    void populateDescriptionFields(final CertificateItemDTO certificateItemDTO) {
        certificateItemDTO.setDescriptionIdentifier(itemType);
        certificateItemDTO.setKind(kind);
        // TODO Populate description and description values when we know what these are.
    }

    /**
     * Populates the item costs fields.
     * @param certificateItemDTO the DTO bearing the item costs
     */
    void populateItemCosts(final CertificateItemDTO certificateItemDTO) {
        final ItemCosts costs = new ItemCosts();

        // TODO Retrieve the actual costs as appropriate.
        costs.setDiscountApplied("1");
        costs.setIndividualItemCost("2");
        costs.setPostageCost("3");
        costs.setTotalCost("4");

        certificateItemDTO.setItemCosts(costs);
    }

    /**
     * Populates the postal delivery field as appropriate for this type.
     * @param certificateItemDTO the DTO
     */
    void populatePostalDelivery(final CertificateItemDTO certificateItemDTO) {
        certificateItemDTO.setPostalDelivery(true);
    }
}
