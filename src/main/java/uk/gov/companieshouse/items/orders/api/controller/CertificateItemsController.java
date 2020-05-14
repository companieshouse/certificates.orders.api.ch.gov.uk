package uk.gov.companieshouse.items.orders.api.controller;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.mapper.CertificateItemMapper;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.service.CertificateItemService;
import uk.gov.companieshouse.items.orders.api.service.CompanyService;
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

import static org.springframework.http.HttpStatus.*;
import static uk.gov.companieshouse.items.orders.api.ItemsApiApplication.APPLICATION_NAMESPACE;

@RestController
public class CertificateItemsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    private static final String LOG_MESSAGE_DATA_KEY = "message";
    
    private static final String COMPANY_NUMBER = "company_number";
    private static final String REQUEST_ID = "request_id";
    private static final String CERTIFICATE_ID = "certificate_id";
    private static final String USER_ID = "user_id";
    private static final String STATUS = "status";
    private static final String ERRORS = "errors";

    private final CreateItemRequestValidator createItemRequestValidator;
    private final PatchItemRequestValidator patchItemRequestValidator;
    private final CertificateItemMapper mapper;
    private final PatchMerger patcher;
    private final CertificateItemService certificateItemService;
    private final CompanyService companyService;

    /**
     * Constructor.
     * @param createItemRequestValidator the validator this relies on for some create request 'input' validations
     * @param patchItemRequestValidator the validator this relies on for patch/update request 'input' validations
     * @param mapper mapper used by this to map between {@link CertificateItemDTO} and
     *               {@link CertificateItem} instances
     * @param patcher the component used by this to apply JSON merge patches to
     *                {@link CertificateItem} instances
     * @param certificateItemService the service used by this to manage and store certificate items
     */
    public CertificateItemsController(final CreateItemRequestValidator createItemRequestValidator,
                                      final PatchItemRequestValidator patchItemRequestValidator,
                                      final CertificateItemMapper mapper,
                                      final PatchMerger patcher,
                                      final CertificateItemService certificateItemService,
                                      final CompanyService companyService) {
        this.createItemRequestValidator = createItemRequestValidator;
        this.patchItemRequestValidator = patchItemRequestValidator;
        this.mapper = mapper;
        this.patcher = patcher;
        this.certificateItemService = certificateItemService;
        this.companyService = companyService;
    }

    @PostMapping("${uk.gov.companieshouse.items.orders.api.certificates}")
    public ResponseEntity<Object> createCertificateItem(final @Valid @RequestBody CertificateItemDTO certificateItemDTO,
                                                        HttpServletRequest request,
                                                        final @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {
    	Map<String, Object> logMap = new HashMap<>();
    	logMap.put(REQUEST_ID, requestId);
    	LOGGER.infoRequest(request, "create certficate item request", logMap);

        final List<String> errors = createItemRequestValidator.getValidationErrors(certificateItemDTO);
        if (!errors.isEmpty()) {
        	logMap.put(ERRORS, errors);
        	logMap.put(STATUS, BAD_REQUEST);
        	LOGGER.errorRequest(request, "create certificate item validation errors", logMap);
            return ResponseEntity.status(BAD_REQUEST).body(new ApiError(BAD_REQUEST, errors));
        }

        CertificateItem item = mapper.certificateItemDTOtoCertificateItem(certificateItemDTO);
        item.setUserId(EricHeaderHelper.getIdentity(request));
        final String companyName = companyService.getCompanyName(item.getCompanyNumber());
        item.setCompanyName(companyName);

        item = certificateItemService.createCertificateItem(item);
        final CertificateItemDTO createdCertificateItemDTO = mapper.certificateItemToCertificateItemDTO(item);
        
        logMap.put(USER_ID, item.getUserId());
        logMap.put(COMPANY_NUMBER, item.getCompanyNumber());
        logMap.put(CERTIFICATE_ID, item.getId());
        logMap.put(STATUS, CREATED);
        LOGGER.infoRequest(request, "certificate item created", logMap);
        return ResponseEntity.status(CREATED).body(createdCertificateItemDTO);
    }

    @GetMapping("${uk.gov.companieshouse.items.orders.api.certificates}/{id}")
    public ResponseEntity<Object> getCertificateItem(final @PathVariable String id,
                                                     final @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId)
    {
    	Map<String, Object> logMap = new HashMap<>();
    	logMap.put(CERTIFICATE_ID, id);
    	logMap.put(REQUEST_ID, requestId);
    	LOGGER.info("get certificate item request", logMap);
        Optional<CertificateItem> item = certificateItemService.getCertificateItemWithCosts(id);
        if(item.isPresent()) {
            final CertificateItemDTO createdCertificateItemDTO = mapper.certificateItemToCertificateItemDTO(item.get());
            logMap.put(STATUS, OK);
            LOGGER.info("certificate item found", logMap);
            return ResponseEntity.status(OK).body(createdCertificateItemDTO);
        } else {
        	String errorMsg = "certificate resource not found";
            final List<String> errors = new ArrayList<>();
            errors.add(errorMsg);
            logMap.put(ERRORS, errors);
            logMap.put(STATUS, NOT_FOUND);
            LOGGER.error(errorMsg, logMap);
            return ResponseEntity.status(NOT_FOUND).body(new ApiError(NOT_FOUND, errors));
        }
    }

    @PatchMapping(path = "${uk.gov.companieshouse.items.orders.api.certificates}/{id}",
                  consumes = "application/merge-patch+json")
    public ResponseEntity<Object> updateCertificateItem(
            final @RequestBody JsonMergePatch mergePatchDocument,
            final @PathVariable("id") String id,
            final @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {
    	Map<String, Object> logMap = new HashMap<>();
    	logMap.put(CERTIFICATE_ID, id);
    	logMap.put(REQUEST_ID, requestId);
    	LOGGER.info("update certificate item request", logMap);

        final List<String> errors = patchItemRequestValidator.getValidationErrors(mergePatchDocument);
        if (!errors.isEmpty()) {
        	logMap.put(ERRORS, errors);
        	logMap.put(STATUS, BAD_REQUEST);
        	LOGGER.error("update certificate item request had validation errors", logMap);
            return ResponseEntity.status(BAD_REQUEST).body(new ApiError(BAD_REQUEST, errors));
        }

        // TODO LOG Exception
        final CertificateItem itemRetrieved = certificateItemService.getCertificateItemById(id)
                .orElseThrow(ResourceNotFoundException::new);
        logMap.put(COMPANY_NUMBER, itemRetrieved.getCompanyNumber());
        logMap.put(USER_ID, itemRetrieved.getUserId());

        // Apply the patch
        final CertificateItem patchedItem = patcher.mergePatch(mergePatchDocument, itemRetrieved, CertificateItem.class);
        final List<String> patchedErrors = patchItemRequestValidator.getValidationErrors(patchedItem);
        if (!patchedErrors.isEmpty()) {
        	logMap.put(ERRORS, patchedErrors);
        	logMap.put(STATUS, BAD_REQUEST);
        	LOGGER.error("patched certificate item had validation errors", logMap);
            return ResponseEntity.status(BAD_REQUEST).body(new ApiError(BAD_REQUEST, patchedErrors));
        }

        final String companyName = companyService.getCompanyName(patchedItem.getCompanyNumber());
        patchedItem.setCompanyName(companyName);
        final CertificateItem savedItem = certificateItemService.saveCertificateItem(patchedItem);
        final CertificateItemDTO savedItemDTO = mapper.certificateItemToCertificateItemDTO(savedItem);

        logMap.put(STATUS, OK);
        LOGGER.info("update certificate item request completed", logMap);

        return ResponseEntity.ok().body(savedItemDTO);
    }
}
