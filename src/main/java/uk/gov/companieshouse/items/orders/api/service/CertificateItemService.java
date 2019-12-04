package uk.gov.companieshouse.items.orders.api.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.model.Item;
import uk.gov.companieshouse.items.orders.api.repository.CertificateItemRepository;

import static uk.gov.companieshouse.items.orders.api.ItemType.CERTIFICATE;

/**
 * Service for the management and storage of certificate items.
 */
@Service
public class CertificateItemService {

    private final CertificateItemRepository repository;
    private final SequenceGeneratorService generator;

    public CertificateItemService(final CertificateItemRepository repository,
                                  final SequenceGeneratorService generator) {
        this.repository = repository;
        this.generator = generator;
    }

    /**
     * Creates the item in the database.
     * @param item the item to be created
     * @return the created item
     */
    public CertificateItem createCertificateItem(final CertificateItem item) {
        CERTIFICATE.populateReadOnlyFields(item);
        item.setId(getNextId());
        return repository.save(item);
    }

    /**
     * Gets the next ID value suitable for use as a 20 character PSNUMBER within CHD. All such values
     * originated from within CHS are to be prefixed 'CHS...'.
     * @return the next available ID value
     */
    String getNextId() {
        return String.format("CHS%017d", generator.generateSequence(Item.SEQUENCE_NAME));
    }

}
