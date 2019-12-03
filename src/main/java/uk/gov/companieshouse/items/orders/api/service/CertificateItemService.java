package uk.gov.companieshouse.items.orders.api.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;

import static uk.gov.companieshouse.items.orders.api.ItemType.CERTIFICATE;

/**
 * Service for the management and storage of certificate items.
 */
@Service
public class CertificateItemService {

    /**
     * Creates the item in the database.
     * @param item the item to be created
     * @return the created item
     */
    public CertificateItem createCertificateItem(final CertificateItem item) {
        CERTIFICATE.populateReadOnlyFields(item);
        // TODO PCI-324 ID will be generated as per
        //  https://companieshouse.atlassian.net/wiki/spaces/DEV/pages/1258094916/Certificates+API+High+Level+Development+Design#CertificatesAPIHighLevelDevelopmentDesign-Traceability
        item.setId("CHS1");
        return item;
    }

}
