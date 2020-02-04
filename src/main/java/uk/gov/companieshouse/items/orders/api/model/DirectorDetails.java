package uk.gov.companieshouse.items.orders.api.model;

import com.google.gson.Gson;

import java.util.Objects;

/**
 * An instance of this represents the director details item options selected.
 */
public class DirectorDetails {

    private Boolean includeAddress;
    private Boolean includeAppointmentDate;
    private Boolean includeBasicInformation;
    private Boolean includeCountryOfResidence;
    private IncludeDobType includeDobType;
    private Boolean includeNationality;
    private Boolean includeOccupation;

    public Boolean getIncludeAddress() {
        return includeAddress;
    }

    public void setIncludeAddress(Boolean includeAddress) {
        this.includeAddress = includeAddress;
    }

    public Boolean getIncludeAppointmentDate() {
        return includeAppointmentDate;
    }

    public void setIncludeAppointmentDate(Boolean includeAppointmentDate) {
        this.includeAppointmentDate = includeAppointmentDate;
    }

    public Boolean getIncludeBasicInformation() {
        return includeBasicInformation;
    }

    public void setIncludeBasicInformation(Boolean includeBasicInformation) {
        this.includeBasicInformation = includeBasicInformation;
    }

    public Boolean getIncludeCountryOfResidence() {
        return includeCountryOfResidence;
    }

    public void setIncludeCountryOfResidence(Boolean includeCountryOfResidence) {
        this.includeCountryOfResidence = includeCountryOfResidence;
    }

    public IncludeDobType getIncludeDobType() {
        return includeDobType;
    }

    public void setIncludeDobType(IncludeDobType includeDobType) {
        this.includeDobType = includeDobType;
    }

    public Boolean getIncludeNationality() {
        return includeNationality;
    }

    public void setIncludeNationality(Boolean includeNationality) {
        this.includeNationality = includeNationality;
    }

    public Boolean getIncludeOccupation() {
        return includeOccupation;
    }

    public void setIncludeOccupation(Boolean includeOccupation) {
        this.includeOccupation = includeOccupation;
    }

    @Override
    public String toString() { return new Gson().toJson(this); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DirectorDetails)) return false;
        DirectorDetails that = (DirectorDetails) o;
        return Objects.equals(includeAddress, that.includeAddress) &&
                Objects.equals(includeAppointmentDate, that.includeAppointmentDate) &&
                Objects.equals(includeBasicInformation, that.includeBasicInformation) &&
                Objects.equals(includeCountryOfResidence, that.includeCountryOfResidence) &&
                Objects.equals(includeDobType, that.includeDobType) &&
                Objects.equals(includeNationality, that.includeNationality) &&
                Objects.equals(includeOccupation, that.includeOccupation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(includeAddress, includeAppointmentDate, includeBasicInformation, includeCountryOfResidence, includeDobType, includeNationality, includeOccupation);
    }
}
