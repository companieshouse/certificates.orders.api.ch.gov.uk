package uk.gov.companieshouse.items.orders.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class CertificateItemDTO {

    private String id;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("description")
    private String description;

    @JsonProperty("description_identifier")
    private String descriptionIdentifier;

    @JsonProperty("description_values")
    private Map<String, String> descriptionValues;

    private String kind;

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionIdentifier() {
        return descriptionIdentifier;
    }

    public void setDescriptionIdentifier(String descriptionIdentifier) {
        this.descriptionIdentifier = descriptionIdentifier;
    }

    public Map<String, String> getDescriptionValues() {
        return descriptionValues;
    }

    public void setDescriptionValues(Map<String, String> descriptionValues) {
        this.descriptionValues = descriptionValues;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    @Override
    public String toString() {
        return "CertificateItemDTO{" +
                "id='" + id + '\'' +
                ", companyNumber='" + companyNumber + '\'' +
                ", description='" + description + '\'' +
                ", descriptionIdentifier='" + descriptionIdentifier + '\'' +
                ", descriptionValues=" + descriptionValues +
                ", kind='" + kind + '\'' +
                '}';
    }
}
