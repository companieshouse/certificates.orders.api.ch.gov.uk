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
import uk.gov.companieshouse.items.orders.api.model.CreatedBy;
import uk.gov.companieshouse.items.orders.api.service.CertificateItemService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.items.orders.api.util.TestConstants.ERIC_IDENTITY_HEADER_NAME;
import static uk.gov.companieshouse.items.orders.api.util.TestConstants.ERIC_IDENTITY_VALUE;

@RunWith(MockitoJUnitRunner.class)
public class CertificateItemsInterceptorTest {

    @InjectMocks
    private CertificateItemsInterceptor certificateItemsInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private CertificateItemService service;

    private static final String ITEM_ID = "CHS00000000000000001";
    private static final String ALTERNATIVE_CREATED_BY = "abc123";

    @Test
    @DisplayName("Authorise if authorised user created the certificate")
    public void willAuthoriseIfAuthorisedUserCreatedTheCertificate() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", ITEM_ID);

        CertificateItem item = new CertificateItem();
        item.setId(ITEM_ID);
        CreatedBy createdBy = new CreatedBy();
        createdBy.setId(ERIC_IDENTITY_VALUE);
        item.setCreatedBy(createdBy);

        when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(map);
        when(request.getHeader(ERIC_IDENTITY_HEADER_NAME)).thenReturn(ERIC_IDENTITY_VALUE);
        when(service.getCertificateItemById(ITEM_ID)).thenReturn(Optional.of(item));

        assertTrue(certificateItemsInterceptor.preHandle(request, response, null));
    }

    @Test
    @DisplayName("Does not authorise if authorised user did not create the certificate")
    public void willNotAuthoriseIfAuthorisedUserDidNotCreateTheCertificate() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", ITEM_ID);

        CertificateItem item = new CertificateItem();
        item.setId(ITEM_ID);
        CreatedBy createdBy = new CreatedBy();
        createdBy.setId(ALTERNATIVE_CREATED_BY);
        item.setCreatedBy(createdBy);

        when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(map);
        when(request.getHeader(ERIC_IDENTITY_HEADER_NAME)).thenReturn(ERIC_IDENTITY_VALUE);
        when(service.getCertificateItemById(ITEM_ID)).thenReturn(Optional.of(item));

        assertFalse(certificateItemsInterceptor.preHandle(request, response, null));
    }

    @Test
    @DisplayName("Does not authorise if authorised user did not create the certificate")
    public void willNotAuthoriseIfCertificateIsNotFound() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", ITEM_ID);

        when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(map);
        when(request.getHeader(ERIC_IDENTITY_HEADER_NAME)).thenReturn(ERIC_IDENTITY_VALUE);
        when(service.getCertificateItemById(ITEM_ID)).thenReturn(Optional.empty());

        assertFalse(certificateItemsInterceptor.preHandle(request, response, null));
    }

}
