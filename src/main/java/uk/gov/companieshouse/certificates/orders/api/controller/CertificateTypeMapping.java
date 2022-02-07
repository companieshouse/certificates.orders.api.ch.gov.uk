package uk.gov.companieshouse.certificates.orders.api.controller;

import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateType;

import static java.util.Objects.isNull;

public final class CertificateTypeMapping implements CertificateTypeable {
    private final ApiError mappingError;
    private final CertificateType certificateType;

    public CertificateTypeMapping(ApiError mappingError) {
        this.mappingError = mappingError;
        this.certificateType = null;
    }

    public CertificateTypeMapping(CertificateType certificateType) {
        this.mappingError = null;
        this.certificateType = certificateType;
    }

    public boolean isMappingError() {
        return !isNull(mappingError);
    }

    public ApiError getMappingError() {
        return mappingError;
    }

    @Override
    public CertificateType getCertificateType() {
        return certificateType;
    }
}
