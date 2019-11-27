package uk.gov.companieshouse.items.orders.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import static uk.gov.companieshouse.items.orders.api.ItemType.CERTIFICATE;
import static uk.gov.companieshouse.items.orders.api.ItemsApiApplication.APPLICATION_NAMESPACE;

@RestController
public class CertificateItemsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    @PostMapping("${uk.gov.companieshouse.items.orders.api.path}")
    @ResponseStatus(HttpStatus.CREATED)
    public CertificateItemDTO createCertificateItem(final @RequestBody CertificateItemDTO certificateItemDTO) {

        LOGGER.info("ENTERING createCertificateItem(" + certificateItemDTO + ")");

        CERTIFICATE.populateDescriptionFields(certificateItemDTO);
        CERTIFICATE.populateItemCosts(certificateItemDTO);

        LOGGER.info("EXITING createCertificateItem() with " + certificateItemDTO);
        return certificateItemDTO;
    }

}
