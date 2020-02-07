package uk.gov.companieshouse.items.orders.api.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Values of this represent the possible delivery timescales.
 */
public enum DeliveryTimescale {
    STANDARD,
    SAME_DAY {

        @Override
        public int getIndividualCertificateCost() {
            return SAME_DAY_INDIVIDUAL_CERTIFICATE_COST;
        }

        @Override
        public int getExtraCertificateDiscount() {
            return SAME_DAY_EXTRA_CERTIFICATE_DISCOUNT;
        }
    };

    private static final int STANDARD_INDIVIDUAL_CERTIFICATE_COST = 15;
    private static final int SAME_DAY_INDIVIDUAL_CERTIFICATE_COST = 50;
    private static final int STANDARD_EXTRA_CERTIFICATE_DISCOUNT = 5;
    private static final int SAME_DAY_EXTRA_CERTIFICATE_DISCOUNT = 40;

    @JsonValue
    public String getJsonName() {
        return name().toLowerCase().replace("_", "-");
    }

    public int getIndividualCertificateCost() {
        return STANDARD_INDIVIDUAL_CERTIFICATE_COST;
    }

    public int getExtraCertificateDiscount() {
        return STANDARD_EXTRA_CERTIFICATE_DISCOUNT;
    }
}
