package uk.gov.companieshouse.certificates.orders.api.model;

import com.google.gson.Gson;

import java.util.Objects;

/**
 * An instance of this represents the principal place of business details item options selected.
 */
public class PrincipalPlaceOfBusinessDetails {

    private IncludeAddressRecordsType includeAddressRecordsType;
    private Boolean includeDates;

    public IncludeAddressRecordsType getIncludeAddressRecordsType() {
        return includeAddressRecordsType;
    }

    public void setIncludeAddressRecordsType(IncludeAddressRecordsType includeAddressRecordsType) {
        this.includeAddressRecordsType = includeAddressRecordsType;
    }

    public Boolean getIncludeDates() {
        return includeDates;
    }

    public void setIncludeDates(Boolean includeDates) {
        this.includeDates = includeDates;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PrincipalPlaceOfBusinessDetails that = (PrincipalPlaceOfBusinessDetails) o;
        return includeAddressRecordsType == that.includeAddressRecordsType && Objects.equals(includeDates, that.includeDates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(includeAddressRecordsType, includeDates);
    }
}
