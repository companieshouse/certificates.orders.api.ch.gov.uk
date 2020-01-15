package uk.gov.companieshouse.items.orders.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Values of this represent the possible delivery timescales.
 */
public enum DeliveryTimescale {
    STANDARD,
    SAME_DAY;

    @JsonCreator
    public static DeliveryTimescale fromString(String raw) {
        return DeliveryTimescale.valueOf(raw.toUpperCase());
    }
}
