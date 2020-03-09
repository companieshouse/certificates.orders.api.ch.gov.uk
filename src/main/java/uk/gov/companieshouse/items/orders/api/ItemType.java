package uk.gov.companieshouse.items.orders.api;

import uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale;
import uk.gov.companieshouse.items.orders.api.model.Item;
import uk.gov.companieshouse.items.orders.api.service.CertificateCostCalculatorService;
import uk.gov.companieshouse.items.orders.api.service.DescriptionProviderService;

import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.STANDARD;

/**
 * Instances of this represent the type of the item handled by each API.
 */
public enum ItemType {

    CERTIFICATE("certificate", "item#certificate"),
    CERTIFIED_COPY("certified-copy", "item#certified-copy"),
    SCAN_ON_DEMAND("scan-on-demand", "item#scan-on-demand") {
        @Override
        public void populatePostalDelivery(final Item item) {
            item.setPostalDelivery(false);
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
     * @param item the item with read only fields
     * @param descriptions the description string resources provider
     */
    public void populateReadOnlyFields(final Item item,
                                       final DescriptionProviderService descriptions) {
        populateDescriptionFields(item, descriptions);
        populatePostalDelivery(item);
    }

    /**
     * Populates those description fields that must be derived using the latest item field values.
     * @param item the item with read only fields
     * @param descriptions the description string resources provider
     */
    public void populateDerivedDescriptionFields(final Item item, final DescriptionProviderService descriptions) {
        item.setDescription(descriptions.getDescription(item.getCompanyNumber()));
        item.setDescriptionValues(descriptions.getDescriptionValues(item.getCompanyNumber()));
    }

    /**
     * Populates the description fields to facilitate UI text rendering.
     * @param item the item bearing text for UI rendering
     * @param descriptions the description string resources provider
     */
    void populateDescriptionFields(final Item item, final DescriptionProviderService descriptions) {
        item.setDescriptionIdentifier(itemType);
        item.setKind(kind);
        populateDerivedDescriptionFields(item, descriptions);
    }

    /**
     * Populates the item costs fields.
     * @param item the item bearing the item costs
     * @param calculator the item costs calculator
     */
    public void populateItemCosts(final Item item, final CertificateCostCalculatorService calculator) {
        calculator.calculateCosts(item, getOrDefaultDeliveryTimescale(item));
    }

    /**
     * Populates the postal delivery field as appropriate for this type.
     * @param item the item
     */
    void populatePostalDelivery(final Item item) {
        item.setPostalDelivery(true);
    }

    /**
     * Defaults the delivery timescale to {@link DeliveryTimescale#STANDARD} if not specified.
     * @param item the item
     * @return the obtained or defaulted delivery timescale
     */
    DeliveryTimescale getOrDefaultDeliveryTimescale(final Item item) {
        return item.getItemOptions() != null &&
               item.getItemOptions().getDeliveryTimescale() != null ?
                item.getItemOptions().getDeliveryTimescale() :
                STANDARD;
    }
}
