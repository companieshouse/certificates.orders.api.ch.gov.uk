package uk.gov.companieshouse.certificates.orders.api.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import uk.gov.companieshouse.certificates.orders.api.logging.LoggingUtils;
import uk.gov.companieshouse.certificates.orders.api.util.LoggableBuilder;
import uk.gov.companieshouse.certificates.orders.api.util.StringHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

import static java.util.Objects.isNull;
import static uk.gov.companieshouse.certificates.orders.api.util.EricHeaderHelper.ERIC_AUTHORISED_ROLES;

@Component
@RequestScope
public
class Oauth2Authoriser {
    private final WebContext webContext;
    private final StringHelper stringHelper;
    private boolean hasPermission;

    public Oauth2Authoriser(WebContext webContext, StringHelper stringHelper) {
        this.webContext = webContext;
        this.stringHelper = stringHelper;
    }

    public boolean checkPermission(final String permission, final HttpServletRequest request) {
        // Note: ERIC_AUTHORISED_ROLES contains a space separated list of permissions
        String authorisedRolesHeader = /*webContext.getHeader(ERIC_AUTHORISED_ROLES)*/request.getHeader(ERIC_AUTHORISED_ROLES);
        if (isNull(authorisedRolesHeader)) {
//            webContext.invalidate(LoggableBuilder.newBuilder()
//                    .withMessage("Authorisation error: caller authorised roles are absent")
//                    .build());
            return false;
        }

        // Note: permissions are space separated
        Set<String> permissions = /*stringHelper.*/ new StringHelper().asSet("\\s+", authorisedRolesHeader);
        if (! permissions.contains(permission)) {
//            webContext.invalidate(LoggableBuilder.newBuilder()
//                    .withLogMapPut(LoggingUtils.AUTHORISED_ROLES, authorisedRolesHeader)
//                    .withMessage("Authorisation error: caller does not have permission %s", permission)
//                    .build());
            return false;
        }

        //hasPermission = true;
        //return this;
        return true;
    }

//    boolean hasPermission() {
//        return hasPermission;
//    }
}
