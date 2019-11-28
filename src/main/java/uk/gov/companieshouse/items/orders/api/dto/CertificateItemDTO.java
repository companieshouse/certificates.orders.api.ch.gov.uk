package uk.gov.companieshouse.items.orders.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;

/**
 * An instance of this represents the JSON serializable certificate item for use in REST requests and responses.
 */
public class CertificateItemDTO extends ItemDTO {

    @JsonProperty("item_options")
    private CertificateItemOptions itemOptions;

    public CertificateItemOptions getItemOptions() {
        return itemOptions;
    }

    public void setItemOptions(CertificateItemOptions itemOptions) {
        this.itemOptions = itemOptions;
    }

    @Override
    public String toString() {
        return "CertificateItemDTO{" +
                "itemOptions=" + itemOptions +
                "} [" + super.toString() + "]";
    }
}
