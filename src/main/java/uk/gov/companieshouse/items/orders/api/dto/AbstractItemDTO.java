package uk.gov.companieshouse.items.orders.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.items.orders.api.model.Links;

import javax.validation.constraints.Null;
import java.util.Map;

/**
 * Represents state common to all item DTOs.
 */
public abstract class AbstractItemDTO {

    protected String id;

    protected String description;

    protected String descriptionIdentifier;

    protected Map<String, String> descriptionValues;

    private Links links;

    public void setId(String id) {
        this.id = id;
    }

    @Null
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Null
    @JsonProperty("description_identifier")
    public String getDescriptionIdentifier() {
        return descriptionIdentifier;
    }

    public void setDescriptionIdentifier(String descriptionIdentifier) {
        this.descriptionIdentifier = descriptionIdentifier;
    }

    @Null
    @JsonProperty("description_values")
    public Map<String, String> getDescriptionValues() {
        return descriptionValues;
    }

    public void setDescriptionValues(Map<String, String> descriptionValues) {
        this.descriptionValues = descriptionValues;
    }

    @Null
    @JsonProperty("links")
    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }
}
