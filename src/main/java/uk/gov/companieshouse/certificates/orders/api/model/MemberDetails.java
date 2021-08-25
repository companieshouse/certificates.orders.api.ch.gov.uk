package uk.gov.companieshouse.certificates.orders.api.model;


import com.google.gson.Gson;

import java.util.Objects;

/**
 * An instance of this represents the member details item options selected.
 */
public class MemberDetails implements BasicInformationIncludable, DateOfBirthIncludable {

    private Boolean includeAddress;
    private Boolean includeAppointmentDate;
    private Boolean includeBasicInformation;
    private Boolean includeCountryOfResidence;
    private IncludeDobType includeDobType;

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

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberDetails that = (MemberDetails) o;
        return Objects.equals(includeAddress, that.includeAddress) && Objects.equals(includeAppointmentDate, that.includeAppointmentDate) && Objects.equals(includeBasicInformation, that.includeBasicInformation) && Objects.equals(includeCountryOfResidence, that.includeCountryOfResidence) && includeDobType == that.includeDobType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(includeAddress, includeAppointmentDate, includeBasicInformation, includeCountryOfResidence, includeDobType);
    }
}
