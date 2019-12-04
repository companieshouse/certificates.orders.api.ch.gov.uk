package uk.gov.companieshouse.items.orders.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;

@RepositoryRestResource
public interface CertificateItemRepository extends MongoRepository<CertificateItem, String> { }
