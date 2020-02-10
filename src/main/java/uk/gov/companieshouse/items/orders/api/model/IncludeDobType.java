package uk.gov.companieshouse.items.orders.api.model;

import com.fasterxml.jackson.annotation.JsonValue;

import static uk.gov.companieshouse.items.orders.api.converter.EnumValueNameConverter.convertEnumValueNameToJson;

public enum IncludeDobType {
    PARTIAL,
    FULL;

    @JsonValue
    public String getJsonName() {
        return convertEnumValueNameToJson(this);
    }
}
