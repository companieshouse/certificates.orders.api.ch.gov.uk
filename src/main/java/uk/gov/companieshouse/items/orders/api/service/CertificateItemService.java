package uk.gov.companieshouse.items.orders.api.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.model.Item;
import uk.gov.companieshouse.items.orders.api.repository.CertificateItemRepository;
import uk.gov.companieshouse.items.orders.api.util.NonNullPropertyCopier;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

import static uk.gov.companieshouse.items.orders.api.ItemType.CERTIFICATE;

/**
 * Service for the management and storage of certificate items.
 */
@Service
public class CertificateItemService {

    private final CertificateItemRepository repository;
    private final SequenceGeneratorService generator;
    private final NonNullPropertyCopier copier;

    public CertificateItemService(final CertificateItemRepository repository,
                                  final SequenceGeneratorService generator,
                                  final NonNullPropertyCopier copier) {
        this.repository = repository;
        this.generator = generator;
        this.copier = copier;
    }

    /**
     * Creates the certificate item in the database.
     * @param item the item to be created
     * @return the created item
     */
    public CertificateItem createCertificateItem(final CertificateItem item) {
        CERTIFICATE.populateReadOnlyFields(item);
        item.setId(getNextId());
        setCreationDateTimes(item);
        return repository.save(item);
    }

    /**
     * Updates the certificate item in the database identified by the ID.
     * @param partialUpdate the partially populated item, the non-null fields of which
     *                      will be copied to the corresponding item in the database
     * @param id the ID of the certificate item to be updated
     * @return the updated certificate item, or <code>null</code> if the item could not be found
     * @throws InvocationTargetException should something unexpected happen
     * @throws IllegalAccessException should something unexpected happen
     */
    public CertificateItem updateCertificateItem(final CertificateItem partialUpdate, final String id) throws InvocationTargetException, IllegalAccessException {

        if (repository.findById(id).isPresent()) {
            final CertificateItem itemFound = repository.findById(id).get();
            final LocalDateTime now = LocalDateTime.now();
            itemFound.setUpdatedAt(now); //?
            copier.copyProperties(itemFound, partialUpdate);

            repository.save(itemFound);

            return itemFound;
        } else {
            return null;
        }

    }

    /**
     * Sets the created at and updated at date time 'timestamps' to now.
     * @param item the item to be 'timestamped'
     */
    void setCreationDateTimes(final Item item) {
        final LocalDateTime now = LocalDateTime.now();
        item.setCreatedAt(now);
        item.setUpdatedAt(now);
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
