package uk.gov.companieshouse.items.orders.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import uk.gov.companieshouse.items.orders.api.model.CollectionLocation;

/**
 * Implements {@link Converter} to alter the way the collection location values are written to the database.
 */
@WritingConverter
public class WritingCollectionLocationConverter implements Converter<CollectionLocation, String> {

    @Override
    public String convert(final CollectionLocation collectionLocation) {
        return collectionLocation.name().toLowerCase();
    }
}
