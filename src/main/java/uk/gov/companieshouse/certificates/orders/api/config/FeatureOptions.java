package uk.gov.companieshouse.certificates.orders.api.config;

public record FeatureOptions(boolean llpCertificateOrdersEnabled, boolean lpCertificateOrdersEnabled,
                             boolean liquidatedCompanyCertificateEnabled,
                             boolean administratorCompanyCertificateEnabled) {
}
