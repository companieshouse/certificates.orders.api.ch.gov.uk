package uk.gov.companieshouse.certificates.orders.api.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.controller.ApiErrors;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.CollectionLocation;
import uk.gov.companieshouse.certificates.orders.api.model.DeliveryMethod;
import uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale;
import uk.gov.companieshouse.certificates.orders.api.util.FieldNameConverter;

import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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

    @Mock
    private FieldNameConverter fieldNameConverter;

    @BeforeEach
    void beforeEach() {
        certificateItemOptions = new CertificateItemOptions();
    }

    @Test
    void correctlyFailsWhenCompanyTypeIsNull() {
        // Given
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);
        when(optionsValidationHelper.companyTypeIsNull()).thenReturn(true);
        CertificateOptionsValidator validator = new CertificateOptionsValidator(optionsValidationHelperConsumer, optionsValidationHelperFactory, fieldNameConverter);

        // When
        validator.validateCertificateOptions(requestValidatable);

        // Then
        verifyNoInteractions(optionsValidationHelperConsumer);
    }

    @Test
    void correctlyPassesWhenCompanyTypeIsNotNull() {
        // Given
        certificateItemOptions.setCompanyType("ltd");
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);
        when(optionsValidationHelper.companyTypeIsNull()).thenReturn(false);
        CertificateOptionsValidator validator = new CertificateOptionsValidator(optionsValidationHelperConsumer, optionsValidationHelperFactory, fieldNameConverter);

        // When
        List<ApiError> result = validator.validateCertificateOptions(requestValidatable);

        // Then
        assertThat(result, is(empty()));
    }

    @Test
    void correctlyPassesWhenItemOptionsNull() {
        //given
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);
        CertificateOptionsValidator validator = new CertificateOptionsValidator(optionsValidationHelperConsumer, optionsValidationHelperFactory, fieldNameConverter);

        // When
        List<ApiError> result = validator.validateCertificateOptions(requestValidatable);

        // Then
        assertThat(result, is(empty()));
    }

    @Test
    @DisplayName("Collection details are mandatory for collection delivery method")
    void collectionDetailsAreMandatoryForCollectionDeliveryMethod() {
        // Given
        certificateItemOptions.setCompanyType("limited");
        certificateItemOptions.setDeliveryMethod(DeliveryMethod.COLLECTION);
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);
        CertificateOptionsValidator validator = new CertificateOptionsValidator(optionsValidationHelperConsumer, optionsValidationHelperFactory, fieldNameConverter);

        // When
        final List<ApiError> errors = validator.validateCertificateOptions(requestValidatable);

        // Then
        assertThat(errors, containsInAnyOrder(
                ApiErrors.raiseError(ApiErrors.ERR_COLLECTION_LOCATION_REQUIRED, "collection_location: must not be null when delivery method is collection"),
                ApiErrors.raiseError(ApiErrors.ERR_FORENAME_REQUIRED, "forename: must not be blank when delivery method is collection"),
                ApiErrors.raiseError(ApiErrors.ERR_SURNAME_REQUIRED, "surname: must not be blank when delivery method is collection")));
    }

    @Test
    @DisplayName("Collection details have been provided for collection delivery method")
    void collectionDetailsProvidedForCollectionDeliveryMethod() {
        // Given
        certificateItemOptions.setCompanyType("limited");
        certificateItemOptions.setDeliveryMethod(DeliveryMethod.COLLECTION);
        certificateItemOptions.setForename("TOM");
        certificateItemOptions.setSurname("COBLEY");
        certificateItemOptions.setCollectionLocation(CollectionLocation.LONDON);
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);
        CertificateOptionsValidator validator = new CertificateOptionsValidator(optionsValidationHelperConsumer, optionsValidationHelperFactory, fieldNameConverter);

        // When
        final List<ApiError> errors = validator.validateCertificateOptions(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    void testRaiseErrorIfEmailRequestedAndDeliveryTimescaleNotSameDay() {
        //given
        certificateItemOptions.setIncludeEmailCopy(true);
        certificateItemOptions.setDeliveryTimescale(DeliveryTimescale.STANDARD);
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);
        CertificateOptionsValidator validator = new CertificateOptionsValidator(optionsValidationHelperConsumer, optionsValidationHelperFactory, fieldNameConverter);

        //when
        final List<ApiError> errors = validator.validateCertificateOptions(requestValidatable);

        //then
        assertThat(errors, contains(
                ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_EMAIL_COPY_NOT_ALLOWED, "include_email_copy: can only be true when delivery timescale is same_day")
        ));
    }

    @Test
    void testOptionsValidIfEmailRequestedAndDeliveryTimescaleSameDay() {
        //given
        certificateItemOptions.setIncludeEmailCopy(true);
        certificateItemOptions.setDeliveryTimescale(DeliveryTimescale.SAME_DAY);
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
        when(optionsValidationHelperFactory.createOptionsValidationHelper(any())).thenReturn(optionsValidationHelper);
        CertificateOptionsValidator validator = new CertificateOptionsValidator(optionsValidationHelperConsumer, optionsValidationHelperFactory, fieldNameConverter);

        //when
        final List<ApiError> errors = validator.validateCertificateOptions(requestValidatable);

        //then
        assertThat(errors, is(empty()));
    }
}
