package uk.gov.companieshouse.certificates.orders.api.config;

public class FeatureOptions {
    private boolean llpCertificateOrdersEnabled;
    private boolean lpCertificateOrdersEnabled;

    public FeatureOptions(boolean llpCertificateOrdersEnabled, boolean lpCertificateOrdersEnabled) {
        this.llpCertificateOrdersEnabled = llpCertificateOrdersEnabled;
        this.lpCertificateOrdersEnabled = lpCertificateOrdersEnabled;
    }

    public boolean isLlpCertificateOrdersEnabled() {
        return llpCertificateOrdersEnabled;
    }

    public boolean isLpCertificateOrdersEnabled() {
        return lpCertificateOrdersEnabled;
    }
}
