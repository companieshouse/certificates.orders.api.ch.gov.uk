package uk.gov.companieshouse.items.orders.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;

@Service
public class CompanyService {

    private static final UriTemplate
            GET_COMPANY_URI =
            new UriTemplate("/company/{companyNumber}");

    private final ApiClientService apiClientService;

    public CompanyService(final ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    public String getCompanyName(final String companyNumber) throws Exception {

        final ApiClient apiClient = apiClientService.getInternalApiClient();
        String companyName;

        try {
            final String uri = GET_COMPANY_URI.expand(companyNumber).toString();
            companyName = apiClient.company().get(uri).execute().getData().getCompanyName();
        } catch (ApiErrorResponseException ex) {
            // TODO Is ServiceException useful here?
            throw new /*Service*/Exception("Error retrieving Company Details", ex);
        } catch (URIValidationException ex) {
            // TODO Is ServiceException useful here?
            throw new /*Service*/Exception("Invalid URI for Company Details", ex);
        }
        return companyName;
    }

}
