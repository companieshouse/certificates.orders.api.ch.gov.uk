package uk.gov.companieshouse.certificates.orders.api.model;

import com.google.gson.Gson;
import org.springframework.data.annotation.Transient;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An instance of this represents the director or secretary details item options selected.
 */
public class DirectorOrSecretaryDetails implements DateOfBirthIncludable<Map<String, Object>> {

    private Boolean includeAddress;
    private Boolean includeAppointmentDate;
    private Boolean includeBasicInformation;
    private Boolean includeCountryOfResidence;
    private IncludeDobType includeDobType;
    private Boolean includeNationality;
    private Boolean includeOccupation;
    @Transient
    private final Map<String, Object> fieldValues = new HashMap<>();

    public Boolean getIncludeAddress() {
        return includeAddress;
    }

    public void setIncludeAddress(Boolean includeAddress) {
        this.includeAddress = includeAddress;
        fieldValues.put("include_address", includeAddress);
    }

    public Boolean getIncludeAppointmentDate() {
        return includeAppointmentDate;
    }

    public void setIncludeAppointmentDate(Boolean includeAppointmentDate) {
        this.includeAppointmentDate = includeAppointmentDate;
        fieldValues.put("include_appointment_date", includeAppointmentDate);
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
        fieldValues.put("include_country_of_residence", includeCountryOfResidence);
    }

    public IncludeDobType getIncludeDobType() {
        return includeDobType;
    }

    public void setIncludeDobType(IncludeDobType includeDobType) {
        this.includeDobType = includeDobType;
        fieldValues.put("include_dob_type", includeDobType);
    }

    public Boolean getIncludeNationality() {
        return includeNationality;
    }

    public void setIncludeNationality(Boolean includeNationality) {
        this.includeNationality = includeNationality;
        fieldValues.put("include_nationality", includeNationality);
    }

    public Boolean getIncludeOccupation() {
        return includeOccupation;
    }

    public void setIncludeOccupation(Boolean includeOccupation) {
        this.includeOccupation = includeOccupation;
        fieldValues.put("include_occupation", includeOccupation);
    }

    @Override
    public String toString() { return new Gson().toJson(this); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DirectorOrSecretaryDetails that)) return false;
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

    @Override
    public void accept(Visitor<Map<String, Object>> visitor) {
        visitor.visit(fieldValues);
    }
}
