package uk.gov.companieshouse.certificates.orders.api.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants.APPLICATION_NAMESPACE;
import static uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants.CERTIFICATE_ID_LOG_KEY;
import static uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants.COMPANY_NUMBER_LOG_KEY;
import static uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants.ERRORS_LOG_KEY;
import static uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants.MESSAGE;
import static uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants.PATCHED_COMPANY_NUMBER;
import static uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants.REQUEST_ID_HEADER_NAME;
import static uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants.REQUEST_ID_LOG_KEY;
import static uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants.STATUS_LOG_KEY;
import static uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants.USER_ID_LOG_KEY;

import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.web.server.ResponseStatusException;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemRequestDTO;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemInitialDTO;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemResponseDTO;
import uk.gov.companieshouse.certificates.orders.api.mapper.CertificateItemMapper;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItem;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;
import uk.gov.companieshouse.certificates.orders.api.service.CertificateItemService;
import uk.gov.companieshouse.certificates.orders.api.service.CompanyService;
import uk.gov.companieshouse.certificates.orders.api.util.EricHeaderHelper;
import uk.gov.companieshouse.certificates.orders.api.util.PatchMerger;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;
import uk.gov.companieshouse.certificates.orders.api.validator.CreateItemRequestValidator;
import uk.gov.companieshouse.certificates.orders.api.validator.PatchItemRequestValidator;
import uk.gov.companieshouse.certificates.orders.api.validator.RequestValidatable;
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
     * @param mapper mapper used by this to map between {@link CertificateItemRequestDTO} and
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

    @PostMapping("${uk.gov.companieshouse.certificates.orders.api.certificates}")
    public ResponseEntity<Object> createCertificateItem(final @Valid @RequestBody CertificateItemRequestDTO certificateItemRequestDTO,
                                                         HttpServletRequest request,
                                                         final @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {
        Map<String, Object> logMap = createLoggingDataMap(requestId);
        LOGGER.infoRequest(request, "create certificate item request", logMap);

        String companyNumber = certificateItemRequestDTO.getCompanyNumber();
        final CompanyProfileResource companyProfile = companyService.getCompanyProfile(companyNumber);

        final List<String> errors = createItemRequestValidator.getValidationErrors(
                new CompanyCertificateInformation(companyProfile.getCompanyStatus(),
                        certificateItemRequestDTO.getId(), certificateItemRequestDTO.getItemOptions()));
        if (!errors.isEmpty()) {
            logErrorsWithStatus(logMap, errors, BAD_REQUEST);
            LOGGER.errorRequest(request, "create certificate item validation errors", logMap);
            return ResponseEntity.status(BAD_REQUEST).body(new ApiError(BAD_REQUEST, errors));
        }

        CertificateItem item = mapper.certificateItemDTOtoCertificateItem(certificateItemRequestDTO);
        item = mapper.enrichCertificateItem(EricHeaderHelper.getIdentity(request), companyProfile, item);
        item = certificateItemService.createCertificateItem(item);
        final CertificateItemResponseDTO createdCertificateItemDTO = mapper.certificateItemToCertificateItemResponseDTO(item);

        logMap.put(USER_ID_LOG_KEY, item.getUserId());
        logMap.put(COMPANY_NUMBER_LOG_KEY, item.getCompanyNumber());
        logMap.put(CERTIFICATE_ID_LOG_KEY, item.getId());
        logMap.put(STATUS_LOG_KEY, CREATED);
        logMap.remove(MESSAGE);
        LOGGER.infoRequest(request, "certificate item created", logMap);
        return ResponseEntity.status(CREATED).body(createdCertificateItemDTO);
    }

    @GetMapping("${uk.gov.companieshouse.certificates.orders.api.certificates}/{id}")
    public ResponseEntity<Object> getCertificateItem(final @PathVariable String id,
                                                     final @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId)
    {
        Map<String, Object> logMap = createLoggingDataMap(requestId);
        logMap.put(CERTIFICATE_ID_LOG_KEY, id);        
        LOGGER.info("get certificate item request", logMap);
        logMap.remove(MESSAGE);
        Optional<CertificateItem> item = certificateItemService.getCertificateItemWithCosts(id);
        if(item.isPresent()) {
            final CertificateItemResponseDTO createdCertificateItemDTO = mapper.certificateItemToCertificateItemResponseDTO(item.get());
            logMap.put(COMPANY_NUMBER_LOG_KEY, createdCertificateItemDTO.getCompanyNumber());
            logMap.put(STATUS_LOG_KEY, OK);
            LOGGER.info("certificate item found", logMap);
            return ResponseEntity.status(OK).body(createdCertificateItemDTO);
        } else {
            String errorMsg = "certificate resource not found";
            final List<String> errors = new ArrayList<>();
            errors.add(errorMsg);
            logErrorsWithStatus(logMap, errors, NOT_FOUND);
            LOGGER.error(errorMsg, logMap);
            return ResponseEntity.status(NOT_FOUND).body(new ApiError(NOT_FOUND, errors));
        }
    }

    @PostMapping("${uk.gov.companieshouse.certificates.orders.api.initial}")
    public ResponseEntity<Object> initialCertificateItem(final @Valid @RequestBody CertificateItemInitialDTO certificateItemInitialDTO,
                                                         HttpServletRequest request,
                                                         final @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {

        Map<String, Object> logMap = createLoggingDataMap(requestId);
        LOGGER.infoRequest(request, "create certificate item request", logMap);

        String companyNumber = certificateItemInitialDTO.getCompanyNumber();
        try {
            final CompanyProfileResource companyProfile = companyService.getCompanyProfile(companyNumber);

            CertificateItem item = mapper.certificateItemDTOtoCertificateItem(certificateItemInitialDTO);
            item = mapper.enrichCertificateItem(EricHeaderHelper.getIdentity(request), companyProfile, item);
            item.setQuantity(1);
            item = certificateItemService.createCertificateItem(item);
            final CertificateItemResponseDTO createdCertificateItemDTO =
                    mapper.certificateItemToCertificateItemResponseDTO(item);

            logMap.put(USER_ID_LOG_KEY, item.getUserId());
            logMap.put(COMPANY_NUMBER_LOG_KEY, item.getCompanyNumber());
            logMap.put(CERTIFICATE_ID_LOG_KEY, item.getId());
            logMap.put(STATUS_LOG_KEY, CREATED);
            logMap.remove(MESSAGE);
            LOGGER.infoRequest(request, "certificate item created", logMap);

            return ResponseEntity.status(CREATED).body(createdCertificateItemDTO);

        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(BAD_REQUEST).body(new ApiError(ex.getStatus(),
                    Collections.singletonList(ex.getMessage())));
        }
    }

    @PatchMapping(path = "${uk.gov.companieshouse.certificates.orders.api.certificates}/{id}",
                  consumes = "application/merge-patch+json")
    public ResponseEntity<Object> updateCertificateItem(
            final @RequestBody JsonMergePatch mergePatchDocument,
            final @PathVariable("id") String id,
            final @RequestHeader(REQUEST_ID_HEADER_NAME) String requestId) {
        Map<String, Object> logMap = createLoggingDataMap(requestId);
        logMap.put(CERTIFICATE_ID_LOG_KEY, id);
        LOGGER.info("update certificate item request", logMap);
        logMap.remove(MESSAGE);

        final List<String> errors = patchItemRequestValidator.getValidationErrors(mergePatchDocument);
        if (!errors.isEmpty()) {
            logErrorsWithStatus(logMap, errors, BAD_REQUEST);
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

        //TODO: Fetch company profile if company status == null
        //item = mapper.enrichCertificateItem(EricHeaderHelper.getIdentity(request), companyProfile, item);

        final List<String> patchedErrors = patchItemRequestValidator.getValidationErrors(
                new CompanyCertificateInformation(
                        CompanyStatus.getEnumValue(itemRetrieved.getItemOptions().getCompanyStatus()),
                        patchedItem.getId(),
                        patchedItem.getItemOptions()));
        if (!patchedErrors.isEmpty()) {
            logErrorsWithStatus(logMap, patchedErrors, BAD_REQUEST);
            LOGGER.error("patched certificate item had validation errors", logMap);
            return ResponseEntity.status(BAD_REQUEST).body(new ApiError(BAD_REQUEST, patchedErrors));
        }

        logMap.put(PATCHED_COMPANY_NUMBER, patchedItem.getCompanyNumber());
        final CertificateItem savedItem = certificateItemService.saveCertificateItem(patchedItem);
        final CertificateItemResponseDTO savedItemDTO = mapper.certificateItemToCertificateItemResponseDTO(savedItem);

        logMap.put(STATUS_LOG_KEY, OK);
        LOGGER.info("update certificate item request completed", logMap);

        return ResponseEntity.ok().body(savedItemDTO);
    }
    
    /**
     * method to set up a map for logging purposes and add a value for the 
     * request id
     * @param requestId of the request
     * @return map of logging data
     */
    private Map<String, Object> createLoggingDataMap(final String requestId) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put(REQUEST_ID_LOG_KEY, requestId);
        return logMap;
    }

    /**
     * method to add errors and a bad request status to a map for logging
     * purposes
     * @param logMap the map of logging data
     * @param errors a list of errors
     */
    private void logErrorsWithStatus(Map<String, Object> logMap, 
    		final List<String> errors, HttpStatus status) {
        logMap.put(ERRORS_LOG_KEY, errors);
        logMap.put(STATUS_LOG_KEY, status);
    }

    private static class CompanyCertificateInformation implements RequestValidatable {

        private final CompanyStatus companyStatus;
        private final String certificateId;
        private final CertificateItemOptions itemOptions;

        public CompanyCertificateInformation(CompanyStatus companyStatus,
                String certificateId,
                CertificateItemOptions itemOptions) {
            this.companyStatus = companyStatus;
            this.certificateId = certificateId;
            this.itemOptions = itemOptions;
        }

        @Override
        public CompanyStatus getCompanyStatus() {
            return companyStatus;
        }

        @Override
        public String getCertificateId() {
            return certificateId;
        }

        @Override
        public CertificateItemOptions getItemOptions() {
            return itemOptions;
        }
    }
}
