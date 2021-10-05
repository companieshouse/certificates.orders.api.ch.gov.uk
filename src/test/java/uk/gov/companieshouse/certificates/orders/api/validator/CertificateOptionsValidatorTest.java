package uk.gov.companieshouse.certificates.orders.api.validator;

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

@ExtendWith(MockitoExtension.class)
class CertificateOptionsValidatorTest {
    @Mock
    Consumer<OptionsValidationHelper> optionsValidationHelperConsumer;

    @Test
    void correctlyFailsWhenCompanyTypeIsNull() {
        // Given
        CertificateItemOptions certificateItemOptions = new CertificateItemOptions();
        CertificateOptionsValidator validator = new CertificateOptionsValidator(optionsValidationHelperConsumer);

        // When
        List<String> result = validator.validate(certificateItemOptions);

        // Then
        assertThat(result, containsInAnyOrder("company type: is a mandatory field"));
    }

    @Test
    void correctlyPassesWhenCompanyTypeIsNotNull() {
        // Given
        CertificateItemOptions certificateItemOptions = new CertificateItemOptions();
        certificateItemOptions.setCompanyType("limited");
        CertificateOptionsValidator validator = new CertificateOptionsValidator(optionsValidationHelperConsumer);

        // When
        List<String> result = validator.validate(certificateItemOptions);

        // Then
        assertThat(result, is(empty()));
    }
}
