package uk.gov.companieshouse.certificates.orders.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.SAME_DAY_EXTRA_CERTIFICATE_DISCOUNT;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.SAME_DAY_INDIVIDUAL_CERTIFICATE_COST;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.STANDARD_EXTRA_CERTIFICATE_DISCOUNT;
import static uk.gov.companieshouse.certificates.orders.api.util.TestConstants.STANDARD_INDIVIDUAL_CERTIFICATE_COST;

/**
 * Unit tests the {@link CostsConfig} class.
 */
@SpringBootTest
class CostsConfigTest {

    @Autowired
    private CostsConfig configUnderTest;

    @Test
    @DisplayName("The configured costs have their expected values")
    void costsAreConfiguredCorrectly() {
        assertThat(configUnderTest.getStandardCost(), is(STANDARD_INDIVIDUAL_CERTIFICATE_COST));
        assertThat(configUnderTest.getSameDayCost(), is(SAME_DAY_INDIVIDUAL_CERTIFICATE_COST));
        assertThat(configUnderTest.getStandardDiscount(), is(STANDARD_EXTRA_CERTIFICATE_DISCOUNT));
        assertThat(configUnderTest.getSameDayDiscount(), is(SAME_DAY_EXTRA_CERTIFICATE_DISCOUNT));
    }

}
