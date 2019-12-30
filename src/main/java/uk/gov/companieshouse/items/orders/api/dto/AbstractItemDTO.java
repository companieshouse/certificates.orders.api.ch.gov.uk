package uk.gov.companieshouse.items.orders.api.dto;

import java.util.Map;

/**
 * Represents state common to all item DTOs.
 */
public abstract class AbstractItemDTO {

    protected String id;

    protected String description;

    protected String descriptionIdentifier;

    protected Map<String, String> descriptionValues;

}
