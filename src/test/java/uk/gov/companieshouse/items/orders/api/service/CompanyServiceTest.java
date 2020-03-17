package uk.gov.companieshouse.items.orders.api.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Unit/integration tests the {@link CompanyService} class.
 */
@SpringBootTest
class CompanyServiceTest {

    private static final String COMPANY_NUMBER = "00006400";
    private static final String EXPECTED_COMPANY_NAME = "THE GIRLS' DAY SCHOOL TRUST";

    @Autowired
    private CompanyService serviceUnderTest;

    @Test
    void getCompanyName() throws Exception {
        assertThat(serviceUnderTest.getCompanyName(COMPANY_NUMBER), is(EXPECTED_COMPANY_NAME));
    }
}
