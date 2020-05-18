package uk.gov.companieshouse.items.orders.api.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.companieshouse.items.orders.api.ItemsApiApplication.APPLICATION_NAMESPACE;
import static uk.gov.companieshouse.items.orders.api.ItemsApiApplication.CERTIFICATE_ID_LOG_KEY;
import static uk.gov.companieshouse.items.orders.api.ItemsApiApplication.COMPANY_NUMBER_LOG_KEY;
import static uk.gov.companieshouse.items.orders.api.ItemsApiApplication.ERRORS_LOG_KEY;
import static uk.gov.companieshouse.items.orders.api.ItemsApiApplication.REQUEST_ID_HEADER_NAME;
import static uk.gov.companieshouse.items.orders.api.ItemsApiApplication.REQUEST_ID_LOG_KEY;
import static uk.gov.companieshouse.items.orders.api.ItemsApiApplication.STATUS_LOG_KEY;
import static uk.gov.companieshouse.items.orders.api.ItemsApiApplication.USER_ID_LOG_KEY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.json.JsonMergePatch;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
public class CertificateItemsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

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
        logMap.put(REQUEST_ID_LOG_KEY, requestId);
        LOGGER.infoRequest(request, "create certficate item request", logMap);

        final List<String> errors = createItemRequestValidator.getValidationErrors(certificateItemDTO);
        if (!errors.isEmpty()) {
            logMap.put(ERRORS_LOG_KEY, errors);
            logMap.put(STATUS_LOG_KEY, BAD_REQUEST);
            LOGGER.errorRequest(request, "create certificate item validation errors", logMap);
            return ResponseEntity.status(BAD_REQUEST).body(new ApiError(BAD_REQUEST, errors));
        }

        CertificateItem item = mapper.certificateItemDTOtoCertificateItem(certificateItemDTO);
        item.setUserId(EricHeaderHelper.getIdentity(request));
        final String companyName = companyService.getCompanyName(item.getCompanyNumber());
        item.setCompanyName(companyName);

        item = certificateItemService.createCertificateItem(item);
        final CertificateItemDTO createdCertificateItemDTO = mapper.certificateItemToCertificateItemDTO(item);
        
        logMap.put(USER_ID_LOG_KEY, item.getUserId());
        logMap.put(COMPANY_NUMBER_LOG_KEY, item.getCompanyNumber());
        logMap.put(CERTIFICATE_ID_LOG_KEY, item.getId());
        logMap.put(STATUS_LOG_KEY, CREATED);
        LOGGER.infoRequest(request, "certificate item created", logMap);
        return ResponseEntity.status(CREATED).body(createdCertificateItemDTO);
    }

    @GetMapping("${uk.gov.companieshouse.items.orders.api.certificates}/{id}")
    public ResponseEntity<Object> getCertificateItem(final @PathVariable String id,
                                                     final @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId)
    {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put(CERTIFICATE_ID_LOG_KEY, id);
        logMap.put(REQUEST_ID_LOG_KEY, requestId);
        LOGGER.info("get certificate item request", logMap);
        Optional<CertificateItem> item = certificateItemService.getCertificateItemWithCosts(id);
        if(item.isPresent()) {
            final CertificateItemDTO createdCertificateItemDTO = mapper.certificateItemToCertificateItemDTO(item.get());
            logMap.put(STATUS_LOG_KEY, OK);
            LOGGER.info("certificate item found", logMap);
            return ResponseEntity.status(OK).body(createdCertificateItemDTO);
        } else {
            String errorMsg = "certificate resource not found";
            final List<String> errors = new ArrayList<>();
            errors.add(errorMsg);
            logMap.put(ERRORS_LOG_KEY, errors);
            logMap.put(STATUS_LOG_KEY, NOT_FOUND);
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
        logMap.put(CERTIFICATE_ID_LOG_KEY, id);
        logMap.put(REQUEST_ID_LOG_KEY, requestId);
        LOGGER.info("update certificate item request", logMap);

        final List<String> errors = patchItemRequestValidator.getValidationErrors(mergePatchDocument);
        if (!errors.isEmpty()) {
            logMap.put(ERRORS_LOG_KEY, errors);
            logMap.put(STATUS_LOG_KEY, BAD_REQUEST);
            LOGGER.error("update certificate item request had validation errors", logMap);
            return ResponseEntity.status(BAD_REQUEST).body(new ApiError(BAD_REQUEST, errors));
        }

        Optional<CertificateItem> certRetrieved = certificateItemService.getCertificateItemById(id);
        if(!certRetrieved.isPresent()) {
            logMap.put(STATUS_LOG_KEY, HttpStatus.NOT_FOUND);
            LOGGER.error("certificate item not found", logMap);
            return ResponseEntity.notFound().build();
        }
        final CertificateItem itemRetrieved = certRetrieved.get();
        logMap.put(COMPANY_NUMBER_LOG_KEY, itemRetrieved.getCompanyNumber());
        logMap.put(USER_ID_LOG_KEY, itemRetrieved.getUserId());

        // Apply the patch
        final CertificateItem patchedItem = patcher.mergePatch(mergePatchDocument, itemRetrieved, CertificateItem.class);
        final List<String> patchedErrors = patchItemRequestValidator.getValidationErrors(patchedItem);
        if (!patchedErrors.isEmpty()) {
            logMap.put(ERRORS_LOG_KEY, patchedErrors);
            logMap.put(STATUS_LOG_KEY, BAD_REQUEST);
            LOGGER.error("patched certificate item had validation errors", logMap);
            return ResponseEntity.status(BAD_REQUEST).body(new ApiError(BAD_REQUEST, patchedErrors));
        }

        final String companyName = companyService.getCompanyName(patchedItem.getCompanyNumber());
        patchedItem.setCompanyName(companyName);
        final CertificateItem savedItem = certificateItemService.saveCertificateItem(patchedItem);
        final CertificateItemDTO savedItemDTO = mapper.certificateItemToCertificateItemDTO(savedItem);

        logMap.put(STATUS_LOG_KEY, OK);
        LOGGER.info("update certificate item request completed", logMap);

        return ResponseEntity.ok().body(savedItemDTO);
    }
}
