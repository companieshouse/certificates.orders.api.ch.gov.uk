package uk.gov.companieshouse.certificates.orders.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.certificates.orders.api.util.StringHelper;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EricAuthoriserTest {

    @Mock
    private StringHelper stringHelper;

    private EricAuthoriser ericAuthoriser;

    private final String regexDelimiter = "\\s+";
    @BeforeEach
    void setUp() {
        ericAuthoriser = new EricAuthoriser(stringHelper);
    }

    @Test
    void testHasPermissionWhenHeaderIsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("ERIC-Authorised-Roles")).thenReturn(null);

        boolean result = ericAuthoriser.hasPermission("read", request);

        assertFalse(result);
    }

    @Test
    void testHasPermissionWhenPermissionIsPresent() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String headerValue = "read write";
        when(request.getHeader("ERIC-Authorised-Roles")).thenReturn(headerValue);
        when(stringHelper.asSet(regexDelimiter, headerValue)).thenReturn(Set.of("read", "write"));

        boolean result = ericAuthoriser.hasPermission("read", request);

        assertTrue(result);
    }

    @Test
    void testHasPermissionWhenPermissionIsNotPresent() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String headerValue = "write execute";
        when(request.getHeader("ERIC-Authorised-Roles")).thenReturn(headerValue);
        when(stringHelper.asSet(regexDelimiter, headerValue)).thenReturn(Set.of("write", "execute"));

        boolean result = ericAuthoriser.hasPermission("read", request);

        assertFalse(result);
    }

    @Test
    void testHasPermissionWithEmptyHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String headerValue = "";
        when(request.getHeader("ERIC-Authorised-Roles")).thenReturn(headerValue);
        when(stringHelper.asSet(regexDelimiter, headerValue)).thenReturn(Set.of());

        boolean result = ericAuthoriser.hasPermission("read", request);

        assertFalse(result);
    }

    @Test
    void testHasPermissionWithMultiplePermissions() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String headerValue = "read write execute";
        when(request.getHeader("ERIC-Authorised-Roles")).thenReturn(headerValue);
        when(stringHelper.asSet(regexDelimiter, headerValue)).thenReturn(Set.of("read", "write", "execute"));

        boolean result = ericAuthoriser.hasPermission("execute", request);

        assertTrue(result);
    }
}
