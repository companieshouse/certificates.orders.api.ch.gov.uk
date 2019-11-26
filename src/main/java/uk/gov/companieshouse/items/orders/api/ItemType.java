package uk.gov.companieshouse.items.orders.api;

import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;

/**
 * Instances of this represent the type of the item handled by each API.
 */
public enum ItemType {

    CERTIFICATE("certificate", "certificate"),
    CERTIFIED_COPY("certified-copy", "certified-copy"),
    SCAN_ON_DEMAND("scan-on-demand", "scan-on-demand");

    private String itemType;
    private String kind;

    ItemType(String itemType, String kind) {
        this.itemType = itemType;
        this.kind = kind;
    }

    /**
     * Populates the description fields to facilitate UI text rendering.
     * @param certificateItemDTO the DTO bearing text for Ui rendering
     */
    public void populateDescriptionFields(final CertificateItemDTO certificateItemDTO) {
        certificateItemDTO.setDescriptionIdentifier(itemType);
        certificateItemDTO.setKind(kind);
        // TODO Populate description and description values when we know what these are.
    }
}
