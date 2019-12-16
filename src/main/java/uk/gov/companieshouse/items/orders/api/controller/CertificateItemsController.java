package uk.gov.companieshouse.items.orders.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.mapper.CertificateItemMapper;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.service.CertificateItemService;
import uk.gov.companieshouse.items.orders.api.validator.CreateItemRequestValidator;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import javax.validation.Valid;
import java.util.*;

import static uk.gov.companieshouse.items.orders.api.ItemsApiApplication.APPLICATION_NAMESPACE;

@RestController
public class CertificateItemsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    private static final String LOG_MESSAGE_DATA_KEY = "message";

    private final CreateItemRequestValidator validator;
    private final CertificateItemMapper mapper;
    private final CertificateItemService service;

    /**
     * Constructor.
     * @param validator the validator this relies on for some 'input' validations
     * @param mapper mapper used by this to map between {@link CertificateItemDTO} and
     *               {@link CertificateItem} instances
     * @param service the service used by this to manage and store certificate items
     */
    public CertificateItemsController(final CreateItemRequestValidator validator,
                                      final CertificateItemMapper mapper,
                                      final CertificateItemService service) {
        this.validator = validator;
        this.mapper = mapper;
        this.service = service;
    }

    @PostMapping("${uk.gov.companieshouse.items.orders.api.path}")
    public ResponseEntity<Object> createCertificateItem(final @Valid @RequestBody CertificateItemDTO certificateItemDTO,
                                                        final @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId)
    {
        final Map<String, Object> logData = new HashMap<>();
        logData.put(LOG_MESSAGE_DATA_KEY, "ENTERING createCertificateItem(" + certificateItemDTO + ")");
        LOGGER.infoContext(requestId, "X Request ID header", logData);

        final List<String> errors = validator.getValidationErrors(certificateItemDTO);
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST, errors));
        }

        CertificateItem item = mapper.certificateItemDTOtoCertificateItem(certificateItemDTO);
        item = service.createCertificateItem(item);
        final CertificateItemDTO createdCertificateItemDTO = mapper.certificateItemToCertificateItemDTO(item);

        logData.put(LOG_MESSAGE_DATA_KEY, "EXITING createCertificateItem() with " + createdCertificateItemDTO);
        LOGGER.infoContext(requestId, "X Request ID header", logData);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCertificateItemDTO);
    }

    @GetMapping("${uk.gov.companieshouse.items.orders.api.path}/{certificateId}")
    public ResponseEntity<Object> getCertificateItem(final @PathVariable String certificateId,
                                                     final @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId)
    {
        Optional<CertificateItem> item = service.getCertificateItemById(certificateId);
        if(item.isPresent()) {
            final CertificateItemDTO createdCertificateItemDTO = mapper.certificateItemToCertificateItemDTO(item.get());
            return ResponseEntity.status(HttpStatus.OK).body(createdCertificateItemDTO);
        } else {
            final List<String> errors = new ArrayList<>();
            errors.add("certificate resource not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(HttpStatus.NOT_FOUND, errors));
        }
    }

}
