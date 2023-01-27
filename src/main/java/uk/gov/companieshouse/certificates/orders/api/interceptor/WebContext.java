package uk.gov.companieshouse.certificates.orders.api.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import uk.gov.companieshouse.certificates.orders.api.logging.LoggingUtils;
import uk.gov.companieshouse.certificates.orders.api.util.Log;
import uk.gov.companieshouse.certificates.orders.api.util.Loggable;
import uk.gov.companieshouse.certificates.orders.api.util.LoggableBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
@RequestScope
public class WebContext {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final Log log;

    public WebContext(HttpServletRequest request, HttpServletResponse response, Log log) {
        this.request = request;
        this.response = response;
        this.log = log;
    }

    public String getHeader(final String key) {
        return request.getHeader(key);
    }

    public void invalidate(Loggable loggable) {
        log.infoRequest(LoggableBuilder.newBuilder(loggable)
                .withLogMapPut(LoggingUtils.STATUS, UNAUTHORIZED)
                .withRequest(request)
                .build());
        response.setStatus(UNAUTHORIZED.value());
    }
}
