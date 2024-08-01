package uk.gov.companieshouse.certificates.orders.api.interceptor;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.certificates.orders.api.util.StringHelper;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;

import static java.util.Objects.isNull;
import static uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants.APPLICATION_NAMESPACE;
import static uk.gov.companieshouse.certificates.orders.api.util.EricHeaderHelper.ERIC_AUTHORISED_ROLES;

@Component
public
class EricAuthoriser {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    private final StringHelper stringHelper;

    public EricAuthoriser(final StringHelper stringHelper) {
        this.stringHelper = stringHelper;
    }

    public boolean hasPermission(final String permission, final HttpServletRequest request) {
        final String authorisedRolesHeader = request.getHeader(ERIC_AUTHORISED_ROLES);
        LOGGER.debug("Checking " + ERIC_AUTHORISED_ROLES + " header with value `"
                +  authorisedRolesHeader +"` for permission `" + permission + "`.");
        if (isNull(authorisedRolesHeader)) {
            return false;
        }

        // perms are space separated
        final Set<String> permissions = stringHelper.asSet("\\s+", authorisedRolesHeader);
        return permissions.contains(permission);
    }

}