package uk.gov.companieshouse.certificates.orders.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CertificatesApiApplication {

	public static void main(String[] args) {
		if (checkEnvironmentVariables()) {
			SpringApplication.run(CertificatesApiApplication.class, args);
		}
	}

	/**
	 * Checks whether all required environment variables have defined values.
	 * @return whether all required environment variables have defined values (<code>true</code>), or not
	 * (<code>false</code>)
	 */
	static boolean checkEnvironmentVariables() {
		// TODO GCI-1316 Implement this!
		return false;
	}

}
