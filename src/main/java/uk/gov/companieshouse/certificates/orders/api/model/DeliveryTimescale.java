package uk.gov.companieshouse.certificates.orders.api.model;

import com.fasterxml.jackson.annotation.JsonValue;
import uk.gov.companieshouse.certificates.orders.api.config.CostsConfig;

import static uk.gov.companieshouse.certificates.orders.api.converter.EnumValueNameConverter.convertEnumValueNameToJson;
import static uk.gov.companieshouse.certificates.orders.api.model.ProductType.CERTIFICATE;
import static uk.gov.companieshouse.certificates.orders.api.model.ProductType.CERTIFICATE_ADDITIONAL_COPY;
import static uk.gov.companieshouse.certificates.orders.api.model.ProductType.CERTIFICATE_SAME_DAY;

/**
 * Values of this represent the possible delivery timescales.
 */
public enum DeliveryTimescale {
    STANDARD,
    SAME_DAY {

        @Override
        public int getIndividualCertificateCost(final CostsConfig costs) {
            return costs.getSameDayCost();
        }

        @Override
        public int getExtraCertificateDiscount(final CostsConfig costs) {
            return costs.getSameDayDiscount();
        }

        @Override
        public ProductType getFirstCertificateProductType() {
            return CERTIFICATE_SAME_DAY;
        }
    };

    @JsonValue
    public String getJsonName() {
        return convertEnumValueNameToJson(this);
    }

    public int getIndividualCertificateCost(final CostsConfig costs) {
        return costs.getStandardCost();
    }

    public int getExtraCertificateDiscount(final CostsConfig costs) {
        return costs.getStandardDiscount();
    }

    public ProductType getFirstCertificateProductType() {
        return CERTIFICATE;
    }

    public ProductType getAdditionalCertificatesProductType() {
        return CERTIFICATE_ADDITIONAL_COPY;
    }
}
