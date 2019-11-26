package uk.gov.companieshouse.items.orders.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import static uk.gov.companieshouse.items.orders.api.ItemsApiApplication.APPLICATION_NAMESPACE;

@RestController
public class CertificateItemsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    @PostMapping("${uk.gov.companieshouse.items.orders.api.path}")
    public Object createCertificateItem() {

        LOGGER.info("createCertificateItem called.");


        return null;
    }

}
