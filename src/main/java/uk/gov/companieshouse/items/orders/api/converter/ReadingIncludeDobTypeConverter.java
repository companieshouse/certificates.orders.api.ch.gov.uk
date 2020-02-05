package uk.gov.companieshouse.items.orders.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import uk.gov.companieshouse.items.orders.api.model.IncludeDobType;

/**
 * Implements {@link Converter} to alter the way the include DOB type values are read from the database.
 */
@ReadingConverter
public class ReadingIncludeDobTypeConverter implements Converter<String, IncludeDobType> {

    @Override
    public IncludeDobType convert(final String includeDobType) {
        return IncludeDobType.valueOf(includeDobType.toUpperCase());
    }
}
