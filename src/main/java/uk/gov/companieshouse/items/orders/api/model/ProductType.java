package uk.gov.companieshouse.items.orders.api.model;

import com.fasterxml.jackson.annotation.JsonValue;

import static uk.gov.companieshouse.items.orders.api.converter.EnumValueNameConverter.convertEnumValueNameToJson;

/**
 * Values of this represent the possible product types.
 */
public enum ProductType {
    CERTIFICATE,
    CERTIFICATE_SAME_DAY,
    CERTIFICATE_ADDITIONAL_COPY,
    SCAN_UPON_DEMAND,
    CERTIFIED_COPY;

    @JsonValue
    public String getJsonName() {
        return convertEnumValueNameToJson(this);
    }
}
