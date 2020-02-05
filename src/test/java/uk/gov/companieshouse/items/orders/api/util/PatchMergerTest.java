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
import static uk.gov.companieshouse.items.orders.api.model.IncludeDobType.FULL;
import static uk.gov.companieshouse.items.orders.api.model.IncludeDobType.PARTIAL;

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
    private static final boolean INCLUDE_EMAIL_COPY = false;
    private static final boolean UPDATED_INCLUDE_EMAIL_COPY = true;
    private static final boolean INCLUDE_GOOD_STANDING_INFORMATION = true;
    private static final boolean UPDATED_INCLUDE_GOOD_STANDING_INFORMATION = false;

    private static final boolean INCLUDE_ADDRESS = true;
    private static final boolean UPDATED_INCLUDE_ADDRESS = false;
    private static final boolean INCLUDE_APPOINTMENT_DATE = false;
    private static final boolean UPDATED_INCLUDE_APPOINTMENT_DATE = true;
    private static final boolean INCLUDE_BASIC_INFORMATION = true;
    private static final boolean UPDATED_INCLUDE_BASIC_INFORMATION = false;
    private static final boolean INCLUDE_COUNTRY_OF_RESIDENCE = false;
    private static final boolean UPDATED_INCLUDE_COUNTRY_OF_RESIDENCE = true;
    private static final IncludeDobType INCLUDE_DOB_TYPE = PARTIAL;
    private static final IncludeDobType  UPDATED_INCLUDE_DOB_TYPE = FULL;
    private static final boolean INCLUDE_NATIONALITY= false;
    private static final boolean UPDATED_INCLUDE_NATIONALITY= true;
    private static final boolean INCLUDE_OCCUPATION = true;
    private static final boolean UPDATED_INCLUDE_OCCUPATION = false;

    private static final DirectorOrSecretaryDetails DIRECTOR_OR_SECRETARY_DETAILS;
    private static final DirectorOrSecretaryDetails UPDATED_DIRECTOR_OR_SECRETARY_DETAILS;

    static {
        DIRECTOR_OR_SECRETARY_DETAILS = new DirectorOrSecretaryDetails();
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeAddress(INCLUDE_ADDRESS);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeAppointmentDate(INCLUDE_APPOINTMENT_DATE);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeBasicInformation(INCLUDE_BASIC_INFORMATION);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeCountryOfResidence(INCLUDE_COUNTRY_OF_RESIDENCE);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeDobType(INCLUDE_DOB_TYPE);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeNationality(INCLUDE_NATIONALITY);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeOccupation(INCLUDE_OCCUPATION);
        UPDATED_DIRECTOR_OR_SECRETARY_DETAILS = new DirectorOrSecretaryDetails();
        UPDATED_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeAddress(UPDATED_INCLUDE_ADDRESS);
        UPDATED_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeAppointmentDate(UPDATED_INCLUDE_APPOINTMENT_DATE);
        UPDATED_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeBasicInformation(UPDATED_INCLUDE_BASIC_INFORMATION);
        UPDATED_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeCountryOfResidence(UPDATED_INCLUDE_COUNTRY_OF_RESIDENCE);
        UPDATED_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeDobType(UPDATED_INCLUDE_DOB_TYPE);
        UPDATED_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeNationality(UPDATED_INCLUDE_NATIONALITY);
        UPDATED_DIRECTOR_OR_SECRETARY_DETAILS.setIncludeOccupation(UPDATED_INCLUDE_OCCUPATION);
    }

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
        originalOptions.setDirectorDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        originalOptions.setIncludeCompanyObjectsInformation(INCLUDE_COMPANY_OBJECTS_INFORMATION);
        originalOptions.setIncludeEmailCopy(INCLUDE_EMAIL_COPY);
        originalOptions.setIncludeGoodStandingInformation(INCLUDE_GOOD_STANDING_INFORMATION);
        originalOptions.setSecretaryDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        original.setItemOptions(originalOptions);

        final CertificateItem delta = new CertificateItem();
        final CertificateItemOptions deltaOptions = new CertificateItemOptions();
        deltaOptions.setCollectionLocation(UPDATED_COLLECTION_LOCATION);
        deltaOptions.setContactNumber(UPDATED_CONTACT_NUMBER);
        deltaOptions.setDeliveryMethod(UPDATED_DELIVERY_METHOD);
        deltaOptions.setDeliveryTimescale(UPDATED_DELIVERY_TIMESCALE);
        deltaOptions.setDirectorDetails(UPDATED_DIRECTOR_OR_SECRETARY_DETAILS);
        deltaOptions.setIncludeCompanyObjectsInformation(UPDATED_INCLUDE_COMPANY_OBJECTS_INFORMATION);
        deltaOptions.setIncludeEmailCopy(UPDATED_INCLUDE_EMAIL_COPY);
        deltaOptions.setIncludeGoodStandingInformation(UPDATED_INCLUDE_GOOD_STANDING_INFORMATION);
        deltaOptions.setSecretaryDetails(UPDATED_DIRECTOR_OR_SECRETARY_DETAILS);
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
        assertThat(patched.getItemOptions().getIncludeEmailCopy(), is(UPDATED_INCLUDE_EMAIL_COPY));
        assertThat(patched.getItemOptions().getIncludeGoodStandingInformation(),
                is(UPDATED_INCLUDE_GOOD_STANDING_INFORMATION));

        final DirectorOrSecretaryDetails patchedDirector = patched.getItemOptions().getDirectorDetails();
        assertThat(patchedDirector.getIncludeAddress(), is(UPDATED_INCLUDE_ADDRESS));
        assertThat(patchedDirector.getIncludeAppointmentDate(), is(UPDATED_INCLUDE_APPOINTMENT_DATE));
        assertThat(patchedDirector.getIncludeBasicInformation(), is(UPDATED_INCLUDE_BASIC_INFORMATION));
        assertThat(patchedDirector.getIncludeCountryOfResidence(), is(UPDATED_INCLUDE_COUNTRY_OF_RESIDENCE));
        assertThat(patchedDirector.getIncludeDobType(), is(UPDATED_INCLUDE_DOB_TYPE));
        assertThat(patchedDirector.getIncludeNationality(), is(UPDATED_INCLUDE_NATIONALITY));
        assertThat(patchedDirector.getIncludeOccupation(), is(UPDATED_INCLUDE_OCCUPATION));

        final DirectorOrSecretaryDetails patchedSecretary = patched.getItemOptions().getSecretaryDetails();
        assertThat(patchedSecretary.getIncludeAddress(), is(UPDATED_INCLUDE_ADDRESS));
        assertThat(patchedSecretary.getIncludeAppointmentDate(), is(UPDATED_INCLUDE_APPOINTMENT_DATE));
        assertThat(patchedSecretary.getIncludeBasicInformation(), is(UPDATED_INCLUDE_BASIC_INFORMATION));
        assertThat(patchedSecretary.getIncludeCountryOfResidence(), is(UPDATED_INCLUDE_COUNTRY_OF_RESIDENCE));
        assertThat(patchedSecretary.getIncludeDobType(), is(UPDATED_INCLUDE_DOB_TYPE));
        assertThat(patchedSecretary.getIncludeNationality(), is(UPDATED_INCLUDE_NATIONALITY));
        assertThat(patchedSecretary.getIncludeOccupation(), is(UPDATED_INCLUDE_OCCUPATION));
    }

}
