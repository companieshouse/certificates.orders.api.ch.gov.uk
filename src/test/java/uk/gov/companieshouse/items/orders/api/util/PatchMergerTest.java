package uk.gov.companieshouse.items.orders.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import uk.gov.companieshouse.items.orders.api.config.ApplicationConfiguration;
import uk.gov.companieshouse.items.orders.api.model.*;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.gov.companieshouse.items.orders.api.model.CertificateType.INCORPORATION;
import static uk.gov.companieshouse.items.orders.api.model.CollectionLocation.BELFAST;
import static uk.gov.companieshouse.items.orders.api.model.CollectionLocation.CARDIFF;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryMethod.COLLECTION;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryMethod.POSTAL;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.SAME_DAY;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.STANDARD;

/**
 * Unit tests the {@link PatchMerger} class.
 */
@ExtendWith(SpringExtension.class)
@SpringJUnitConfig(PatchMergerTest.Config.class)
class PatchMergerTest {

    @Configuration
    static class Config {
        @Bean
        public ObjectMapper objectMapper() {
            return new ApplicationConfiguration().objectMapper();
        }

        @Bean
        PatchMerger patchMerger() {
            return new PatchMerger(objectMapper());
        }

        @Bean
        TestMergePatchFactory patchFactory() {
            return new TestMergePatchFactory(objectMapper());
        }
    }

    private static final String ORIGINAL_COMPANY_NUMBER = "1234";
    private static final String CORRECTED_COMPANY_NUMBER = "1235";
    private static final boolean ORIGINAL_POSTAL_DELIVERY = true;
    private static final boolean CORRECTED_POSTAL_DELIVERY = false;
    private static final int ORIGINAL_QUANTITY = 20;
    private static final int CORRECTED_QUANTITY = 2;

    private static final CertificateType CERTIFICATE_TYPE = INCORPORATION;
    private static final CollectionLocation COLLECTION_LOCATION = BELFAST;
    private static final CollectionLocation UPDATED_COLLECTION_LOCATION = CARDIFF;
    private static final String CONTACT_NUMBER = "+44 1234 123456";
    private static final String UPDATED_CONTACT_NUMBER = "+44 1234 123457";
    private static final DeliveryMethod DELIVERY_METHOD = POSTAL;
    private static final DeliveryMethod UPDATED_DELIVERY_METHOD = COLLECTION;
    private static final DeliveryTimescale DELIVERY_TIMESCALE = STANDARD;
    private static final DeliveryTimescale UPDATED_DELIVERY_TIMESCALE = SAME_DAY;
    private static final boolean INCLUDE_COMPANY_OBJECTS_INFORMATION = true;
    private static final boolean UPDATED_INCLUDE_COMPANY_OBJECTS_INFORMATION = false;

    @Autowired
    private PatchMerger patchMergerUnderTest;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private TestMergePatchFactory patchFactory;

    @Test
    @DisplayName("Unpopulated source string property does not overwrite populated target field")
    void unpopulatedSourceStringLeavesTargetIntact() throws IOException {
        // Given
        final CertificateItem original = new CertificateItem();
        original.setCompanyNumber(ORIGINAL_COMPANY_NUMBER);
        final CertificateItem empty = new CertificateItem();

        // When
        final CertificateItem patched =
                patchMergerUnderTest.mergePatch(patchFactory.patchFromPojo(empty), original, CertificateItem.class);

        // Then
        assertThat(patched.getCompanyNumber(), is(ORIGINAL_COMPANY_NUMBER));
    }

    @Test
    @DisplayName("Unpopulated source boolean property does not overwrite populated target field")
    void unpopulatedSourceBooleanLeavesTargetIntact() throws IOException {
        // Given
        final CertificateItem original = new CertificateItem();
        original.setPostalDelivery(ORIGINAL_POSTAL_DELIVERY);
        final CertificateItem empty = new CertificateItem();

        // When
        final CertificateItem patched =
                patchMergerUnderTest.mergePatch(patchFactory.patchFromPojo(empty), original, CertificateItem.class);

        // Then
        assertThat(patched.isPostalDelivery(), is(ORIGINAL_POSTAL_DELIVERY));
    }

    @Test
    @DisplayName("Unpopulated source integer property does not overwrite populated target field")
    void unpopulatedIntegerPropertyDoesNotOverwrite() throws IOException {
        // Given
        final CertificateItem original = new CertificateItem();
        original.setQuantity(ORIGINAL_QUANTITY);
        final CertificateItem empty = new CertificateItem();

        // When
        final CertificateItem patched =
                patchMergerUnderTest.mergePatch(patchFactory.patchFromPojo(empty), original, CertificateItem.class);

        // Then
        assertThat(patched.getQuantity(), is(ORIGINAL_QUANTITY));
    }

    @Test
    @DisplayName("Root level string property is propagated correctly")
    void sourceRootLevelStringPropertyPropagated() throws IOException  {
        // Given
        final CertificateItem original = new CertificateItem();
        original.setCompanyNumber(ORIGINAL_COMPANY_NUMBER);
        final CertificateItem delta = new CertificateItem();
        delta.setCompanyNumber(CORRECTED_COMPANY_NUMBER);

        // When
        final CertificateItem patched =
                patchMergerUnderTest.mergePatch(patchFactory.patchFromPojo(delta), original, CertificateItem.class);

        // Then
        assertThat(patched.getCompanyNumber(), is(CORRECTED_COMPANY_NUMBER));
    }

    @Test
    @DisplayName("Root level boolean property is propagated correctly")
    void sourceRootLevelBooleanPropertyPropagated() throws IOException  {
        // Given
        final CertificateItem original = new CertificateItem();
        original.setPostalDelivery(ORIGINAL_POSTAL_DELIVERY);
        final CertificateItem delta = new CertificateItem();
        delta.setPostalDelivery(CORRECTED_POSTAL_DELIVERY);

        // When
        final CertificateItem patched =
                patchMergerUnderTest.mergePatch(patchFactory.patchFromPojo(delta), original, CertificateItem.class);

        // Then
        assertThat(patched.isPostalDelivery(), is(CORRECTED_POSTAL_DELIVERY));
    }

    @Test
    @DisplayName("Root level integer property is propagated correctly")
    void sourceRootLevelIntegerPropertyPropagated() throws IOException  {
        // Given
        final CertificateItem original = new CertificateItem();
        original.setQuantity(ORIGINAL_QUANTITY);
        final CertificateItem delta = new CertificateItem();
        delta.setQuantity(CORRECTED_QUANTITY);

        // When
        final CertificateItem patched =
                patchMergerUnderTest.mergePatch(patchFactory.patchFromPojo(delta), original, CertificateItem.class);

        // Then
        assertThat(patched.getQuantity(), is(CORRECTED_QUANTITY));
    }

    @Test
    @DisplayName("Nested level properties are propagated correctly")
    void sourceNestedLevelPropertiesPropagated() throws IOException {
        // Given
        final CertificateItem original = new CertificateItem();
        final CertificateItemOptions originalOptions = new CertificateItemOptions();
        originalOptions.setCertificateType(CERTIFICATE_TYPE);
        originalOptions.setCollectionLocation(COLLECTION_LOCATION);
        originalOptions.setContactNumber(CONTACT_NUMBER);
        originalOptions.setDeliveryMethod(DELIVERY_METHOD);
        originalOptions.setDeliveryTimescale(DELIVERY_TIMESCALE);
        originalOptions.setIncludeCompanyObjectsInformation(INCLUDE_COMPANY_OBJECTS_INFORMATION);
        original.setItemOptions(originalOptions);

        final CertificateItem delta = new CertificateItem();
        final CertificateItemOptions deltaOptions = new CertificateItemOptions();
        deltaOptions.setCollectionLocation(UPDATED_COLLECTION_LOCATION);
        deltaOptions.setContactNumber(UPDATED_CONTACT_NUMBER);
        deltaOptions.setDeliveryMethod(UPDATED_DELIVERY_METHOD);
        deltaOptions.setDeliveryTimescale(UPDATED_DELIVERY_TIMESCALE);
        deltaOptions.setIncludeCompanyObjectsInformation(UPDATED_INCLUDE_COMPANY_OBJECTS_INFORMATION);
        delta.setItemOptions(deltaOptions);

        // When
        final CertificateItem patched =
                patchMergerUnderTest.mergePatch(patchFactory.patchFromPojo(delta), original, CertificateItem.class);

        // Then
        assertThat(patched.getItemOptions().getCertificateType(), is(CERTIFICATE_TYPE));
        assertThat(patched.getItemOptions().getCollectionLocation(), is(UPDATED_COLLECTION_LOCATION));
        assertThat(patched.getItemOptions().getContactNumber(), is(UPDATED_CONTACT_NUMBER));
        assertThat(patched.getItemOptions().getDeliveryMethod(), is(UPDATED_DELIVERY_METHOD));
        assertThat(patched.getItemOptions().getDeliveryTimescale(), is(UPDATED_DELIVERY_TIMESCALE));
        assertThat(patched.getItemOptions().getIncludeCompanyObjectsInformation(),
                is(UPDATED_INCLUDE_COMPANY_OBJECTS_INFORMATION));
    }

}
