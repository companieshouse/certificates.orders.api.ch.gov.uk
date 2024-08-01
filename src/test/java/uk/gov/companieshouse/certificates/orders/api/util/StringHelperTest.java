package uk.gov.companieshouse.certificates.orders.api.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StringHelperTest {

    private StringHelper stringHelper;

    @BeforeEach
    void setUp() {
        stringHelper = new StringHelper();
    }

    @ParameterizedTest
    @CsvSource({
            "'read write execute', '\\s+', 3, 'read,write,execute'",
            "'', '\\s+', 0, ''",
            "'null', '\\s+', 0, ''",
            "'read,write,execute', ',', 3, 'read,write,execute'",
            "'read,,write,,execute', ',', 3, 'read,write,execute'",
            "',read,write,execute,', ',', 3, 'read,write,execute'",
            "' read \t write\nexecute ', '\\s+', 3, 'read,write,execute'"
    })
    void testAsSet(String values, String delimiter, int expectedSize, String expectedValues) {
        // Interpret "null" as an actual null value
        if ("null".equals(values)) {
            values = null;
        }

        Set<String> result = stringHelper.asSet(delimiter, values);

        assertEquals(expectedSize, result.size());
        if (expectedSize > 0) {
            Set<String> expectedSet = Arrays.stream(expectedValues.split(","))
                    .collect(Collectors.toSet());
            assertTrue(result.containsAll(expectedSet));
        }
    }
}
