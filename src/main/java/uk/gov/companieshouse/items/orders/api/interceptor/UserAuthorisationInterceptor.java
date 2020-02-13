package uk.gov.companieshouse.items.orders.api.interceptor;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.service.CertificateItemService;
import uk.gov.companieshouse.items.orders.api.util.EricHeaderHelper;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

import static uk.gov.companieshouse.items.orders.api.ItemsApiApplication.APPLICATION_NAMESPACE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.GET;

public class UserAuthorisationInterceptor extends HandlerInterceptorAdapter {

    private final CertificateItemService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    public UserAuthorisationInterceptor(CertificateItemService service) {
        this.service = service;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final String identityType = EricHeaderHelper.getIdentityType(request);
        boolean isApiKeyRequest = EricHeaderHelper.API_KEY_IDENTITY_TYPE.equals(identityType);
        boolean isOAuth2Request = EricHeaderHelper.OAUTH2_IDENTITY_TYPE.equals(identityType);

        if(isApiKeyRequest) {
            return validateAPI(request, response);
        }

        if(isOAuth2Request) {
            return validateOAuth2(request, response);
        }

        LOGGER.error("Unrecognised identity type");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;
    }

    private boolean validateAPI(HttpServletRequest request, HttpServletResponse response){
        if(GET.matches(request.getMethod())) {
            LOGGER.info("API is permitted to view the resource");
            return true;
        } else {
            LOGGER.error("API is not permitted to perform a "+request.getMethod());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
    }

    private boolean validateOAuth2(HttpServletRequest request, HttpServletResponse response) {
        if (!POST.matches(request.getMethod())) {
            final Map<String, String> pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            final String certificateId = pathVariables.get("id");

            final String identity = EricHeaderHelper.getIdentity(request);
            Optional<CertificateItem> item = service.getCertificateItemById(certificateId);

            if (item.isPresent()) {
                String userId = item.get().getUserId();
                if (userId == null) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    LOGGER.error("No user id found on certificate item, all certificates should have a user id");
                    return false;
                }
                boolean authUserIsCreatedBy = userId.equals(identity);
                if (authUserIsCreatedBy) {
                    LOGGER.info("User is permitted to view/edit the resource certificate userId=" + userId + ", identity=" + identity);
                    return true;
                } else {
                    LOGGER.error("User is not permitted to view/edit the resource certificate userId=" + userId + ", identity=" + identity);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return false;
                }
            } else {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return false;
            }
        }
        return true;
    }

}
