package uk.gov.companieshouse.certificates.orders.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.gov.companieshouse.certificates.orders.api.config.EnvironmentVariableChecks;

@SpringBootApplication
public class CertificatesApiApplication {

    public static void main(String[] args) {
        if (EnvironmentVariableChecks.checkEnvironmentVariables()) {
            SpringApplication.run(CertificatesApiApplication.class, args);
        }
    }
}
