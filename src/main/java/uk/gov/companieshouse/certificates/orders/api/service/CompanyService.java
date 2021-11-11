package uk.gov.companieshouse.certificates.orders.api.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import static uk.gov.companieshouse.certificates.orders.api.logging.LoggingConstants.APPLICATION_NAMESPACE;

@Service
public class CompanyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    private static final UriTemplate
            GET_COMPANY_URI =
            new UriTemplate("/company/{companyNumber}");

    private final ApiClientService apiClientService;

    public CompanyService(final ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    /**
     * Interrogates the company profiles API to get the company name, type and status for the
     * companynumber provided.
     *
     * @param companyNumber the number of the company
     * @return A {@link CompanyProfileResource} object containing required company profile details.
     */
    public CompanyProfileResource getCompanyProfile(final String companyNumber) {

        final ApiClient apiClient = apiClientService.getInternalApiClient();
        final String uri = GET_COMPANY_URI.expand(companyNumber).toString();

        try {
            CompanyProfileApi companyProfile = apiClient.company().get(uri).execute().getData();
            return new CompanyProfileResource(companyProfile.getCompanyName(),
                    companyProfile.getType(),
                    CompanyStatus.getEnumValue(companyProfile.getCompanyStatus()));
        } catch (ApiErrorResponseException ex) {
            throw getResponseStatusException(ex, apiClient, companyNumber, uri);
        } catch (URIValidationException ex) {
            // Should this happen (unlikely), it is a broken contract, hence 500.
            final String error = "Invalid URI " + uri + " for company details";
            LOGGER.error(error, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error);
        }
    }

    /**
     * Creates an appropriate exception to report the underlying problem.
     * @param apiException the API exception caught
     * @param client the API client
     * @param companyNumber the number of the company looked up
     * @param uri the URI used to communicate with the company profiles API
     * @return the {@link ResponseStatusException} exception to report the problem
     */
    private ResponseStatusException getResponseStatusException(final ApiErrorResponseException apiException,
                                                               final ApiClient client,
                                                               final String companyNumber,
                                                               final String uri) {

        final ResponseStatusException propagatedException;
        if (apiException.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            final String error = "Error sending request to "
                    + client.getBasePath() + uri + ": " + apiException.getStatusMessage();
            LOGGER.error(error, apiException);
            propagatedException = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error);
        } else {
            final String error = "Error getting company name for company number " + companyNumber;
            LOGGER.error(error, apiException);
            propagatedException =  new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }
        return propagatedException;
    }

}
