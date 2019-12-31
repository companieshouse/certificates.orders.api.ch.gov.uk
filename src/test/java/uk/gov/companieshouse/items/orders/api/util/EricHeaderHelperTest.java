package uk.gov.companieshouse.items.orders.api.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.items.orders.api.util.TestConstants.*;

@RunWith(MockitoJUnitRunner.class)
public class EricHeaderHelperTest {

    @Mock
    private HttpServletRequest request;

    @Test
    public void testGetIdentity() {
        when(request.getHeader(ERIC_IDENTITY_HEADER_NAME)).thenReturn(ERIC_IDENTITY_VALUE);
        assertEquals(ERIC_IDENTITY_VALUE, EricHeaderHelper.getIdentity(request));
    }

    @Test
    public void testGetIdentityType() {
        when(request.getHeader(ERIC_IDENTITY_TYPE_HEADER_NAME)).thenReturn(ERIC_IDENTITY_TYPE_VALUE);
        assertEquals(ERIC_IDENTITY_TYPE_VALUE, EricHeaderHelper.getIdentityType(request));
    }

}
