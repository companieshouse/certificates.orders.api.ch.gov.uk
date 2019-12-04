package uk.gov.companieshouse.items.orders.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.repository.CertificateItemRepository;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Unit tests the {@link CertificateItemService} class.
 */
@ExtendWith(MockitoExtension.class)
class CertificateItemServiceUnitTest {

    @InjectMocks
    private CertificateItemService serviceUnderTest;

    @Mock
    private CertificateItemRepository repository;

    @Mock
    private SequenceGeneratorService generator;

    @Test
    void getNextIdGetsNextId() {

        // Given
        when(generator.generateSequence(anyString())).thenReturn(1L);

        // When and Then
        assertThat(serviceUnderTest.getNextId(), is("CHS00000000000000001"));
    }

    @Test
    void createCertificateItemPopulatesAndSavesItem() {

        // Given
        when(generator.generateSequence(anyString())).thenReturn(1L);
        final CertificateItem item = new CertificateItem();

        // When
        serviceUnderTest.createCertificateItem(item);

        // Then
        assertThat(item.getId(), is("CHS00000000000000001"));
        verify(repository).save(item);
    }


}
