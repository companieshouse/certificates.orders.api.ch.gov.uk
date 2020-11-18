package uk.gov.companieshouse.certificates.orders.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import uk.gov.companieshouse.certificates.orders.api.model.CertificateItem;

@Repository
public interface CertificateItemRepository extends MongoRepository<CertificateItem, String> { }
