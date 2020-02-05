package uk.gov.companieshouse.items.orders.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.items.orders.api.model.Links;

/**
 * Service that generates the links for the item ID specified.
 */
@Service
public class LinksGeneratorService {

    @Value("${uk.gov.companieshouse.items.orders.api.certificates}")
    private String pathToSelf;

    /**
     * Generates the links for the item identified.
     * @param itemId the ID for the item
     * @return the appropriate {@link Links}
     */
    public Links generateLinks(final String itemId) {
        final Links links = new Links();
        links.setSelf(pathToSelf + "/" + itemId);
        return links;
    }

}
