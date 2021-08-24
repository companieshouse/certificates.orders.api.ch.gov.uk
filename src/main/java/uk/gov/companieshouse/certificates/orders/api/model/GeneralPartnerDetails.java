package uk.gov.companieshouse.certificates.orders.api.model;

import com.google.gson.Gson;

import java.util.Objects;

/**
 * An instance of this represents the general partner details item options selected.
 */
public class GeneralPartnerDetails {

    private Boolean includeBasicInformation;

    public Boolean isIncludeBasicInformation() {
        return includeBasicInformation;
    }

    public void setIncludeBasicInformation(Boolean includeBasicInformation) {
        this.includeBasicInformation = includeBasicInformation;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneralPartnerDetails that = (GeneralPartnerDetails) o;
        return Objects.equals(includeBasicInformation, that.includeBasicInformation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(includeBasicInformation);
    }
}
