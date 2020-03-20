package uk.gov.companieshouse.items.orders.api.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.handler.company.CompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.CompanyGet;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Unit tests the {@link CompanyService} class.
 */
@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    private static final String COMPANY_NUMBER = "00006400";
    private static final String INVALID_URI = "URI pattern does not match expected URI pattern for this resource.";
    private static final String EXPECTED_REASON = "Invalid URI /company/00006400 for company details";

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

    @Test
    public void getCompanyNameThrowsInternalServerErrorForInvalidUri() throws Exception {

        // Given
        when(apiClientService.getInternalApiClient()).thenReturn(apiClient);
        when(apiClient.company()).thenReturn(handler);
        when(handler.get(anyString())).thenReturn(get);
        when(get.execute()).thenThrow(new URIValidationException(INVALID_URI));

        // When and then
        final ResponseStatusException exception =
                Assertions.assertThrows(ResponseStatusException.class,
                        () -> serviceUnderTest.getCompanyName(COMPANY_NUMBER));
        assertThat(exception.getStatus(), is(INTERNAL_SERVER_ERROR));
        assertThat(exception.getReason(), is(EXPECTED_REASON));
    }

}
