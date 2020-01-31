package uk.gov.companieshouse.items.orders.api.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.Gson;

/**
 * An instance of this represents the item options for a certificate item.
 */
@JsonPropertyOrder(alphabetic = true)
public class CertificateItemOptions {

    private CertificateType certificateType;

    private DeliveryTimescale deliveryTimescale;

    public CertificateType getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
    }

    public DeliveryTimescale getDeliveryTimescale() {
        return deliveryTimescale;
    }

    public void setDeliveryTimescale(DeliveryTimescale deliveryTimescale) {
        this.deliveryTimescale = deliveryTimescale;
    }

    @Override
    public String toString() { return new Gson().toJson(this); }

}
