package uk.gov.companieshouse.certificates.orders.api.validator;

import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;

public interface RequestValidatable {
    CertificateItemOptions getItemOptions();
}
