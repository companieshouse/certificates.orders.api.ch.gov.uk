package uk.gov.companieshouse.items.orders.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.model.Item;
import uk.gov.companieshouse.items.orders.api.repository.CertificateItemRepository;

import java.time.LocalDateTime;

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
        final LocalDateTime intervalStart = LocalDateTime.now();

        // When
        serviceUnderTest.createCertificateItem(item);

        // Then
        final LocalDateTime intervalEnd = LocalDateTime.now();
        assertThat(item.getId(), is("CHS00000000000000001"));
        verifyCreationTimestampsWithinExecutionInterval(item, intervalStart, intervalEnd);
        verify(repository).save(item);
    }

    /**
     * Verifies that the item created at and updated at timestamps are within the expected interval
     * for item creation.
     * @param itemCreated the item created
     * @param intervalStart roughly the start of the test
     * @param intervalEnd roughly the end of the test
     */
    private void verifyCreationTimestampsWithinExecutionInterval(final Item itemCreated,
                                                                 final LocalDateTime intervalStart,
                                                                 final LocalDateTime intervalEnd) {
        assertThat(itemCreated.getCreatedAt().isAfter(intervalStart), is(true));
        assertThat(itemCreated.getCreatedAt().isBefore(intervalEnd), is(true));
        assertThat(itemCreated.getUpdatedAt().isAfter(intervalStart), is(true));
        assertThat(itemCreated.getUpdatedAt().isBefore(intervalEnd), is(true));
    }

}
