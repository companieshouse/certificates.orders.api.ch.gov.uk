package uk.gov.companieshouse.certificates.orders.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeatureOptionsConfig {
    @Value("${llp.certificate.orders.enabled}")
    private boolean llpCertificateOrdersEnabled;
    @Value("${lp.certificate.orders.enabled}")
    private boolean lpCertificateOrdersEnabled;

    @Bean
    public FeatureOptions featureOptions() {
        return new FeatureOptions(llpCertificateOrdersEnabled, lpCertificateOrdersEnabled);
    }
}
