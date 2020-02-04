package uk.gov.companieshouse.items.orders.api.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DeliveryMethod {
    POSTAL,
    COLLECTION;

    @JsonValue
    public String getJsonName() {
        return name().toLowerCase();
    }
}
