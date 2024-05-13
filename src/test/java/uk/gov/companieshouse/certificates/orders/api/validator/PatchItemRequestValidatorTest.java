package uk.gov.companieshouse.certificates.orders.api.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.JsonMergePatch;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.config.ApplicationConfiguration;
import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptions;
import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptionsConfig;
import uk.gov.companieshouse.certificates.orders.api.controller.ApiErrors;
import uk.gov.companieshouse.certificates.orders.api.dto.PatchValidationCertificateItemDTO;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.ItemCosts;
import uk.gov.companieshouse.certificates.orders.api.util.FieldNameConverter;
import uk.gov.companieshouse.certificates.orders.api.util.TestMergePatchFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

/**
 * Unit tests the {@link PatchItemRequestValidator} class.
 */

@SpringBootTest
@TestPropertySource(
        properties = """
    lp.certificate.orders.enabled=false
    llp.certificate.orders.enabled=false
    liquidated.company.certificate.enabled=false
    administrator.company.certificate.enabled=false
  """
)
class PatchItemRequestValidatorTest {
    @Import({CertificateOptionsValidatorConfig.class, FeatureOptionsConfig.class})
    @Configuration
    public static class Config {
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
        public FieldNameConverter converter() {
            return new FieldNameConverter();
        }

        @Bean
        public PatchItemRequestValidator patchItemRequestValidator(CertificateOptionsValidator certificateOptionsValidator) {
            return new PatchItemRequestValidator(objectMapper(), validator(), converter());
        }

        @Bean
        TestMergePatchFactory patchFactory() {
            return new TestMergePatchFactory(objectMapper());
        }

        @Bean
        OptionsValidationHelperFactory optionsValidationHelperFactory(FeatureOptions featureOptions) {
            return new OptionsValidationHelperFactory(featureOptions);
        }
    }

    private static final int TOKEN_QUANTITY = 2;
    private static final int INVALID_QUANTITY = 0;
    private static final String TOKEN_STRING = "TOKEN VALUE";
    static final Map<String, String> TOKEN_VALUES = new HashMap<>();
    private static final ItemCosts TOKEN_ITEM_COSTS = new ItemCosts();
    private static final boolean TOKEN_POSTAL_DELIVERY_VALUE = true;

    @Autowired
    private PatchItemRequestValidator validatorUnderTest;

    @Autowired
    private TestMergePatchFactory patchFactory;

    private PatchValidationCertificateItemDTO itemUpdate;

    @MockBean
    private RequestValidatable requestValidatable;

    @BeforeEach
    void setUp() {
        itemUpdate = new PatchValidationCertificateItemDTO();
        CertificateItemOptions certificateItemOptions = new CertificateItemOptions();
        when(requestValidatable.itemOptions()).thenReturn(certificateItemOptions);
    }

    @Test
    @DisplayName("No errors")
    void getValidationErrorsReturnsNoErrors() throws IOException {
        // Given
        itemUpdate.setQuantity(TOKEN_QUANTITY);
        final JsonMergePatch patch = patchFactory.patchFromPojo(itemUpdate);

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(patch);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Quantity must be greater than 0")
    void getValidationErrorsRejectsZeroQuantity() throws IOException {
        // Given
        itemUpdate.setQuantity(INVALID_QUANTITY);
        final JsonMergePatch patch = patchFactory.patchFromPojo(itemUpdate);

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(patch);

        // Then
        assertThat(errors, contains(ApiErrors.raiseError(ApiErrors.ERR_QUANTITY_AMOUNT, "quantity: must be greater than or equal to 1")));
    }

    @Test
    @DisplayName("Validation error raised if unknown field specified")
    void getValidationErrorsRaisesErrorIfUnknownFieldSpecified() throws IOException {
        // Given
        final String jsonWithUnknownField = "{ \"idx\": \"CHS1\" }";
        final JsonMergePatch patch = patchFactory.patchFromJson(jsonWithUnknownField);

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(patch);

        // Then
        assertThat(errors, contains(ApiErrors.ERR_JSON_PROCESSING));
    }
}
