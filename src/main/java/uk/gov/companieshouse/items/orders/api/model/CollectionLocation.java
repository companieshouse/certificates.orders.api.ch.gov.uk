package uk.gov.companieshouse.items.orders.api.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CollectionLocation {
    BELFAST,
    CARDIFF,
    EDINBURGH,
    LONDON;

    @JsonValue
    public String getJsonName() {
        return name().toLowerCase().replace("_", "-");
    }
}
