package uk.gov.companieshouse.certificates.orders.api.util;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StringHelperTest {

    private StringHelper stringHelper;

    @BeforeEach
    void setUp() {
        stringHelper = new StringHelper();
    }

    @Test
    void testAsSetWithNonEmptyString() {
        String values = "read write execute";
        Set<String> result = stringHelper.asSet("\\s+", values);

        assertEquals(3, result.size());
        assertTrue(result.contains("read"));
        assertTrue(result.contains("write"));
        assertTrue(result.contains("execute"));
    }

    @Test
    void testAsSetWithEmptyString() {
        String values = "";
        Set<String> result = stringHelper.asSet("\\s+", values);

        assertTrue(result.isEmpty());
    }

    @Test
    void testAsSetWithNullString() {
        String values = null;
        Set<String> result = stringHelper.asSet("\\s+", values);

        assertTrue(result.isEmpty());
    }

    @Test
    void testAsSetWithMultipleDelimiters() {
        String values = "read,write,execute";
        Set<String> result = stringHelper.asSet(",", values);

        assertEquals(3, result.size());
        assertTrue(result.contains("read"));
        assertTrue(result.contains("write"));
        assertTrue(result.contains("execute"));
    }

    @Test
    void testAsSetWithEmptyValuesBetweenDelimiters() {
        String values = "read,,write,,execute";
        Set<String> result = stringHelper.asSet(",", values);

        assertEquals(3, result.size());
        assertTrue(result.contains("read"));
        assertTrue(result.contains("write"));
        assertTrue(result.contains("execute"));
    }

    @Test
    void testAsSetWithTrailingAndLeadingDelimiters() {
        String values = ",read,write,execute,";
        Set<String> result = stringHelper.asSet(",", values);

        assertEquals(3, result.size());
        assertTrue(result.contains("read"));
        assertTrue(result.contains("write"));
        assertTrue(result.contains("execute"));
    }

    @Test
    void testAsSetWithMixedWhitespace() {
        String values = " read \t write\nexecute ";
        Set<String> result = stringHelper.asSet("\\s+", values);

        assertEquals(3, result.size());
        assertTrue(result.contains("read"));
        assertTrue(result.contains("write"));
        assertTrue(result.contains("execute"));
    }
}
