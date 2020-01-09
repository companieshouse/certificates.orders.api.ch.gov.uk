package uk.gov.companieshouse.items.orders.api.interceptor;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.items.orders.api.util.EricHeaderHelper;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static uk.gov.companieshouse.items.orders.api.ItemsApiApplication.APPLICATION_NAMESPACE;

public class UserAuthenticationInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final String identityType = EricHeaderHelper.getIdentityType(request);
        if(identityType == null) {
            LOGGER.infoRequest(request, "UserAuthenticationInterceptor error: no authorised identity type", null);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        final String identity = EricHeaderHelper.getIdentity(request);
        if(identity == null) {
            LOGGER.infoRequest(request, "UserAuthenticationInterceptor error: no authorised identity", null);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        return true;
    }
}
