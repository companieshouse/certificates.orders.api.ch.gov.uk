package uk.gov.companieshouse.items.orders.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import uk.gov.companieshouse.items.orders.api.model.IncludeAddressRecordsType;

/**
 * Implements {@link Converter} to alter the way the include address records type values are read from the database.
 */
@ReadingConverter
public class ReadingIncludeAddressRecordsTypeConverter implements Converter<String, IncludeAddressRecordsType> {

    @Override
    public IncludeAddressRecordsType convert(final String includeAddressRecordsType) {
        return IncludeAddressRecordsType.valueOf(includeAddressRecordsType.toUpperCase());
    }
}
