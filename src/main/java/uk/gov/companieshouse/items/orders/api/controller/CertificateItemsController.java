package uk.gov.companieshouse.items.orders.api.controller;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.mapper.CertificateItemMapper;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.service.CertificateItemService;
import uk.gov.companieshouse.items.orders.api.util.EricHeaderHelper;
import uk.gov.companieshouse.items.orders.api.util.PatchMerger;
import uk.gov.companieshouse.items.orders.api.validator.CreateItemRequestValidator;
import uk.gov.companieshouse.items.orders.api.validator.PatchItemRequestValidator;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import javax.json.JsonMergePatch;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

import static uk.gov.companieshouse.items.orders.api.ItemsApiApplication.APPLICATION_NAMESPACE;

@RestController
public class CertificateItemsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    private static final String LOG_MESSAGE_DATA_KEY = "message";

    private final CreateItemRequestValidator createItemRequestValidator;
    private final PatchItemRequestValidator patchItemRequestValidator;
    private final CertificateItemMapper mapper;
    private final PatchMerger patcher;
    private final CertificateItemService service;

    /**
     * Constructor.
     * @param createItemRequestValidator the validator this relies on for some create request 'input' validations
     * @param patchItemRequestValidator the validator this relies on for patch/update request 'input' validations
     * @param mapper mapper used by this to map between {@link CertificateItemDTO} and
     *               {@link CertificateItem} instances
     * @param patcher the component used by this to apply JSON merge patches to
     *                {@link CertificateItem} instances
     * @param service the service used by this to manage and store certificate items
     */
    public CertificateItemsController(final CreateItemRequestValidator createItemRequestValidator,
                                      final PatchItemRequestValidator patchItemRequestValidator,
                                      final CertificateItemMapper mapper,
                                      final PatchMerger patcher,
                                      final CertificateItemService service) {
        this.createItemRequestValidator = createItemRequestValidator;
        this.patchItemRequestValidator = patchItemRequestValidator;
        this.mapper = mapper;
        this.patcher = patcher;
        this.service = service;
    }

    @PostMapping("${uk.gov.companieshouse.items.orders.api.path}")
    public ResponseEntity<Object> createCertificateItem(final @Valid @RequestBody CertificateItemDTO certificateItemDTO,
                                                        HttpServletRequest request,
                                                        final @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {
        trace("ENTERING createCertificateItem(" + certificateItemDTO + ")", requestId);

        final List<String> errors = createItemRequestValidator.getValidationErrors(certificateItemDTO);
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST, errors));
        }

        CertificateItem item = mapper.certificateItemDTOtoCertificateItem(certificateItemDTO);
        item.setUserId(EricHeaderHelper.getIdentity(request));

        item = service.createCertificateItem(item);
        final CertificateItemDTO createdCertificateItemDTO = mapper.certificateItemToCertificateItemDTO(item);

        trace("EXITING createCertificateItem() with " + createdCertificateItemDTO, requestId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCertificateItemDTO);
    }

    @GetMapping("${uk.gov.companieshouse.items.orders.api.path}/{id}")
    public ResponseEntity<Object> getCertificateItem(final @PathVariable String id,
                                                     final @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId)
    {
        Optional<CertificateItem> item = service.getCertificateItemById(id);
        if(item.isPresent()) {
            final CertificateItemDTO createdCertificateItemDTO = mapper.certificateItemToCertificateItemDTO(item.get());
            return ResponseEntity.status(HttpStatus.OK).body(createdCertificateItemDTO);
        } else {
            final List<String> errors = new ArrayList<>();
            errors.add("certificate resource not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(HttpStatus.NOT_FOUND, errors));
        }
    }

    @PatchMapping(path = "${uk.gov.companieshouse.items.orders.api.path}/{id}",
                  consumes = "application/merge-patch+json")
    public ResponseEntity<Object> updateCertificateItem(
            final @RequestBody JsonMergePatch mergePatchDocument,
            final @PathVariable("id") String id,
            final @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {

        trace("ENTERING updateCertificateItem(" + mergePatchDocument + ", " + id + ", " + requestId + ")", requestId);

        final List<String> errors = patchItemRequestValidator.getValidationErrors(mergePatchDocument);
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST, errors));
        }

        final CertificateItem itemRetrieved = service.getCertificateItemById(id)
                .orElseThrow(ResourceNotFoundException::new);

        // Apply the patch
        final CertificateItem patchedItem = patcher.mergePatch(mergePatchDocument, itemRetrieved, CertificateItem.class);

        final CertificateItem savedItem = service.saveCertificateItem(patchedItem);
        final CertificateItemDTO savedItemDTO = mapper.certificateItemToCertificateItemDTO(savedItem);

        trace("EXITING updateCertificateItem() with " + savedItemDTO, requestId);

        return ResponseEntity.ok().body(savedItemDTO);
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
