package uk.gov.companieshouse.items.orders.api.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.items.orders.api.model.Links;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Unit tests the {@link LinksGeneratorService} class.
 */
class LinksGeneratorServiceTest {

    private static final String SELF_PATH = "/orderable/certificates";
    private static final String ITEM_ID = "CHS00000000000000001";

    @Test
    @DisplayName("Generates links correctly with valid inputs")
    void generatesLinksCorrectlyWithValidInputs() {

        // Given
        final LinksGeneratorService generatorUnderTest =new LinksGeneratorService(SELF_PATH);

        // When
        final Links links = generatorUnderTest.generateLinks(ITEM_ID);

        // Then
        assertThat(links.getSelf(), is(SELF_PATH + "/" + ITEM_ID));
    }

    @Test
    @DisplayName("Unpopulated item ID argument results in an IllegalArgumentException")
    void itemIdMustNotBeBlank() {
        // Given
        final LinksGeneratorService generatorUnderTest = new LinksGeneratorService(SELF_PATH);

        // When and then
        final IllegalArgumentException exception =
                Assertions.assertThrows(IllegalArgumentException.class,
                        () -> generatorUnderTest.generateLinks(null));

        // Then
        assertThat(exception.getMessage(), is("Item ID not populated!"));
    }

    @Test
    @DisplayName("Unpopulated path to self URI results in an IllegalArgumentException")
    void selfPathMustNotBeBlank() {

        // When and then
        final IllegalArgumentException exception =
                Assertions.assertThrows(IllegalArgumentException.class,
                        () -> new LinksGeneratorService(null));

        // Then
        assertThat(exception.getMessage(), is("Path to self URI not configured!"));
    }

}
