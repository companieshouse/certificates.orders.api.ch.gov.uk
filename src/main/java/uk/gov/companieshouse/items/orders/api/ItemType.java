package uk.gov.companieshouse.items.orders.api;

import uk.gov.companieshouse.items.orders.api.dto.ItemDTO;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;

/**
 * Instances of this represent the type of the item handled by each API.
 */
public enum ItemType {

    CERTIFICATE("certificate", "certificate"),
    CERTIFIED_COPY("certified-copy", "certified-copy"),
    SCAN_ON_DEMAND("scan-on-demand", "scan-on-demand") {
        @Override
        public void populatePostalDelivery(final ItemDTO itemDTO) {
            itemDTO.setPostalDelivery(false);
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
     * @param itemDTO the DTO with read only fields
     */
    public void populateReadOnlyFields(final ItemDTO itemDTO) {
        populateDescriptionFields(itemDTO);
        populateItemCosts(itemDTO);
        populatePostalDelivery(itemDTO);
    }

    /**
     * Populates the description fields to facilitate UI text rendering.
     * @param itemDTO the DTO bearing text for UI rendering
     */
    void populateDescriptionFields(final ItemDTO itemDTO) {
        itemDTO.setDescriptionIdentifier(itemType);
        itemDTO.setKind(kind);
        // TODO PCI-505 Populate description and description values when we know what these are.
    }

    /**
     * Populates the item costs fields.
     * @param itemDTO the DTO bearing the item costs
     */
    void populateItemCosts(final ItemDTO itemDTO) {
        final ItemCosts costs = new ItemCosts();

        // TODO PCI-506 Retrieve the actual costs as appropriate.
        costs.setDiscountApplied("1");
        costs.setIndividualItemCost("2");
        costs.setPostageCost("3");
        costs.setTotalCost("4");

        itemDTO.setItemCosts(costs);
    }

    /**
     * Populates the postal delivery field as appropriate for this type.
     * @param itemDTO the DTO
     */
    void populatePostalDelivery(final ItemDTO itemDTO) {
        itemDTO.setPostalDelivery(true);
    }
}
