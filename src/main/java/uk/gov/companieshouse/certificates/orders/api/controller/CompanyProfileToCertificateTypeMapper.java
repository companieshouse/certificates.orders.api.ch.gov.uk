package uk.gov.companieshouse.certificates.orders.api.controller;

import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;

interface CompanyProfileToCertificateTypeMapper {
    CertificateTypeMapResult mapToCertificateType(CompanyProfileResource companyProfileResource);
}
