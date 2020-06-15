package uk.gov.companieshouse.certificates.orders.api.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.TOKEN_REQUEST_ID_VALUE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.json.JsonMergePatch;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.certificates.orders.api.mapper.CertificateItemMapper;
import uk.gov.companieshouse.certificates.orders.api.service.CertificateItemService;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItem;
import uk.gov.companieshouse.certificates.orders.api.service.CompanyService;
import uk.gov.companieshouse.certificates.orders.api.util.PatchMerger;
import uk.gov.companieshouse.certificates.orders.api.validator.CreateItemRequestValidator;
import uk.gov.companieshouse.certificates.orders.api.validator.PatchItemRequestValidator;

/**
 * Unit tests the {@link CertificateItemsController} class.
 */
@ExtendWith(MockitoExtension.class)
public class CertificatesItemControllerTest {

    private static final String ITEM_ID = "CHS00000000000000001";

    @InjectMocks
    private CertificateItemsController controllerUnderTest;

    @Mock
    private JsonMergePatch patch;

    @Mock
    private CertificateItemService certificateItemService;

    @Mock
    private CertificateItem item;

    @Mock
    private CertificateItemDTO dto;

    @Mock
    private PatchMerger merger;

    @Mock
    private PatchItemRequestValidator validator;
    
    @Mock
    private CreateItemRequestValidator createValidator;

    @Mock
    private CertificateItemMapper mapper;

    @Mock
    private CompanyService companyService;
    
    @Mock
    private HttpServletRequest request;

    @Test
    @DisplayName("Update request updates successfully")
    void updateUpdatesSuccessfully() throws Exception {
        // Given
        when(certificateItemService.getCertificateItemById(ITEM_ID)).thenReturn(Optional.of(item));
        when(merger.mergePatch(patch, item, CertificateItem.class)).thenReturn(item);
        when(certificateItemService.saveCertificateItem(item)).thenReturn(item);
        when(mapper.certificateItemToCertificateItemDTO(item)).thenReturn(dto);

        // When
        final ResponseEntity<Object> response = controllerUnderTest.updateCertificateItem(patch, ITEM_ID,
                TOKEN_REQUEST_ID_VALUE);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(dto));
    }

    @Test
    @DisplayName("Update request reports resource not found")
    void updateReportsResourceNotFound() {
        when(certificateItemService.getCertificateItemById(ITEM_ID)).thenReturn(Optional.empty());
        final ResponseEntity<Object> response = controllerUnderTest.updateCertificateItem(patch, ITEM_ID,
                TOKEN_REQUEST_ID_VALUE);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        
    }
    
    @Test
    @DisplayName("Update certificate item supplied patch has validation errors")
    void updateCerificateItemPatchValidationErrors() {
        List<String> errors = new ArrayList<String>();
        errors.add("error");
        when(validator.getValidationErrors(patch)).thenReturn(errors);
        ResponseEntity<Object> response = controllerUnderTest.updateCertificateItem(patch, ITEM_ID,
                TOKEN_REQUEST_ID_VALUE);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }
    
    @Test
    @DisplayName("Update certificate item patched certificate has validation errors")
    void updateCertificateItemMergedValidationErrors() {
        List<String> errors = new ArrayList<String>();
        errors.add("error");
        when(certificateItemService.getCertificateItemById(ITEM_ID)).thenReturn(Optional.of(item));
        when(merger.mergePatch(patch, item, CertificateItem.class)).thenReturn(item);
        lenient().when(validator.getValidationErrors(item)).thenReturn(errors);
        
        ResponseEntity<Object> response = controllerUnderTest.updateCertificateItem(patch, ITEM_ID,
                TOKEN_REQUEST_ID_VALUE);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }
    
    @Test
    @DisplayName("Get certificate item resource returned")
    void getCertificateItemIsPresent() {
        when(certificateItemService.getCertificateItemWithCosts(ITEM_ID)).thenReturn(Optional.of(item));
        when(mapper.certificateItemToCertificateItemDTO(item)).thenReturn(dto);
        ResponseEntity<Object> response = controllerUnderTest.getCertificateItem(ITEM_ID, TOKEN_REQUEST_ID_VALUE);
        
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(dto));
    }
    
    @Test
    @DisplayName("Get certificate item resouce returns HTTP NOT FOUND")
    void getCertificateItemNotFound() {
        when(certificateItemService.getCertificateItemWithCosts(ITEM_ID)).thenReturn(Optional.empty());
        ResponseEntity<Object> response = controllerUnderTest.getCertificateItem(ITEM_ID, TOKEN_REQUEST_ID_VALUE);
        
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }
    
    @Test
    @DisplayName("Create certificate item is successful")
    void createCertificateItemSuccessful() {
        when(mapper.certificateItemDTOtoCertificateItem(dto)).thenReturn(item);
        when(item.getCompanyNumber()).thenReturn("number");
        when(companyService.getCompanyName("number")).thenReturn("name");
        when(certificateItemService.createCertificateItem(item)).thenReturn(item);
        when(mapper.certificateItemToCertificateItemDTO(item)).thenReturn(dto);
        
        ResponseEntity<Object> response = controllerUnderTest.createCertificateItem(dto, request, TOKEN_REQUEST_ID_VALUE);
        
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(response.getBody(), is(dto));
    }
    
    @Test
    @DisplayName("Create certificate item has validation errors")
    void createCertificateItemValidationErrors() {
        List<String> errors = new ArrayList<>();
        errors.add("error");
        when(createValidator.getValidationErrors(dto)).thenReturn(errors);
        ResponseEntity<Object> response = controllerUnderTest.createCertificateItem(dto, request, TOKEN_REQUEST_ID_VALUE);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

}
