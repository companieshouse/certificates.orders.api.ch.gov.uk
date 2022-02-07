package uk.gov.companieshouse.certificates.orders.api.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

    private static final UriTemplate GET_COMPANY_URI = new UriTemplate("/company/{companyNumber}");

    private final ApiClientService apiClientService;

    public CompanyService(final ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    /**
     * Interrogates the company profiles API to get the company name, type and status for the
     * company number provided.
     *
     * @param companyNumber the number of the company
     * @return A {@link CompanyProfileResource} object containing required company profile details.
     * @throws CompanyNotFoundException when the company is not found
     * @throws CompanyServiceException  for all other internal errors
     */
    public CompanyProfileResource getCompanyProfile(final String companyNumber) throws CompanyNotFoundException, CompanyServiceException {

        final ApiClient apiClient = apiClientService.getInternalApiClient();
        final String uri = GET_COMPANY_URI.expand(companyNumber).toString();

        try {
            CompanyProfileApi companyProfile = apiClient.company().get(uri).execute().getData();
            return new CompanyProfileResource(companyProfile.getCompanyName(),
                    companyProfile.getType(),
                    CompanyStatus.getEnumValue(companyProfile.getCompanyStatus()));
        } catch (ApiErrorResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                final String error = "Company profile not found company number " + companyNumber;
                LOGGER.error(error, ex);
                throw new CompanyNotFoundException(error);
            } else {
                final String error = "Error sending request to "
                        + apiClient.getBasePath() + uri + ": " + ex.getStatusMessage();
                LOGGER.error(error, ex);
                throw new CompanyServiceException(error);
            }
        } catch (URIValidationException ex) {
            final String error = "Invalid URI " + uri + " for company details";
            LOGGER.error(error, ex);
            throw new CompanyServiceException(error);
        }
    }
}