package uk.gov.companieshouse.certificates.orders.api.config;

public class FeatureOptions {
    private boolean llpCertificateOrdersEnabled;
    private boolean lpCertificateOrdersEnabled;
    private boolean liquidatedCompanyCertificateEnabled;

    public FeatureOptions(boolean llpCertificateOrdersEnabled, boolean lpCertificateOrdersEnabled, boolean liquidatedCompanyCertificateEnabled) {
        this.llpCertificateOrdersEnabled = llpCertificateOrdersEnabled;
        this.lpCertificateOrdersEnabled = lpCertificateOrdersEnabled;
        this.liquidatedCompanyCertificateEnabled = liquidatedCompanyCertificateEnabled;
    }

    public boolean isLlpCertificateOrdersEnabled() {
        return llpCertificateOrdersEnabled;
    }

    public boolean isLpCertificateOrdersEnabled() {
        return lpCertificateOrdersEnabled;
    }

    public boolean isLiquidatedCompanyCertificateEnabled() {
        return liquidatedCompanyCertificateEnabled;
    }
}
