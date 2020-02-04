package uk.gov.companieshouse.items.orders.api.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static uk.gov.companieshouse.items.orders.api.model.CertificateType.DISSOLUTION_LIQUIDATION;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryMethod.COLLECTION;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.SAME_DAY;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.STANDARD;

/**
 * Unit tests the {@link CreateItemRequestValidator} class.
 */
class CreateItemRequestValidatorTest {

    private CreateItemRequestValidator validatorUnderTest;

    @BeforeEach
    void setUp() {
        validatorUnderTest = new CreateItemRequestValidator();
    }

    @Test
    @DisplayName("ID is mandatory")
    void idIsMandatory() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        item.setId("1");

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        //assertThat(errors, is(empty()));
        assertThat(errors, contains("id: must be null in a create item request"));
    }

    @Test
    @DisplayName("Collection location is optional by default")
    void collectionLocationIsOptionalByDefault() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Collection location is mandatory for collection delivery method")
    void collectionLocationIsMandatoryForCollectionDeliveryMethod() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryMethod(COLLECTION);
        item.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, contains("collection_location: must not be null when delivery method is collection"));
    }

    @Test
    @DisplayName("Company objects and good standing info may be requested by default")
    void companyObjectsAndGoodStandingInfoMayBeRequestedByDefault() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setIncludeCompanyObjectsInformation(true);
        options.setIncludeGoodStandingInformation(true);
        item.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Neither company objects nor good standing info should be requested for dissolution liquidation")
    void companyObjectsGoodStandingInfoMustNotBeRequestedForDissolutionLiquidation() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertificateType(DISSOLUTION_LIQUIDATION);
        options.setIncludeCompanyObjectsInformation(true);
        options.setIncludeGoodStandingInformation(true);
        item.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, containsInAnyOrder(
                "include_company_objects_information: must not be true when certificate type is dissolution_liquidation",
                "include_good_standing_information: must not be true when certificate type is dissolution_liquidation"));
    }

    @Test
    @DisplayName("(Only) include email copy for same day delivery timescale")
    void includeEmailCopyForSameDayDeliveryTimescale() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryTimescale(SAME_DAY);
        options.setIncludeEmailCopy(true);
        item.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Do not include email copy for standard delivery timescale")
    void doNotIncludeEmailCopyForStandardDeliveryTimescale() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryTimescale(STANDARD);
        options.setIncludeEmailCopy(true);
        item.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, contains(
                "include_email_copy: can only be true when delivery timescale is same_day"));
    }
}
