package uk.gov.companieshouse.certificates.orders.api.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.certificates.orders.api.util.EricHeaderHelper;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants.*;

public class UserAuthenticationInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put(REQUEST_ID_LOG_KEY, request.getHeader(REQUEST_ID_HEADER_NAME));
        final String identityType = EricHeaderHelper.getIdentityType(request);
        if(identityType == null) {
            logMap.put(STATUS_LOG_KEY, UNAUTHORIZED);
            LOGGER.infoRequest(request, "UserAuthenticationInterceptor error: no authorised identity type", logMap);
            response.setStatus(UNAUTHORIZED.value());
            return false;
        }

        final String identity = EricHeaderHelper.getIdentity(request);
        if(identity == null) {
            logMap.put(STATUS_LOG_KEY, UNAUTHORIZED);
            LOGGER.infoRequest(request, "UserAuthenticationInterceptor error: no authorised identity", logMap);
            response.setStatus(UNAUTHORIZED.value());
            return false;
        }
        return true;
    }
}
