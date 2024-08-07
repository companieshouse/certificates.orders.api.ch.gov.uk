package uk.gov.companieshouse.certificates.orders.api.service;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItem;
import uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale;
import uk.gov.companieshouse.certificates.orders.api.model.Item;
import uk.gov.companieshouse.certificates.orders.api.model.ItemCosts;
import uk.gov.companieshouse.certificates.orders.api.repository.CertificateItemRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests the {@link CertificateItemService} class.
 */
@ExtendWith(MockitoExtension.class)
class CertificateItemServiceTest {

    private static final String ID = "CRT-123456-123456";

    private static final String ITEM_SOUGHT_ID_VALUE = "CRT-111111-222222";

    private static final String DISCOUNT_APPLIED = "1";
    private static final String ITEM_COST = "2";
    private static final String POSTAGE_COST = "0";
    private static final String CALCULATED_COST = "4";
    private static final String TOTAL_ITEM_COST = "8";

    @InjectMocks
    private CertificateItemService serviceUnderTest;

    @Mock
    private CertificateItemRepository repository;

    @Mock
    private DescriptionProviderService descriptions;

    @Mock
    private CertificateCostCalculatorService calculator;

    @Mock
    private EtagGeneratorService etagGenerator;

    @Mock
    private IdGeneratorService idGeneratorService;

    @Mock
    private LinksGeneratorService linksGenerator;

    @Test
    @DisplayName("createCertificateItem creates and saves item with id, timestamps, etag and links, returns item with costs")
    void createCertificateItemPopulatesAndSavesItem() {

        // Given
        when(idGeneratorService.autoGenerateId()).thenReturn(ID);
        final CertificateItem item = mockUpCostsCalculation();
        when(repository.save(item)).thenReturn(item);

        final LocalDateTime intervalStart = LocalDateTime.now();

        // When
        serviceUnderTest.createCertificateItem(item, false);

        // Then
        final LocalDateTime intervalEnd = LocalDateTime.now();
        assertThat(item.getId(), is(ID));
        verifyCreationTimestampsWithinExecutionInterval(item, intervalStart, intervalEnd);
        verify(repository).save(item);
        verifyCostsFields(item);
        verify(etagGenerator).generateEtag();
        verify(linksGenerator).generateLinks(ID);
    }

    @Test
    @DisplayName("saveCertificateItem saves item, updates updated at timestamp, generates etag, returns item with costs")
    void saveCertificateItemUpdatesCertificateItem() {

        // Given
        final CertificateItem item = mockUpCostsCalculation();
        when(repository.save(item)).thenReturn(item);

        final LocalDateTime intervalStart = LocalDateTime.now();
        item.setCreatedAt(intervalStart);

        // When
        serviceUnderTest.saveCertificateItem(item, false);

        // Then
        final LocalDateTime intervalEnd = LocalDateTime.now();
        verify(repository).save(item);
        verifyCostsFields(item);
        verifyUpdatedAtTimestampWithinExecutionInterval(item, intervalStart, intervalEnd);
        verify(etagGenerator).generateEtag();
    }

    @Test
    @DisplayName("getCertificateItemWithCosts retrieves item with item costs")
    void getCertificateItemWithCostsRetrievesItemWithCosts() {

        // Given
        final CertificateItem item = mockUpCostsCalculation();
        when(repository.findById(ITEM_SOUGHT_ID_VALUE)).thenReturn(Optional.of(item));

        // When
        final Optional<CertificateItem> itemRetrieved = serviceUnderTest.getCertificateItemWithCosts(ITEM_SOUGHT_ID_VALUE, false);

        // Then
        verify(repository).findById(ITEM_SOUGHT_ID_VALUE);
        assertThat(itemRetrieved.isPresent(), is(true));
        verifyCostsFields(itemRetrieved.get());
        verify(etagGenerator, never()).generateEtag();
    }

    @Test
    @DisplayName("getCertificateItemById retrieves item without item costs")
    void getCertificateItemByIdRetrievesItem() {

        // Given
        final CertificateItem item = new CertificateItem();
        when(repository.findById(ITEM_SOUGHT_ID_VALUE)).thenReturn(Optional.of(item));

        // When
        final Optional<CertificateItem> itemRetrieved = serviceUnderTest.getCertificateItemById(ITEM_SOUGHT_ID_VALUE);

        // Then
        verify(repository).findById(ITEM_SOUGHT_ID_VALUE);
        assertThat(itemRetrieved.isPresent(), is(true));
        assertThat(itemRetrieved.get().getItemCosts(), is(nullValue()));
        verify(etagGenerator, never()).generateEtag();
    }

    @Test
    @DisplayName("getCertificateItemById handles failure to find item smoothly")
    void getCertificateItemHandlesFailureToFindItemSmoothly() {

        // Given
        when(repository.findById(ITEM_SOUGHT_ID_VALUE)).thenReturn(Optional.empty());

        // When
        final Optional<CertificateItem> item = serviceUnderTest.getCertificateItemById(ITEM_SOUGHT_ID_VALUE);
        assertThat(item.isPresent(), is(false));
    }

    /**
     * Utility method that sets up a mock costs calculation to help verify it is handled correctly
     * by the CertificateItemService.
     * @return an item populated by the outcome of a mocked calculator costs calculation
     */
    private CertificateItem mockUpCostsCalculation() {
        final CertificateItem item = new CertificateItem();
        item.setQuantity(1);
        final List<ItemCosts> costs = new ArrayList<>();
        final ItemCosts cost = new ItemCosts();
        cost.setDiscountApplied(DISCOUNT_APPLIED);
        cost.setItemCost(ITEM_COST);
        cost.setCalculatedCost(CALCULATED_COST);
        costs.add(cost);
        when(calculator.calculateCosts(anyInt(), eq(DeliveryTimescale.STANDARD), eq(false))).thenReturn(
                new CertificateCostCalculation(costs, POSTAGE_COST, TOTAL_ITEM_COST));
        return item;
    }

    /**
     * Verifies that the item costs have been populated as expected.
     * @param item the item
     */
    private void verifyCostsFields(final Item item) {
        final List<ItemCosts> costs = item.getItemCosts();
        final ItemCosts cost = costs.getFirst();
        assertThat(cost, Matchers.is(notNullValue()));
        assertThat(cost.getDiscountApplied(), Matchers.is(DISCOUNT_APPLIED));
        assertThat(cost.getItemCost(), Matchers.is(ITEM_COST));
        assertThat(cost.getCalculatedCost(), Matchers.is(CALCULATED_COST));
        assertThat(item.getPostageCost(), is(POSTAGE_COST));
        assertThat(item.getTotalItemCost(), is(TOTAL_ITEM_COST));
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

    /**
     * Verifies that the itemUpdated updated at timestamp is within the expected interval
     * for itemUpdated update.
     * @param itemUpdated the itemUpdated updated
     * @param intervalStart roughly the start of the test
     * @param intervalEnd roughly the end of the test
     */
    private void verifyUpdatedAtTimestampWithinExecutionInterval(final Item itemUpdated,
                                                                 final LocalDateTime intervalStart,
                                                                 final LocalDateTime intervalEnd) {

        assertThat(itemUpdated.getUpdatedAt().isAfter(itemUpdated.getCreatedAt()) ||
                   itemUpdated.getUpdatedAt().isEqual(itemUpdated.getCreatedAt()), is(true));

        assertThat(itemUpdated.getUpdatedAt().isAfter(intervalStart) ||
                itemUpdated.getUpdatedAt().isEqual(intervalStart), is(true));
        assertThat(itemUpdated.getUpdatedAt().isBefore(intervalEnd) ||
                itemUpdated.getUpdatedAt().isEqual(intervalEnd), is(true));
    }

}
