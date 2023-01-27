package uk.gov.companieshouse.certificates.orders.api.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItem;
import uk.gov.companieshouse.certificates.orders.api.model.Item;
import uk.gov.companieshouse.certificates.orders.api.repository.CertificateItemRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static uk.gov.companieshouse.certificates.orders.api.ItemType.CERTIFICATE;

/**
 * Service for the management and storage of certificate items.
 */
@Service
public class CertificateItemService {

    private final CertificateItemRepository repository;
    private final IdGeneratorService idGenerator;
    private final DescriptionProviderService descriptions;
    private final CertificateCostCalculatorService calculator;
    private final EtagGeneratorService etagGenerator;
    private final LinksGeneratorService linksGenerator;

    public CertificateItemService(final CertificateItemRepository repository,

                                  final DescriptionProviderService descriptions,
                                  final IdGeneratorService idGenerator,
                                  final CertificateCostCalculatorService calculator,
                                  final EtagGeneratorService etagGenerator,
                                  final LinksGeneratorService linksGenerator) {
        this.repository = repository;
        this.idGenerator = idGenerator;
        this.descriptions = descriptions;
        this.calculator = calculator;
        this.etagGenerator = etagGenerator;
        this.linksGenerator = linksGenerator;
    }

    /**
     * Creates the certificate item in the database.
     *
     * @param item the item to be created
     * @param userGetsFreeCertificates whether the current user is entitled to free certificates (<code>true</code>),
     *                                 or not (<code>false</code>)
     * @return the created item
     */
    public CertificateItem createCertificateItem(final CertificateItem item, final boolean userGetsFreeCertificates) {
        CERTIFICATE.populateReadOnlyFields(item, descriptions);
        item.setId(idGenerator.autoGenerateId());
        setCreationDateTimes(item);
        item.setEtag(etagGenerator.generateEtag());
        item.setLinks(linksGenerator.generateLinks(item.getId()));
        final CertificateItem itemSaved = repository.save(item);
        CERTIFICATE.populateItemCosts(itemSaved, calculator, userGetsFreeCertificates);
        return itemSaved;
    }

    /**
     * Saves the certificate item, assumed to have been updated, to the database.
     *
     * @param updatedCertificateItem the certificate item to save
     * @param userGetsFreeCertificates whether the current user is entitled to free certificates (<code>true</code>),
     *                                 or not (<code>false</code>)
     * @return the latest certificate item state resulting from the save
     */
    public CertificateItem saveCertificateItem(final CertificateItem updatedCertificateItem,
                                               final boolean userGetsFreeCertificates) {
        final LocalDateTime now = LocalDateTime.now();
        updatedCertificateItem.setUpdatedAt(now);
        CERTIFICATE.populateDerivedDescriptionFields(updatedCertificateItem, descriptions);
        updatedCertificateItem.setEtag(etagGenerator.generateEtag());
        final CertificateItem itemSaved = repository.save(updatedCertificateItem);
        CERTIFICATE.populateItemCosts(itemSaved, calculator, userGetsFreeCertificates);
        return itemSaved;
    }

    /**
     * Sets the created at and updated at date time 'timestamps' to now.
     *
     * @param item the item to be 'timestamped'
     */
    void setCreationDateTimes(final Item item) {
        final LocalDateTime now = LocalDateTime.now();
        item.setCreatedAt(now);
        item.setUpdatedAt(now);
    }

    /**
     * Gets the certificate item by its ID, and returns it as-is, without decorating it in any way.
     * (Compare with {@link #getCertificateItemWithCosts(String)}).
     *
     * @param id the ID of the certificate item to be retrieved
     * @return the undecorated item retrieved from the DB
     */
    public Optional<CertificateItem> getCertificateItemById(String id) {
        return repository.findById(id);
    }

    /**
     * Gets the certificate item by its ID, calculating its costs on the fly.
     * (Compare with {@link #getCertificateItemById(String)}).
     *
     * @param id the ID of the certificate item to be retrieved
     * @param userGetsFreeCertificates whether the current user is entitled to free certificates (<code>true</code>),
     *                                 or not (<code>false</code>)
     * @return the item, complete with its calculated costs
     */
    public Optional<CertificateItem> getCertificateItemWithCosts(final String id,
                                                                 final boolean userGetsFreeCertificates) {
        final Optional<CertificateItem> retrievedItem = repository.findById(id);
        retrievedItem.ifPresent(item -> CERTIFICATE.populateItemCosts(item, calculator, userGetsFreeCertificates));
        return retrievedItem;
    }
}