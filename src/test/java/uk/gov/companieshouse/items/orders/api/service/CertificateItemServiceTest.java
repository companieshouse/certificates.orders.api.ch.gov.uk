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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests the {@link CertificateItemService} class.
 */
@ExtendWith(MockitoExtension.class)
class CertificateItemServiceTest {

    /** The value produced by the sequence that drives the item ID generation. */
    private static final long NEXT_ID_SEQUENCE_VALUE = 1L;

    /** The next ID value expected to result given that the next sequence value is {@link #NEXT_ID_SEQUENCE_VALUE}. */
    private static final String EXPECTED_ID_VALUE = "CHS00000000000000001";

    @InjectMocks
    private CertificateItemService serviceUnderTest;

    @Mock
    private CertificateItemRepository repository;

    @Mock
    private SequenceGeneratorService generator;

    @Mock
    private DescriptionProviderService descriptions;

    @Mock
    private CertificateCostCalculatorService calculator;

    @Test
    void getNextIdGetsNextId() {

        // Given
        when(generator.generateSequence(anyString())).thenReturn(NEXT_ID_SEQUENCE_VALUE);

        // When and Then
        assertThat(serviceUnderTest.getNextId(), is(EXPECTED_ID_VALUE));
    }

    @Test
    void createCertificateItemPopulatesAndSavesItem() {

        // Given
        when(generator.generateSequence(anyString())).thenReturn(NEXT_ID_SEQUENCE_VALUE);
        final CertificateItem item = new CertificateItem();
        final LocalDateTime intervalStart = LocalDateTime.now();
        item.setQuantity(1);

        // When
        serviceUnderTest.createCertificateItem(item);

        // Then
        final LocalDateTime intervalEnd = LocalDateTime.now();
        assertThat(item.getId(), is(EXPECTED_ID_VALUE));
        verifyCreationTimestampsWithinExecutionInterval(item, intervalStart, intervalEnd);
        verify(repository).save(item);
    }

    @Test
    void getCertificateItemRetrievesItem() {
        // When
        serviceUnderTest.getCertificateItemById(EXPECTED_ID_VALUE);

        // Then
        verify(repository).findById(EXPECTED_ID_VALUE);
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
        assertThat(itemCreated.getCreatedAt().isAfter(intervalStart) ||
                   itemCreated.getCreatedAt().isEqual(intervalStart), is(true));
        assertThat(itemCreated.getCreatedAt().isBefore(intervalEnd) ||
                   itemCreated.getCreatedAt().isEqual(intervalEnd), is(true));
        assertThat(itemCreated.getUpdatedAt().isAfter(intervalStart) ||
                   itemCreated.getUpdatedAt().isEqual(intervalStart), is(true));
        assertThat(itemCreated.getUpdatedAt().isBefore(intervalEnd) ||
                   itemCreated.getUpdatedAt().isEqual(intervalEnd), is(true));
    }

}
