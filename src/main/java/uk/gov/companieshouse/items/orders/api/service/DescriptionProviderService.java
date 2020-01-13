package uk.gov.companieshouse.items.orders.api.service;

import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * Service that provides the description fields to facilitate UI text rendering.
 */
@Service
public class DescriptionProviderService {

    private static final String COMPANY_NUMBER_KEY = "company-number";
    private static final String DESCRIPTION = "certificate for company {company-number}";

    public String getDescription(final String companyNumber) {
        final Map<String, String> descriptionValues = getDescriptionValues(companyNumber);
        return StrSubstitutor.replace(DESCRIPTION, descriptionValues, "{", "}");
    }

    public Map<String, String> getDescriptionValues(final String companyNumber) {
        return Collections.singletonMap(COMPANY_NUMBER_KEY, companyNumber);
    }

}
