package uk.gov.companieshouse.certificates.orders.api.interceptor;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.certificates.orders.api.util.StringHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

import static java.util.Objects.isNull;
import static uk.gov.companieshouse.certificates.orders.api.util.EricHeaderHelper.ERIC_AUTHORISED_ROLES;

@Component
public
class EricAuthoriser {
    private final StringHelper stringHelper;

    public EricAuthoriser(final StringHelper stringHelper) {
        this.stringHelper = stringHelper;
    }

    public boolean hasPermission(final String permission, final HttpServletRequest request) {
        // Note: ERIC_AUTHORISED_ROLES contains a space separated list of permissions
        final String authorisedRolesHeader = request.getHeader(ERIC_AUTHORISED_ROLES);
        if (isNull(authorisedRolesHeader)) {
            return false;
        }

        // Note: permissions are space separated
        final Set<String> permissions = stringHelper.asSet("\\s+", authorisedRolesHeader);
        if (! permissions.contains(permission)) {
            return false;
        }

        return true;
    }

}
