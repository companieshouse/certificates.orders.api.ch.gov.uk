package uk.gov.companieshouse.certificates.orders.api.service;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.company.CompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.CompanyGet;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;

import java.io.IOException;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;
import static uk.gov.companieshouse.api.error.ApiErrorResponseException.fromHttpResponseException;
import static uk.gov.companieshouse.api.error.ApiErrorResponseException.fromIOException;

/**
 * Unit tests the {@link CompanyService} class.
 */
@ExtendWith(MockitoExtension.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpResponseException.class)
class CompanyServiceTest {

    private static final String COMPANY_NUMBER = "00006400";

    private static final String INVALID_URI = "URI pattern does not match expected URI pattern for this resource.";
    private static final String INVALID_URI_EXPECTED_REASON = "Invalid URI /company/00006400 for company details";

    private static final String IOEXCEPTION_MESSAGE = "IOException thrown by test";
    private static final String IOEXCEPTION_EXPECTED_REASON =
            "Error sending request to http://host/company/00006400: " + IOEXCEPTION_MESSAGE;

    private static final String NOT_FOUND_EXPECTED_REASON = "Error getting company name for company number "
            + COMPANY_NUMBER;

    @InjectMocks
    private CompanyService serviceUnderTest;

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private InternalApiClient apiClient;

    @Mock
    private CompanyResourceHandler handler;

    @Mock
    private CompanyGet get;

    @Mock
    private ApiResponse<CompanyProfileApi> response;

    @Mock
    private CompanyProfileApi data;

    @Test
    @DisplayName("getCompanyProfile() returns model if request handled successfully")
    void getCompanyProfileReturnsCompanyProfileModel() throws ApiErrorResponseException, URIValidationException, CompanyServiceException {
        //given
        when(apiClientService.getInternalApiClient()).thenReturn(apiClient);
        when(apiClient.company()).thenReturn(handler);
        when(handler.get(anyString())).thenReturn(get);
        when(get.execute()).thenReturn(response);
        when(response.getData()).thenReturn(data);
        when(data.getCompanyName()).thenReturn("TEST LIMITED");
        when(data.getType()).thenReturn("limited-partnership");
        when(data.getCompanyStatus()).thenReturn("active");

        //when
        CompanyProfileResource resource = serviceUnderTest.getCompanyProfile("12345678");

        //then
        assertThat(resource, is(new CompanyProfileResource(
                "TEST LIMITED",
                "limited-partnership",
                CompanyStatus.ACTIVE
        )));
        assertThat(resource.getCompanyName(), is("TEST LIMITED"));
        assertThat(resource.getCompanyType(), is("limited-partnership"));
        verify(handler).get("/company/12345678");
    }

    @Test
    @DisplayName("getCompanyProfile() Invalid URL results in CompanyServiceException")
    void getCompanyProfileThrowsCompanyServiceExceptionForInvalidUri() throws Exception {

        // Given
        when(apiClientService.getInternalApiClient()).thenReturn(apiClient);
        when(apiClient.company()).thenReturn(handler);
        when(handler.get(anyString())).thenReturn(get);
        when(get.execute()).thenThrow(new URIValidationException(INVALID_URI));

        // When and then
        final CompanyServiceException exception =
                Assertions.assertThrows(CompanyServiceException.class,
                        () -> serviceUnderTest.getCompanyProfile(COMPANY_NUMBER));
        assertThat(exception.getMessage(), is(INVALID_URI_EXPECTED_REASON));
    }

    @Test
    @DisplayName("getCompanyProfile() IOException results CompanyServiceException")
    void getCompanyProfileInternalServerErrorApiExceptionIsPropagated() throws Exception {

        final IOException ioException = new IOException(IOEXCEPTION_MESSAGE);

        // Given
        when(apiClientService.getInternalApiClient()).thenReturn(apiClient);
        when(apiClient.company()).thenReturn(handler);
        when(handler.get(anyString())).thenReturn(get);
        when(get.execute()).thenThrow(fromIOException(ioException));
        when(apiClient.getBasePath()).thenReturn("http://host");

        // When and then
        final CompanyServiceException exception =
                Assertions.assertThrows(CompanyServiceException.class,
                        () -> serviceUnderTest.getCompanyProfile(COMPANY_NUMBER));
        assertThat(exception.getMessage(), is(IOEXCEPTION_EXPECTED_REASON));
    }

    /**
     * This is a JUnit 4 test to take advantage of PowerMock.
     * @throws Exception should something unexpected happen
     */
    @Test
    void getCompanyProfileNonInternalServerErrorApiExceptionIsBadRequest() throws Exception {

        // Given
        final HttpResponseException httpResponseException = PowerMockito.mock(HttpResponseException.class);
        when(httpResponseException.getStatusCode()).thenReturn(404);
        when(httpResponseException.getStatusMessage()).thenReturn("Not Found");
        when(httpResponseException.getHeaders()).thenReturn(new HttpHeaders());
        final ApiErrorResponseException ex = fromHttpResponseException(httpResponseException);

        when(apiClientService.getInternalApiClient()).thenReturn(apiClient);
        when(apiClient.company()).thenReturn(handler);
        when(handler.get(anyString())).thenReturn(get);
        when(get.execute()).thenThrow(ex);

        // When and then
        final CompanyNotFoundException exception =
                Assertions.assertThrows(CompanyNotFoundException.class,
                        () -> serviceUnderTest.getCompanyProfile(COMPANY_NUMBER));
        assertThat(exception.getMessage(), is("Company profile not found company number 00006400"));
    }

    @Test
    void shouldThrowCompanyServiceExceptionWhenApiClientReturnsOtherNotFoundErrors() throws Exception {
        //given
        final HttpResponseException httpResponseException = PowerMockito.mock(HttpResponseException.class);
        when(httpResponseException.getStatusCode()).thenReturn(500);
        when(httpResponseException.getStatusMessage()).thenReturn("Service unavailable");
        when(httpResponseException.getHeaders()).thenReturn(new HttpHeaders());
        final ApiErrorResponseException ex = fromHttpResponseException(httpResponseException);

        when(apiClientService.getInternalApiClient()).thenReturn(apiClient);
        when(apiClient.company()).thenReturn(handler);
        when(handler.get(anyString())).thenReturn(get);
        when(get.execute()).thenThrow(ex);

        // When and then
        final CompanyServiceException exception =
                Assertions.assertThrows(CompanyServiceException.class,
                        () -> serviceUnderTest.getCompanyProfile(COMPANY_NUMBER));
        assertThat(exception.getMessage(), is("Error sending request to null/company/00006400: Service unavailable"));
    }
}