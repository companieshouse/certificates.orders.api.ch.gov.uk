package uk.gov.companieshouse.certificates.orders.api.model;

import com.fasterxml.jackson.annotation.JsonValue;

import static uk.gov.companieshouse.certificates.orders.api.converter.EnumValueNameConverter.convertEnumValueNameToJson;

public enum IncludeAddressRecordsType {
    CURRENT,
    CURRENT_AND_PREVIOUS,
    CURRENT_PREVIOUS_AND_PRIOR,
    ALL;

    @JsonValue
    public String getJsonName() {
        return convertEnumValueNameToJson(this);
    }
}
