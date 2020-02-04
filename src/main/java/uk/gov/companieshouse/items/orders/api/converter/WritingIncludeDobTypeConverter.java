package uk.gov.companieshouse.items.orders.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import uk.gov.companieshouse.items.orders.api.model.IncludeDobType;

/**
 * Implements {@link Converter} to alter the way the include DOB type values are written to the database.
 */
@WritingConverter
public class WritingIncludeDobTypeConverter implements Converter<IncludeDobType, String> {

    @Override
    public String convert(final IncludeDobType includeDobType) {
        return includeDobType.name().toLowerCase();
    }
}
