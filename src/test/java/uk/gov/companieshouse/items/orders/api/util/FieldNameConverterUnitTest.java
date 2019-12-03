package uk.gov.companieshouse.items.orders.api.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Unit tests the {@link FieldNameConverter} class.
 */
@SpringBootTest
class FieldNameConverterUnitTest {

    @Autowired
    private FieldNameConverter converterUnderTest;

    @Test
    void toSnakeCaseWorksAsExpected() {
        assertThat(converterUnderTest.toSnakeCase("itemCosts"), is("item_costs"));
        assertThat(converterUnderTest.toSnakeCase("item"), is("item"));
        // Does not work for more than two words!
        assertThat(converterUnderTest.toSnakeCase("certIncConLast"), is("certIncCon_last"));
    }
}
