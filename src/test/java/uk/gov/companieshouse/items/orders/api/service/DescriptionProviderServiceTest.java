package uk.gov.companieshouse.items.orders.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Unit tests the {@link DescriptionProviderService} class.
 */
@ExtendWith(MockitoExtension.class)
class DescriptionProviderServiceTest {

    private static final String COMPANY_NUMBER = "00006400";
    private static final String EXPECTED_DESCRIPTION = "certificate for company " + COMPANY_NUMBER;
    private static final String COMPANY_NUMBER_KEY = "company-number";
    private static final Map<String, String> EXPECTED_DESCRIPTION_VALUES =
            singletonMap(COMPANY_NUMBER_KEY, COMPANY_NUMBER);

    @InjectMocks
    private DescriptionProviderService providerUnderTest;

    @Test
    void getDescriptionProvidesExpectedDescription() {
        assertThat(providerUnderTest.getDescription(COMPANY_NUMBER), is(EXPECTED_DESCRIPTION));
    }

    @Test
    void getDescriptionValuesProvidesExpectedValues() {
        assertThat(providerUnderTest.getDescriptionValues(COMPANY_NUMBER), is(EXPECTED_DESCRIPTION_VALUES));
    }
}
