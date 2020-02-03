package uk.gov.companieshouse.items.orders.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import uk.gov.companieshouse.items.orders.api.model.CollectionLocation;

/**
 * Implements {@link Converter} to alter the way the collection location values are read from the database.
 */
@ReadingConverter
public class ReadingCollectionLocationConverter implements Converter<String, CollectionLocation> {

    @Override
    public CollectionLocation convert(final String collectionLocation) {
        return CollectionLocation.valueOf(collectionLocation.toUpperCase());
    }
}
