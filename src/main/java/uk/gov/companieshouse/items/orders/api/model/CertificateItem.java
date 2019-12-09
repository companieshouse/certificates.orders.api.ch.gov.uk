package uk.gov.companieshouse.items.orders.api.model;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * An instance of this represents a certificate item.
 */
@Document(collection = "certificates")
public class CertificateItem extends Item { }
