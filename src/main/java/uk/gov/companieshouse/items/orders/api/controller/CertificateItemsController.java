package uk.gov.companieshouse.items.orders.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.mapper.CertificateItemMapper;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.service.CertificateItemService;
import uk.gov.companieshouse.items.orders.api.validator.CreateItemRequestValidator;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import javax.validation.Valid;
import java.util.List;

import static uk.gov.companieshouse.items.orders.api.ItemsApiApplication.APPLICATION_NAMESPACE;

@RestController
public class CertificateItemsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    private final CreateItemRequestValidator validator;
    private final CertificateItemMapper mapper;
    private final CertificateItemService service;

    public CertificateItemsController(final CreateItemRequestValidator validator,
                                      final CertificateItemMapper mapper,
                                      final CertificateItemService service) {
        this.validator = validator;
        this.mapper = mapper;
        this.service = service;
    }

    @PostMapping("${uk.gov.companieshouse.items.orders.api.path}")
    public ResponseEntity<Object> createCertificateItem(final @Valid @RequestBody CertificateItemDTO certificateItemDTO)
    {
        LOGGER.info("ENTERING createCertificateItem(" + certificateItemDTO + ")");

        final List<String> errors = validator.getValidationErrors(certificateItemDTO);
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST, errors));
        }

        CertificateItem item = mapper.certificateItemDTOtoCertificateItem(certificateItemDTO);
        item = service.createCertificateItem(item);
        final CertificateItemDTO createdCertificateItemDTO = mapper.certificateItemToCertificateItemDTO(item);

        LOGGER.info("EXITING createCertificateItem() with " + createdCertificateItemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCertificateItemDTO);
    }

}
