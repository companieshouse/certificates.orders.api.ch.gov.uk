package uk.gov.companieshouse.items.orders.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import javax.json.JsonMergePatch;
import javax.json.JsonValue;
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
    private final ObjectMapper objectMapper;
    private final CertificateItemService service;

    /**
     * Constructor.
     * @param validator the validator this relies on for some 'input' validations
     * @param mapper mapper used by this to map between {@link CertificateItemDTO} and
     *               {@link CertificateItem} instances
     * @param objectMapper mapper used by this to convert between {@link JsonMergePatch} and
     *                     {@link CertificateItem} instances
     * @param service the service used by this to manage and store certificate items
     */
    public CertificateItemsController(final CreateItemRequestValidator validator,
                                      final CertificateItemMapper mapper,
                                      final ObjectMapper objectMapper,
                                      final CertificateItemService service) {
        this.validator = validator;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
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

//    @PatchMapping("${uk.gov.companieshouse.items.orders.api.path}/{id}")
//    public ResponseEntity updateCertificateItem(
//            final @Valid @RequestBody CertificateItemDTO certificateItemDTO,
//            final @PathVariable("id") String id,
//            final @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) throws InvocationTargetException, IllegalAccessException {
//
//        trace("ENTERING updateCertificateItem(" + certificateItemDTO + ", " + id + ", " + requestId + ")", requestId);
//
//        final CertificateItem item = mapper.certificateItemDTOtoCertificateItem(certificateItemDTO);
//        trace("item = " + item, requestId);
//
//        final CertificateItem updatedItem = service.updateCertificateItem(item, id);
//
//        return ResponseEntity.status(updatedItem != null ? HttpStatus.OK : HttpStatus.NOT_FOUND).build();
//    }

    @PatchMapping(path = "${uk.gov.companieshouse.items.orders.api.path}/{id}",
                  consumes = "application/merge-patch+json")
    public ResponseEntity updateCertificateItem(
            /*final @Valid @RequestBody CertificateItemDTO certificateItemDTO*/
            final @RequestBody JsonMergePatch mergePatchDocument,
            final @PathVariable("id") String id,
            final @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {

        trace("ENTERING updateCertificateItem(" + mergePatchDocument + ", " + id + ", " + requestId + ")", requestId);

// TODO
//        final CertificateItem itemRetrieved = service.getCertificateItemById(id)
//                .orElseThrow(ResourceNotFoundException::new);

        return ResponseEntity.noContent().build();
    }

    <T> T mergePatch(final JsonMergePatch mergePatch, final T targetBean, final Class<T> beanClass) {

        // Convert the Java bean to a JSON document
        JsonValue target = objectMapper.convertValue(targetBean, JsonValue.class);

        // Apply the JSON Merge Patch to the JSON document
        JsonValue patched = mergePatch.apply(target);

        // Convert the JSON document to a Java bean and return it
        return objectMapper.convertValue(patched, beanClass);
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
