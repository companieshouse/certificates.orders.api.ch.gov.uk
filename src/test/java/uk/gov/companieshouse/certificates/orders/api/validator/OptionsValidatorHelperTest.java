package uk.gov.companieshouse.certificates.orders.api.validator;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

class OptionsValidatorHelperTest {

    @Test
    void correctlyValidatesCompanyTypeNotNull() {
        // Given
        CertificateItemOptions certificateItemOptions = new CertificateItemOptions();
        certificateItemOptions.setCompanyType("limited");
        OptionsValidationHelper helper = new OptionsValidationHelper(certificateItemOptions);
        // When
        boolean result = helper.notCompanyTypeIsNull();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(result, is(true));
        assertThat(errors, is(empty()));
    }

    @Test
    void correctlyErrorsWhenCompanyTypeIsNull() {
        // Given
        CertificateItemOptions certificateItemOptions = new CertificateItemOptions();
        OptionsValidationHelper helper = new OptionsValidationHelper(certificateItemOptions);
        // When
        boolean result = helper.notCompanyTypeIsNull();
        List<String> errors = helper.getErrors();

        // Then
        assertThat(result, is(false));
        assertThat(errors, containsInAnyOrder("company type: is a mandatory field"));
    }
}
