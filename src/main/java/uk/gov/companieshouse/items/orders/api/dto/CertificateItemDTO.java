package uk.gov.companieshouse.items.orders.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.Gson;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * An instance of this represents the JSON serializable certificate item for use in REST requests and responses.
 */
@JsonPropertyOrder(alphabetic = true)
public class CertificateItemDTO extends ItemDTO {

    @NotNull
    @JsonProperty("item_options")
    private CertificateItemOptions itemOptions;

    @NotNull
    @JsonProperty("company_number")
    private String companyNumber;

    public CertificateItemOptions getItemOptions() {
        return itemOptions;
    }

    public void setItemOptions(CertificateItemOptions itemOptions) {
        this.itemOptions = itemOptions;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    @Override
    public String toString() { return new Gson().toJson(this); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CertificateItemDTO)) return false;
        if (!super.equals(o)) return false;
        CertificateItemDTO that = (CertificateItemDTO) o;
        return Objects.equals(itemOptions, that.itemOptions) &&
                Objects.equals(companyNumber, that.companyNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemOptions, companyNumber);
    }
}
