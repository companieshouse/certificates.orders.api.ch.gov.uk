package uk.gov.companieshouse.items.orders.api.interceptor;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.service.CertificateItemService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.items.orders.api.util.TestConstants.*;

@RunWith(MockitoJUnitRunner.class)
public class UserAuthorisationInterceptorTest {

    @InjectMocks
    private UserAuthorisationInterceptor userAuthorisationInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private CertificateItemService service;

    private static final String ITEM_ID = "CHS00000000000000001";
    private static final String ALTERNATIVE_CREATED_BY = "abc123";

    @Test
    @DisplayName("Authorise if authenticated user created the certificate when request method is GET")
    public void willAuthoriseIfAuthorisedUserCreatedTheCertificateWhenRequestMethodIsGet() {
        Map<String, String> map = new HashMap<>();
        map.put("id", ITEM_ID);

        CertificateItem item = new CertificateItem();
        item.setId(ITEM_ID);
        item.setUserId(ERIC_IDENTITY_VALUE);

        when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(map);
        when(request.getHeader(ERIC_IDENTITY_HEADER_NAME)).thenReturn(ERIC_IDENTITY_VALUE);
        when(request.getHeader(ERIC_IDENTITY_TYPE_HEADER_NAME)).thenReturn(ERIC_IDENTITY_TYPE_OAUTH2_VALUE);
        when(service.getCertificateItemById(ITEM_ID)).thenReturn(Optional.of(item));

        assertTrue(userAuthorisationInterceptor.preHandle(request, response, null));
    }

    @Test
    @DisplayName("Authorise if request method is POST for a user")
    public void willAuthoriseIfPostAndOAuth2() {
        when(request.getMethod()).thenReturn(HttpMethod.POST.toString());
        when(request.getHeader(ERIC_IDENTITY_TYPE_HEADER_NAME)).thenReturn(ERIC_IDENTITY_TYPE_OAUTH2_VALUE);

        assertTrue(userAuthorisationInterceptor.preHandle(request, response, null));
    }

    @Test
    @DisplayName("Does not authorise if authenticated user did not create the certificate when request method is GET")
    public void doesNotAuthoriseIfAuthenticatedUserDidNotCreateTheCertificateWhenRequestMethodGet() {
        Map<String, String> map = new HashMap<>();
        map.put("id", ITEM_ID);

        CertificateItem item = new CertificateItem();
        item.setId(ITEM_ID);
        item.setUserId(ALTERNATIVE_CREATED_BY);

        when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(map);
        when(request.getHeader(ERIC_IDENTITY_HEADER_NAME)).thenReturn(ERIC_IDENTITY_VALUE);
        when(request.getHeader(ERIC_IDENTITY_TYPE_HEADER_NAME)).thenReturn(ERIC_IDENTITY_TYPE_OAUTH2_VALUE);
        when(service.getCertificateItemById(ITEM_ID)).thenReturn(Optional.of(item));

        assertFalse(userAuthorisationInterceptor.preHandle(request, response, null));
    }

    @Test
    @DisplayName("Does not authorise if Certificate is not found when request method is GET for a user")
    public void willNotAuthoriseIfCertificateIsNotFoundAndOAuth2() {
        Map<String, String> map = new HashMap<>();
        map.put("id", ITEM_ID);

        when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(map);
        when(request.getHeader(ERIC_IDENTITY_HEADER_NAME)).thenReturn(ERIC_IDENTITY_VALUE);
        when(request.getHeader(ERIC_IDENTITY_TYPE_HEADER_NAME)).thenReturn(ERIC_IDENTITY_TYPE_OAUTH2_VALUE);
        when(service.getCertificateItemById(ITEM_ID)).thenReturn(Optional.empty());

        assertFalse(userAuthorisationInterceptor.preHandle(request, response, null));
    }

    @Test
    @DisplayName("Authorise if GET and an API key is used")
    public void willAuthoriseIfRequestIsGetAndAPIKey() {
        when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
        when(request.getHeader(ERIC_IDENTITY_TYPE_HEADER_NAME)).thenReturn(ERIC_IDENTITY_TYPE_API_KEY_VALUE);
        assertTrue(userAuthorisationInterceptor.preHandle(request, response, null));
    }

    @Test
    @DisplayName("Does not Authorise if POST and an API key is used")
    public void willNotAuthoriseIfRequestIsPostAndAPIKey() {
        when(request.getMethod()).thenReturn(HttpMethod.POST.toString());
        when(request.getHeader(ERIC_IDENTITY_TYPE_HEADER_NAME)).thenReturn(ERIC_IDENTITY_TYPE_API_KEY_VALUE);
        assertFalse(userAuthorisationInterceptor.preHandle(request, response, null));
    }
}
