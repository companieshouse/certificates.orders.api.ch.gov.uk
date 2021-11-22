package uk.gov.companieshouse.certificates.orders.api.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;

import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificateOptionsValidatorTest {
    @Mock
    Consumer<OptionsValidationHelper> optionsValidationHelperConsumer;

    @Mock
    RequestValidatable requestValidatable;

    CertificateItemOptions certificateItemOptions;

    @BeforeEach
    void beforeEach() {
        certificateItemOptions = new CertificateItemOptions();
    }

    @Test
    void correctlyFailsWhenCompanyTypeIsNull() {
        // Given
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        CertificateOptionsValidator validator = new CertificateOptionsValidator(optionsValidationHelperConsumer);

        // When
        List<String> result = validator.validate(requestValidatable);

        // Then
        assertThat(result, containsInAnyOrder("company type: is a mandatory field"));
    }

    @Test
    void correctlyPassesWhenCompanyTypeIsNotNull() {
        // Given
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        certificateItemOptions.setCompanyType("limited");
        CertificateOptionsValidator validator = new CertificateOptionsValidator(optionsValidationHelperConsumer);

        // When
        List<String> result = validator.validate(requestValidatable);

        // Then
        assertThat(result, is(empty()));
    }
}
