package uk.gov.companieshouse.certificates.orders.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeatureOptionsConfig {
    @Value("${llp.certificate.orders.enabled}")
    private boolean LLP_CERTIFICATE_ORDERS_ENABLED;
    @Value("${lp.certificate.orders.enabled}")
    private boolean LP_CERTIFICATE_ORDERS_ENABLED;

    @Bean
    public FeatureOptions featureOptions() {
        return new FeatureOptions(LLP_CERTIFICATE_ORDERS_ENABLED, LP_CERTIFICATE_ORDERS_ENABLED);
    }
}
