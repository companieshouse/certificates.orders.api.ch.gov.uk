package uk.gov.companieshouse.certificates.orders.api.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mapping.PersistentProperty;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

/**
 * Unit tests the {@link NoIsSnakeCaseFieldNamingStrategy} class.
 */
@ExtendWith(MockitoExtension.class)
class NoIsSnakeCaseFieldNamingStrategyTest {

    /**
     * Extends {@link NoIsSnakeCaseFieldNamingStrategy} to facilitate its unit testing.
     */
    private static class TestNoIsSnakeCaseFieldNamingStrategy extends NoIsSnakeCaseFieldNamingStrategy {

        private final String snakeCaseFieldName;

        public TestNoIsSnakeCaseFieldNamingStrategy(String snakeCaseFieldName) {
            this.snakeCaseFieldName = snakeCaseFieldName;
        }

        @Override
        protected String getSnakeCaseFieldName(PersistentProperty<?> property) {
            return snakeCaseFieldName;
        }
    }

    @Mock
    private PersistentProperty property;

    @Test
    @DisplayName("Removes the `is_` prefix from a Boolean field's name")
    void removesBooleanClassIsPrefix() {
        assertFieldNameIsThatExpected("postal_delivery", "is_postal_delivery", Boolean.class);
    }

    @Test
    @DisplayName("Removes the `is_` prefix from a boolean field's name")
    void removesBooleanPrimitiveIsPrefix() {
        assertFieldNameIsThatExpected("postal_delivery", "is_postal_delivery", boolean.class);
    }

    @Test
    @DisplayName("Does not remove the `is_` prefix from a non-boolean field's name")
    void doesNotRemoveNonBooleanIsPrefix() {
        assertFieldNameIsThatExpected("is_postal_delivery", "is_postal_delivery", String.class);
    }

    @Test
    @DisplayName("Removes only the first `is_` substring from a Boolean field's name")
    void removesFirstIsPrefixOnly() {
        assertFieldNameIsThatExpected("something_is_something_else", "is_something_is_something_else", Boolean.class);
    }

    /**
     * Utility method that asserts that the strategy returns the expected field name for the given original field name
     * and field type.
     * @param expectedFieldName the field name the strategy is expected to produce
     * @param originalFieldName the original field name
     * @param fieldType the type (class) of the field
     */
    void assertFieldNameIsThatExpected(final String expectedFieldName,
                                       final String originalFieldName,
                                       final Class fieldType) {
        // Given
        final NoIsSnakeCaseFieldNamingStrategy strategyUnderTest =
                new TestNoIsSnakeCaseFieldNamingStrategy(originalFieldName);
        when(property.getType()).thenReturn(fieldType);

        // When
        final String fieldName = strategyUnderTest.getFieldName(property);

        // Then
        assertThat(fieldName, is(expectedFieldName));
    }

}
