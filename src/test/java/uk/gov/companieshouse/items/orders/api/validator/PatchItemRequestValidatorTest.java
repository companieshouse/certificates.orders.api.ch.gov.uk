package uk.gov.companieshouse.items.orders.api.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import uk.gov.companieshouse.items.orders.api.config.ApplicationConfiguration;
import uk.gov.companieshouse.items.orders.api.dto.PatchValidationCertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;
import uk.gov.companieshouse.items.orders.api.util.TestMergePatchFactory;

import javax.json.JsonMergePatch;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

/**
 * Unit tests the {@link PatchItemRequestValidator} class.
 */
@ExtendWith(SpringExtension.class)
@SpringJUnitConfig(PatchItemRequestValidatorTest.Config.class)
class PatchItemRequestValidatorTest {

    @Configuration
    static class Config {
        @Bean
        public ObjectMapper objectMapper() {
            return new ApplicationConfiguration().objectMapper();
        }

        @Bean
        public Validator validator() {
            final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            return factory.getValidator();
        }

        @Bean
        public PatchItemRequestValidator patchItemRequestValidator() {
            return new PatchItemRequestValidator(objectMapper(), validator());
        }

        @Bean
        TestMergePatchFactory patchFactory() {
            return new TestMergePatchFactory(objectMapper());
        }
    }

    private static final int TOKEN_QUANTITY = 2;
    private static final int INVALID_QUANTITY = 0;
    private static final String TOKEN_STRING = "TOKEN VALUE";
    static final Map<String, String> TOKEN_VALUES = new HashMap<>();
    private static final ItemCosts TOKEN_ITEM_COSTS = new ItemCosts();

    @Autowired
    private PatchItemRequestValidator validatorUnderTest;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private TestMergePatchFactory patchFactory;

    private PatchValidationCertificateItemDTO itemUpdate;

    @BeforeEach
    void setUp() {
        itemUpdate = new PatchValidationCertificateItemDTO();
    }

    @Test
    @DisplayName("No errors")
    void getValidationErrorsReturnsNoErrors() throws IOException {
        // Given
        itemUpdate.setQuantity(TOKEN_QUANTITY);
        final JsonMergePatch patch = patchFactory.patchFromPojo(itemUpdate);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patch);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("ID is read only")
    void getValidationErrorsRejectsReadOnlyId() throws IOException {
        itemUpdate.setId(TOKEN_STRING);
        assertFieldMustBeNullErrorProduced("id");
    }

    @Test
    @DisplayName("Description is read only")
    void getValidationErrorsRejectsReadOnlyDescription() throws IOException {
        itemUpdate.setDescription(TOKEN_STRING);
        assertFieldMustBeNullErrorProduced("description");
    }

    @Test
    @DisplayName("Description identifier is read only")
    void getValidationErrorsRejectsReadOnlyDescriptionIdentifier() throws IOException {
        itemUpdate.setDescriptionIdentifier(TOKEN_STRING);
        assertFieldMustBeNullErrorProduced("descriptionIdentifier");
    }

    @Test
    @DisplayName("Description values are read only")
    void getValidationErrorsRejectsReadOnlyDescriptionValues() throws IOException {
        itemUpdate.setDescriptionValues(TOKEN_VALUES);
        assertFieldMustBeNullErrorProduced("descriptionValues");
    }

    @Test
    @DisplayName("Item costs are read only")
    void getValidationErrorsRejectsReadOnlyItemCosts() throws IOException {
        itemUpdate.setItemCosts(TOKEN_ITEM_COSTS);
        assertFieldMustBeNullErrorProduced("itemCosts");
    }

    @Test
    @DisplayName("Kind is read only")
    void getValidationErrorsRejectsReadOnlyKind() throws IOException {
        itemUpdate.setKind(TOKEN_STRING);
        assertFieldMustBeNullErrorProduced("kind");
    }

    @Test
    @DisplayName("Postal delivery is read only")
    void getValidationErrorsRejectsReadOnlyPostalDelivery() throws IOException {
        itemUpdate.setKind(TOKEN_STRING);
        assertFieldMustBeNullErrorProduced("kind");
    }

    @Test
    @DisplayName("Quantity must be greater than 0")
    void getValidationErrorsRejectsZeroQuantity() throws IOException {
        // Given
        itemUpdate.setQuantity(INVALID_QUANTITY);
        final JsonMergePatch patch = patchFactory.patchFromPojo(itemUpdate);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patch);

        // Then
        assertThat(errors, contains("quantity: must be greater than or equal to 1"));
    }

    // TODO PCI-435 Test multiple read only fields, also what happens with invalid input leading to IOException?

    /**
     * Utility method that asserts that the validator produces a "<field name>: must be null"
     * error message.
     * @param fieldName the name of the field for which the error is expected
     * @throws IOException should something unexpected happen
     */
    private void assertFieldMustBeNullErrorProduced(final String fieldName) throws IOException {
        // Given
        final JsonMergePatch patch = patchFactory.patchFromPojo(itemUpdate);
        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(patch);
        // Then
        assertThat(errors, contains(fieldName + ": must be null"));
    }

}
