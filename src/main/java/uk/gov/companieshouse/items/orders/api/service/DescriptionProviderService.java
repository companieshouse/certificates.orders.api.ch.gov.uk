package uk.gov.companieshouse.items.orders.api.service;

import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * Service that provides the description fields to facilitate UI text rendering.
 */
@Service
public class DescriptionProviderService {

    // TODO private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    private static final String COMPANY_NUMBER_KEY = "company-number";
    private static final String CERTIFICATE_DESCRIPTION_KEY = "certificate-description";
    private static final String COMPANY_CERTIFICATE_DESCRIPTION_KEY = "company-certificate";

    private static final String ORDERS_DESCRIPTIONS_FILEPATH = "api-enumerations/orders_descriptions.yaml";

    public String getDescription(final String companyNumber) throws FileNotFoundException {
        final Map<String, String> descriptionValues = getDescriptionValues(companyNumber);
        final Yaml yaml = new Yaml();
        final File descriptionsFile = new File(ORDERS_DESCRIPTIONS_FILEPATH);
        if (descriptionsFile.exists()) {
            final InputStream inputStream = new FileInputStream(descriptionsFile);
            final Map<String, Object> orderDescriptions = yaml.load(inputStream);
            final Map<String, Object> certificateDescriptions = (Map<String, Object>) orderDescriptions.get(CERTIFICATE_DESCRIPTION_KEY);
            final String companyCertificateDescription = (String) certificateDescriptions.get(COMPANY_CERTIFICATE_DESCRIPTION_KEY);
            return StrSubstitutor.replace(companyCertificateDescription, descriptionValues, "{", "}");
        } else {
            // TODO Error logging etc
            return null;
        }
    }

    public Map<String, String> getDescriptionValues(final String companyNumber) {
        return Collections.singletonMap(COMPANY_NUMBER_KEY, companyNumber);
    }

}
