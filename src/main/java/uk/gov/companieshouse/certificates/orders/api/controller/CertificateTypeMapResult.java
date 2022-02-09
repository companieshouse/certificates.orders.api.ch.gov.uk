package uk.gov.companieshouse.certificates.orders.api.controller;

import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateType;

import static java.util.Objects.isNull;

final class CertificateTypeMapResult implements CertificateTypeable {
    private final ApiError mappingError;
    private final CertificateType certificateType;

    CertificateTypeMapResult(ApiError mappingError) {
        this.mappingError = mappingError;
        this.certificateType = null;
    }

    CertificateTypeMapResult(CertificateType certificateType) {
        this.mappingError = null;
        this.certificateType = certificateType;
    }

    boolean isMappingError() {
        return !isNull(mappingError);
    }

    ApiError getMappingError() {
        return mappingError;
    }

    @Override
    public CertificateType getCertificateType() {
        return certificateType;
    }
}
