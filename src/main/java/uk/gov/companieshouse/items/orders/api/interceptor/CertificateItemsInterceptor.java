package uk.gov.companieshouse.items.orders.api.interceptor;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
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

public class CertificateItemsInterceptor extends HandlerInterceptorAdapter {

    private final CertificateItemService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    public CertificateItemsInterceptor(CertificateItemService service) {
        this.service = service;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(!HttpMethod.POST.matches(request.getMethod())) {
            final Map<String, String> pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            final String certificateId = pathVariables.get("id");

            final String identity = EricHeaderHelper.getIdentity(request);
            Optional<CertificateItem> item = service.getCertificateItemById(certificateId);

            if(item.isPresent()){
                String userId = item.get().getUserId();
                boolean authUserIsCreatedBy = userId.equals(identity);
                if(authUserIsCreatedBy) {
                    LOGGER.info("User is permitted to view/edit the resource certificate createdBy="+userId+", identity="+ identity);
                    return true;
                }
                LOGGER.info("User is not permitted to view/edit the resource certificate createdBy="+userId+", identity="+ identity);
            } else {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return false;
            }

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        return true;
    }
}
