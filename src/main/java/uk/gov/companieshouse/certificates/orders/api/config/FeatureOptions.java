package uk.gov.companieshouse.certificates.orders.api.config;

public class FeatureOptions {
    private boolean llpCertificateOrdersEnabled;
    private boolean lpCertificateOrdersEnabled;
    private boolean liquidatedCompanyCertificateEnabled;
    private boolean administratorCompanyCertificateEnabled;

    public FeatureOptions(boolean llpCertificateOrdersEnabled,
                          boolean lpCertificateOrdersEnabled,
                          boolean liquidatedCompanyCertificateEnabled,
                          boolean administratorCompanyCertificateEnabled) {
        this.llpCertificateOrdersEnabled = llpCertificateOrdersEnabled;
        this.lpCertificateOrdersEnabled = lpCertificateOrdersEnabled;
        this.liquidatedCompanyCertificateEnabled = liquidatedCompanyCertificateEnabled;
        this.administratorCompanyCertificateEnabled = administratorCompanyCertificateEnabled;
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

    public boolean isAdministratorCompanyCertificateEnabled() {
        return administratorCompanyCertificateEnabled;
    }
}
