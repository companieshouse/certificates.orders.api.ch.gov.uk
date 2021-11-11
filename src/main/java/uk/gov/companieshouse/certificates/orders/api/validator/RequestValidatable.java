package uk.gov.companieshouse.certificates.orders.api.validator;

import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;

public interface RequestValidatable {
    CompanyStatus getCompanyStatus();
    String getCertificateId();
    CertificateItemOptions getItemOptions();
}
