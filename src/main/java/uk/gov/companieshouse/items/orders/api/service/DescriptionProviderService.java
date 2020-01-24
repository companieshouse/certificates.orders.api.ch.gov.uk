package uk.gov.companieshouse.items.orders.api.service;

import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static uk.gov.companieshouse.items.orders.api.ItemsApiApplication.APPLICATION_NAMESPACE;

/**
 * Service that provides the description fields to facilitate UI text rendering.
 */
@Service
public class DescriptionProviderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    private static final String COMPANY_NUMBER_KEY = "company_number";
    private static final String CERTIFICATE_DESCRIPTION_KEY = "certificate-description";
    private static final String COMPANY_CERTIFICATE_DESCRIPTION_KEY = "company-certificate";

    private static final String ORDERS_DESCRIPTIONS_FILEPATH = "api-enumerations/orders_descriptions.yaml";

    private static final String LOG_MESSAGE_FILE_KEY = "file";

    private final String companyCertificateDescription;

    public DescriptionProviderService() {
        final File ordersDescriptionsFile = new File(ORDERS_DESCRIPTIONS_FILEPATH);
        companyCertificateDescription = getCompanyCertificateDescription(ordersDescriptionsFile);
    }

    public DescriptionProviderService(final File ordersDescriptionsFile) {
        companyCertificateDescription = getCompanyCertificateDescription(ordersDescriptionsFile);
    }

    /**
     * Gets the configured description.
     * @param companyNumber the company number making up part of the description
     * @return the configured description, or <code>null</code> if none found.
     */
    public String getDescription(final String companyNumber) {
        if (companyCertificateDescription == null) {
            // Error logged again here at time description is requested.
            logOrdersDescriptionsConfigError("Company certificate description not found in orders descriptions file",
                    COMPANY_CERTIFICATE_DESCRIPTION_KEY,
                    COMPANY_CERTIFICATE_DESCRIPTION_KEY);
            return null;
        }
        final Map<String, String> descriptionValues = getDescriptionValues(companyNumber);
        return StrSubstitutor.replace(companyCertificateDescription, descriptionValues, "{", "}");
    }

    /**
     * Gets the description values.
     * @param companyNumber the company number making up part of the description values
     * @return the description values
     */
    public Map<String, String> getDescriptionValues(final String companyNumber) {
        return singletonMap(COMPANY_NUMBER_KEY, companyNumber);
    }

    /**
     * Looks up the company certificate description by its key 'company-certificate' under the
     * 'certificate-description' section of the orders descriptions YAML file.
     * @param ordersDescriptionsFile the orders descriptions YAML file
     * @return the value found or <code>null</code> if none found.
     */
    private String getCompanyCertificateDescription(final File ordersDescriptionsFile) {

        if (!ordersDescriptionsFile.exists()) {
            logOrdersDescriptionsConfigError("Orders descriptions file not found",
                    LOG_MESSAGE_FILE_KEY,
                    ordersDescriptionsFile.getAbsolutePath());
            return null;
        }

        String companyCertificateDesc = null;
        try(final InputStream inputStream = new FileInputStream(ordersDescriptionsFile)) {
            final Yaml yaml = new Yaml();
            final Map<String, Object> orderDescriptions = yaml.load(inputStream);
            final Map<String, String> certificateDescriptions =
                    (Map<String, String>) orderDescriptions.get(CERTIFICATE_DESCRIPTION_KEY);
            if (certificateDescriptions == null) {
                logOrdersDescriptionsConfigError("Certificate descriptions not found in orders descriptions file",
                        CERTIFICATE_DESCRIPTION_KEY,
                        CERTIFICATE_DESCRIPTION_KEY);
                return null;
            }

            companyCertificateDesc = certificateDescriptions.get(COMPANY_CERTIFICATE_DESCRIPTION_KEY);
            if (companyCertificateDesc == null) {
                logOrdersDescriptionsConfigError("Company certificate description not found in orders descriptions file",
                        COMPANY_CERTIFICATE_DESCRIPTION_KEY,
                        COMPANY_CERTIFICATE_DESCRIPTION_KEY);
            }
        } catch (IOException ioe) {
            // This is very unlikely to happen here given File.exists() check above,
            // and that it is not likely to encounter an error closing the stream either.
        }
        return companyCertificateDesc;
    }

    /**
     * Logs an error message for issues related to the orders descriptions configuration.
     * @param errorMessage the error message to log
     * @param dataMapKey the error message data map key
     * @param dataMapValue the error message data map value
     */
    private void logOrdersDescriptionsConfigError(final String errorMessage,
                                                  final String dataMapKey,
                                                  final String dataMapValue) {
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(dataMapKey, dataMapValue);
        LOGGER.error(errorMessage, dataMap);
    }

}
