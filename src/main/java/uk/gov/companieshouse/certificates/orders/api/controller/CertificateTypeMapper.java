package uk.gov.companieshouse.certificates.orders.api.controller;

import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;

public interface CertificateTypeMapper {
    CertificateTypeMapping mapToCertificateType(CompanyProfileResource companyProfileResource);
}
