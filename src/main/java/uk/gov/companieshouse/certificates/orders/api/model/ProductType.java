package uk.gov.companieshouse.certificates.orders.api.model;

import com.fasterxml.jackson.annotation.JsonValue;

import static uk.gov.companieshouse.certificates.orders.api.converter.EnumValueNameConverter.convertEnumValueNameToJson;

/**
 * Values of this represent the possible product types.
 */
public enum ProductType {
    CERTIFICATE,
    CERTIFICATE_SAME_DAY,
    CERTIFICATE_ADDITIONAL_COPY,
    MISSING_IMAGE_DELIVERY_ACCOUNTS,
    MISSING_IMAGE_DELIVERY_ANNUAL_RETURN,
    MISSING_IMAGE_DELIVERY_APPOINTMENT,
    MISSING_IMAGE_DELIVERY_REGISTERED_OFFICE,
    MISSING_IMAGE_DELIVERY_MORTGAGE,
    MISSING_IMAGE_DELIVERY_LIQUIDATION,
    MISSING_IMAGE_DELIVERY_NEW_INCORPORATION,
    MISSING_IMAGE_DELIVERY_CHANGE_OF_NAME,
    MISSING_IMAGE_DELIVERY_CAPITAL,
    MISSING_IMAGE_DELIVERY_MISC,
    CERTIFIED_COPY,
    CERTIFIED_COPY_SAME_DAY;

    @JsonValue
    public String getJsonName() {
        return convertEnumValueNameToJson(this);
    }
}
