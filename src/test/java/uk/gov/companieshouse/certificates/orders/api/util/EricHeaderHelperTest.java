package uk.gov.companieshouse.certificates.orders.api.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_IDENTITY_HEADER_NAME;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_IDENTITY_TYPE_HEADER_NAME;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_IDENTITY_TYPE_OAUTH2_VALUE;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.ERIC_IDENTITY_VALUE;

@ExtendWith(MockitoExtension.class)
class EricHeaderHelperTest {

    @Mock
    private HttpServletRequest request;

    @Test
    void testGetIdentity() {
        when(request.getHeader(ERIC_IDENTITY_HEADER_NAME)).thenReturn(ERIC_IDENTITY_VALUE);
        assertEquals(ERIC_IDENTITY_VALUE, EricHeaderHelper.getIdentity(request));
    }

    @Test
    void testGetIdentityType() {
        when(request.getHeader(ERIC_IDENTITY_TYPE_HEADER_NAME)).thenReturn(ERIC_IDENTITY_TYPE_OAUTH2_VALUE);
        assertEquals(ERIC_IDENTITY_TYPE_OAUTH2_VALUE, EricHeaderHelper.getIdentityType(request));
    }

}
