package uk.gov.companieshouse.certificates.orders.api.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;

import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificateOptionsValidatorTest {
    @Mock
    private Consumer<OptionsValidationHelper> optionsValidationHelperConsumer;

    @Mock
    private RequestValidatable requestValidatable;

    private CertificateItemOptions certificateItemOptions;

    @Mock
    private OptionsValidationHelperFactory optionsValidationHelperFactory;

    @Mock
    private OptionsValidationHelper optionsValidationHelper;

    @BeforeEach
    void beforeEach() {
        certificateItemOptions = new CertificateItemOptions();
    }

    @Test
    void correctlyFailsWhenCompanyTypeIsNull() {
        // Given
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);
        when(optionsValidationHelper.notCompanyTypeIsNull()).thenReturn(false);
        CertificateOptionsValidator validator = new CertificateOptionsValidator(optionsValidationHelperConsumer, optionsValidationHelperFactory);

        // When
        validator.validate(requestValidatable);

        // Then
        verifyNoInteractions(optionsValidationHelperConsumer);
    }

    @Test
    void correctlyPassesWhenCompanyTypeIsNotNull() {
        // Given
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);
        when(optionsValidationHelper.notCompanyTypeIsNull()).thenReturn(true);
        CertificateOptionsValidator validator = new CertificateOptionsValidator(optionsValidationHelperConsumer, optionsValidationHelperFactory);

        // When
        List<ApiError> result = validator.validate(requestValidatable);

        // Then
        assertThat(result, is(empty()));
    }
}
