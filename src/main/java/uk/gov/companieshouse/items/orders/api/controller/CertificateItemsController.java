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
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        trace("ENTERING createCertificateItem(" + certificateItemDTO + ")", requestId);

        final List<String> errors = validator.getValidationErrors(certificateItemDTO);
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST, errors));
        }

        CertificateItem item = mapper.certificateItemDTOtoCertificateItem(certificateItemDTO);
        item = service.createCertificateItem(item);
        final CertificateItemDTO createdCertificateItemDTO = mapper.certificateItemToCertificateItemDTO(item);

        trace("EXITING createCertificateItem() with " + createdCertificateItemDTO, requestId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCertificateItemDTO);
    }

    @PatchMapping("${uk.gov.companieshouse.items.orders.api.path}/{id}")
    public ResponseEntity updateCertificateItem(
            final @Valid @RequestBody CertificateItemDTO certificateItemDTO,
            final @PathVariable("id") String id,
            final @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) throws InvocationTargetException, IllegalAccessException {

        trace("ENTERING updateCertificateItem(" + certificateItemDTO + ", " + id + ", " + requestId + ")", requestId);

        final CertificateItem item = mapper.certificateItemDTOtoCertificateItem(certificateItemDTO);
        trace("item = " + item, requestId);

        final CertificateItem updatedItem = service.updateCertificateItem(item, id);

        return ResponseEntity.status(updatedItem != null ? HttpStatus.OK : HttpStatus.NOT_FOUND).build();
    }

    /**
     * Utility method that logs each message with the request ID for log tracing/analysis.
     * @param message the message to log
     * @param requestId the request ID
     */
    private void trace(final String message, final String requestId) {
        final Map<String, Object> logData = new HashMap<>();
        logData.put(LOG_MESSAGE_DATA_KEY, message);
        LOGGER.traceContext(requestId, "X Request ID header", logData);
    }

}
