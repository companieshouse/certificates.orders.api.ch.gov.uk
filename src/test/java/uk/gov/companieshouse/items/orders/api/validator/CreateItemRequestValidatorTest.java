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
    @DisplayName("Company objects information info may be requested by default")
    void companyObjectsInfoMayBeRequestedByDefault() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setIncludeCompanyObjectsInformation(true);
        item.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Company objects info must not be requested for dissolution liquidation")
    void companyObjectsInfoMustNotBeRequestedForDissolutionLiquidation() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertificateType(DISSOLUTION_LIQUIDATION);
        options.setIncludeCompanyObjectsInformation(true);
        item.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, contains(
                "include_company_objects_information: must not be true when certificate type is dissolution_liquidation"));
    }
}
