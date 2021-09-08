package uk.gov.companieshouse.certificates.orders.api.interceptor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.lenient;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.*;

@ExtendWith(MockitoExtension.class)
public class UserAuthenticationInterceptorTest {

    @InjectMocks
    private UserAuthenticationInterceptor userAuthenticationInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Test
    public void willAuthoriseIfEricHeadersArePresent() {
        lenient().doReturn(ERIC_IDENTITY_VALUE).when(request).getHeader(ERIC_IDENTITY_HEADER_NAME);
        lenient().doReturn(ERIC_IDENTITY_TYPE_OAUTH2_VALUE).when(request).getHeader(ERIC_IDENTITY_TYPE_HEADER_NAME);
        assertTrue(userAuthenticationInterceptor.preHandle(request, response, null));
    }

    @Test
    public void willNotAuthoriseIfIdentityTypeHeaderIsNotPresent() {
        lenient().doReturn(null).when(request).getHeader(ERIC_IDENTITY_TYPE_HEADER_NAME);
        lenient().doReturn(null).when(request).getHeader(REQUEST_ID_HEADER_NAME);
        assertFalse(userAuthenticationInterceptor.preHandle(request, response, null));
    }

    @Test
    public void willNotAuthoriseIfIdentityHeaderIsNotPresent() {
        lenient().when(request.getHeader(ERIC_IDENTITY_TYPE_HEADER_NAME)).thenReturn(ERIC_IDENTITY_TYPE_OAUTH2_VALUE);
        assertFalse(userAuthenticationInterceptor.preHandle(request, response, null));
    }
}
