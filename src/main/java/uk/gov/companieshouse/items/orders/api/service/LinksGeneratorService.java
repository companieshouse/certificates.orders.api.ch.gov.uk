package uk.gov.companieshouse.items.orders.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.items.orders.api.model.Links;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Service that generates the links for the item ID specified.
 */
@Service
public class LinksGeneratorService {

    private final String pathToSelf;

    /**
     * Constructor.
     * @param pathToSelf configured path to self URI
     */
    public LinksGeneratorService(
            final @Value("${uk.gov.companieshouse.items.orders.api.certificates}") String pathToSelf) {
        if (isBlank(pathToSelf)) {
            throw new IllegalArgumentException("Path to self URI not configured!");
        }
        this.pathToSelf = pathToSelf;
    }

    /**
     * Generates the links for the item identified.
     * @param itemId the ID for the item
     * @return the appropriate {@link Links}
     */
    public Links generateLinks(final String itemId) {
        if (isBlank(itemId)) {
            throw new IllegalArgumentException("Item ID not populated!");
        }
        final Links links = new Links();
        links.setSelf(pathToSelf + "/" + itemId);
        return links;
    }

}
