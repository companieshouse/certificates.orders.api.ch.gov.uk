package uk.gov.companieshouse.items.orders.api.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * Unit tests the {@link DescriptionProviderService} class.
 */
class DescriptionProviderServiceTest {

    private static final String COMPANY_NUMBER = "00006400";
    private static final String EXPECTED_DESCRIPTION = "certificate for company " + COMPANY_NUMBER;
    private static final String COMPANY_NUMBER_KEY = "company-number";
    private static final Map<String, String> EXPECTED_DESCRIPTION_VALUES =
            singletonMap(COMPANY_NUMBER_KEY, COMPANY_NUMBER);

    @Test
    @DisplayName("Provides expected description")
    void getDescriptionProvidesExpectedDescription() {
        final DescriptionProviderService provider = new DescriptionProviderService();
        assertThat(provider.getDescription(COMPANY_NUMBER), is(EXPECTED_DESCRIPTION));
    }

    @Test
    @DisplayName("Provides expected description values")
    void getDescriptionValuesProvidesExpectedValues() {
        final DescriptionProviderService provider = new DescriptionProviderService();
        assertThat(provider.getDescriptionValues(COMPANY_NUMBER), is(EXPECTED_DESCRIPTION_VALUES));
    }

    @Test
    @DisplayName("Returns null when orders description file not found")
    void getDescriptionFileNotFoundReturnsNull() {
        final DescriptionProviderService provider = new DescriptionProviderService(new File("notfound.yaml"));
        assertThat(provider.getDescription(COMPANY_NUMBER), is(nullValue()));
    }

    @Test
    @DisplayName("Returns null when certificate descriptions section not found in orders description file")
    void getDescriptionIncorrectCertificateDescriptionsKeyReturnsNull() {
        final File file = getFile("/api-enumerations/orders_descriptions_incorrect_certificate_descriptions_key.yaml");
        final DescriptionProviderService provider =
               new DescriptionProviderService(file);
        assertThat(provider.getDescription(COMPANY_NUMBER), is(nullValue()));
    }

    @Test
    @DisplayName("Returns null when company certificate description not found in orders description file")
    void getDescriptionIncorrectCompanyCertificateDescriptionKeyReturnsNull() {
        final File file = getFile("/api-enumerations/orders_descriptions_incorrect_company_certificate_description_key.yaml");
        final DescriptionProviderService provider =
                new DescriptionProviderService(file);
        assertThat(provider.getDescription(COMPANY_NUMBER), is(nullValue()));
    }

    /**
     * Gets the file from the test resources directory
     * @param filePath the relative file path of the file within the test resources directory
     * @return the {@link File} representing the test resource file at the file path
     */
    private File getFile(final String filePath) {
        final File resourcesDirectory = new File("src/test/resources");
        return new File(resourcesDirectory.getAbsolutePath() + filePath);
    }
}
