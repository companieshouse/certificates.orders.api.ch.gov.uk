package uk.gov.companieshouse.items.orders.api.interceptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.items.orders.api.util.TestConstants.*;

@RunWith(MockitoJUnitRunner.class)
public class UserAuthenticationInterceptorTest {

    @InjectMocks
    private UserAuthenticationInterceptor userAuthenticationInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Test
    public void willAuthoriseIfEricHeadersArePresent() {
        when(request.getHeader(ERIC_IDENTITY_HEADER_NAME)).thenReturn(ERIC_IDENTITY_VALUE);
        when(request.getHeader(ERIC_IDENTITY_TYPE_HEADER_NAME)).thenReturn(ERIC_IDENTITY_TYPE_VALUE);
        assertTrue(userAuthenticationInterceptor.preHandle(request, response, null));
    }

    @Test
    public void willNotAuthoriseIfIdentityTypeHeaderIsNotPresent() {
        when(request.getHeader(ERIC_IDENTITY_HEADER_NAME)).thenReturn(ERIC_IDENTITY_VALUE);
        assertFalse(userAuthenticationInterceptor.preHandle(request, response, null));
    }

    @Test
    public void willNotAuthoriseIfIdentityHeaderIsNotPresent() {
        when(request.getHeader(ERIC_IDENTITY_TYPE_HEADER_NAME)).thenReturn(ERIC_IDENTITY_TYPE_VALUE);
        assertFalse(userAuthenticationInterceptor.preHandle(request, response, null));
    }
}
