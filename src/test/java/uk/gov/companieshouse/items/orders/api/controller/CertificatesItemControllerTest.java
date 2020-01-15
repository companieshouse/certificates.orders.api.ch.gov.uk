package uk.gov.companieshouse.items.orders.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.mapper.CertificateItemMapper;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.service.CertificateItemService;
import uk.gov.companieshouse.items.orders.api.util.PatchMerger;
import uk.gov.companieshouse.items.orders.api.validator.PatchItemRequestValidator;

import javax.json.JsonMergePatch;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.items.orders.api.util.TestConstants.TOKEN_REQUEST_ID_VALUE;

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
    private CertificateItemService service;

    @Mock
    private CertificateItem item;

    @Mock
    private CertificateItemDTO dto;

    @Mock
    private PatchMerger merger;

    @Mock
    private PatchItemRequestValidator validator;

    @Mock
    private CertificateItemMapper mapper;

    @Test
    @DisplayName("Update request updates successfully")
    void updateUpdatesSuccessfully() {
        // Given
        when(service.getCertificateItemById(ITEM_ID)).thenReturn(Optional.of(item));
        when(merger.mergePatch(patch, item, CertificateItem.class)).thenReturn(item);
        when(service.saveCertificateItem(item)).thenReturn(item);
        when(mapper.certificateItemToCertificateItemDTO(item)).thenReturn(dto);

        // When
        final ResponseEntity<Object> response =
                controllerUnderTest.updateCertificateItem(patch, ITEM_ID, TOKEN_REQUEST_ID_VALUE);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(dto));
    }

    @Test
    @DisplayName("Update request reports resource not found")
    void updateReportsResourceNotFound() {
        final Exception thrown = assertThrows(ResourceNotFoundException.class,
                () -> controllerUnderTest.updateCertificateItem(patch, ITEM_ID, TOKEN_REQUEST_ID_VALUE));
        assertEquals("Resource not found!", thrown.getMessage());
    }

}
