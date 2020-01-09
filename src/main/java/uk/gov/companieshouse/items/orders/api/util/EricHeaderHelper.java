package uk.gov.companieshouse.items.orders.api.util;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class EricHeaderHelper {

    public static final String ERIC_IDENTITY = "ERIC-Identity";
    public static final String ERIC_IDENTITY_TYPE = "ERIC-Identity-Type";
    private static final String ERIC_AUTHORISED_USER = "ERIC-Authorised-User";
    private static final String DELIMITER = ";";
    private static final String EMAIL_IDENTIFIER = "@";

    public static String getIdentity(HttpServletRequest request) {
        return getHeader(request, ERIC_IDENTITY);
    }

    public static String getIdentityType(HttpServletRequest request) {
        return getHeader(request, ERIC_IDENTITY_TYPE);
    }

    private static String getHeader(HttpServletRequest request, String headerName) {
        String headerValue = request.getHeader(headerName);
        if (StringUtils.isNotBlank(headerValue)) {
            return headerValue;
        } else {
            return null;
        }
    }

}
