package uk.gov.companieshouse.items.orders.api.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Unit tests the {@link NonNullPropertyCopier} class.
 */
class NonNullPropertyCopierTest {

    private static final String ORIGINAL_COMPANY_NUMBER = "1234";
    private static final String CORRECTED_COMPANY_NUMBER = "1235";
    private static final boolean ORIGINAL_POSTAL_DELIVERY = true;
    private static final int ORIGINAL_QUANTITY = 20;

    private static final String ORIGINAL_ADDITIONAL_INFO = "We have our reasons.";
    private static final boolean ORIGINAL_CERT_ACC = true;
    private static final boolean ORIGINAL_CERT_ARTS = true;

    private static final String CORRECTED_ADDITIONAL_INFO = "We have our top secret reasons.";
    private static final boolean CORRECTED_CERT_ACC = false;

    private NonNullPropertyCopier copierUnderTest;

    @BeforeEach
    private void setUp() {
        copierUnderTest = new NonNullPropertyCopier();
    }

    @Test
    @DisplayName("Unpopulated source string property does not overwrite populated target field")
    void unpopulatedSourceStringLeavesTargetIntact() throws InvocationTargetException, IllegalAccessException {
        // Given
        final CertificateItem original = new CertificateItem();
        original.setCompanyNumber(ORIGINAL_COMPANY_NUMBER);
        final CertificateItem empty = new CertificateItem();

        // When
        copierUnderTest.copyProperties(original, empty);

        // Then
        assertThat(original.getCompanyNumber(), is(ORIGINAL_COMPANY_NUMBER));
    }

    @Test
    @DisplayName("Unpopulated source boolean property does not overwrite populated target field")
    void unpopulatedSourceBooleanLeavesTargetIntact() throws InvocationTargetException, IllegalAccessException {
        // Given
        final CertificateItem original = new CertificateItem();
        original.setPostalDelivery(ORIGINAL_POSTAL_DELIVERY);
        final CertificateItem empty = new CertificateItem();

        // When
        copierUnderTest.copyProperties(original, empty);

        // Then
        assertThat(original.isPostalDelivery(), is(ORIGINAL_POSTAL_DELIVERY));
    }

    @Test
    @DisplayName("Unpopulated source integer property does not overwrite populated target field")
    void unpopulatedIntegerPropertyDoesNotOverwrite() throws InvocationTargetException, IllegalAccessException {
        // Given
        final CertificateItem original = new CertificateItem();
        original.setQuantity(ORIGINAL_QUANTITY);
        final CertificateItem empty = new CertificateItem();

        // When
        copierUnderTest.copyProperties(original, empty);

        // Then
        assertThat(original.getQuantity(), is(ORIGINAL_QUANTITY));
    }

    @Test
    @DisplayName("Root level property is propagated correctly")
    void sourceRootLevelPropertyPropagated() throws InvocationTargetException, IllegalAccessException {
        // Given
        final CertificateItem original = new CertificateItem();
        original.setCompanyNumber(ORIGINAL_COMPANY_NUMBER);
        final CertificateItem delta = new CertificateItem();
        delta.setCompanyNumber(CORRECTED_COMPANY_NUMBER);

        // When
        copierUnderTest.copyProperties(original, delta);

        // Then
        assertThat(original.getCompanyNumber(), is(CORRECTED_COMPANY_NUMBER));
    }

    @Test
    @DisplayName("Nested level properties propagated correctly")
    void sourceNestedLevelPropertiesPropagated() throws InvocationTargetException, IllegalAccessException {
        // Given
        final CertificateItem original = new CertificateItem();
        final CertificateItemOptions originalOptions = new CertificateItemOptions();
        originalOptions.setAdditionalInformation(ORIGINAL_ADDITIONAL_INFO);
        originalOptions.setCertAcc(ORIGINAL_CERT_ACC);
        originalOptions.setCertArts(ORIGINAL_CERT_ARTS);
        original.setItemOptions(originalOptions);

        final CertificateItem delta = new CertificateItem();
        final CertificateItemOptions deltaOptions = new CertificateItemOptions();
        deltaOptions.setAdditionalInformation(CORRECTED_ADDITIONAL_INFO);
        deltaOptions.setCertAcc(CORRECTED_CERT_ACC);
        delta.setItemOptions(deltaOptions);

        // When
        copierUnderTest.copyProperties(original, delta);

        // Then
        assertThat(original.getItemOptions().getAdditionalInformation(), is(CORRECTED_ADDITIONAL_INFO));
        assertThat(original.getItemOptions().isCertAcc(), is(CORRECTED_CERT_ACC));
        assertThat(original.getItemOptions().isCertArts(), is(ORIGINAL_CERT_ARTS));
    }

}
