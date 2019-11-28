package uk.gov.companieshouse.items.orders.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ItemsApiApplication {

	public static final String APPLICATION_NAMESPACE = "items.orders.api.ch.gov.uk";

	public static void main(String[] args) {
		SpringApplication.run(ItemsApiApplication.class, args);
	}

}
