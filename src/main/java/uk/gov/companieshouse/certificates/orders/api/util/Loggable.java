package uk.gov.companieshouse.certificates.orders.api.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface Loggable {
    Map<String, Object> getLogMap();
    String getMessage();
    HttpServletRequest getRequest();
}
