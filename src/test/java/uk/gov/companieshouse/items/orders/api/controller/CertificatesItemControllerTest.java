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
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.service.CertificateItemService;
import uk.gov.companieshouse.items.orders.api.util.PatchMerger;

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
    private PatchMerger merger;

    @Test
    @DisplayName("Update request updates successfully")
    void updateUpdatesSuccessfully() {
        // Given
        when(service.getCertificateItemById(ITEM_ID)).thenReturn(Optional.of(item));

        // When
        final ResponseEntity<Void> response =
                controllerUnderTest.updateCertificateItem(patch, ITEM_ID, TOKEN_REQUEST_ID_VALUE);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
    }

    @Test
    @DisplayName("Update request reports resource not found")
    void updateReportsResourceNotFound() {
        final Exception thrown = assertThrows(ResourceNotFoundException.class,
                () -> controllerUnderTest.updateCertificateItem(patch, ITEM_ID, TOKEN_REQUEST_ID_VALUE));
        assertEquals("Resource not found!", thrown.getMessage());
    }

}
