package uk.gov.companieshouse.items.orders.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import uk.gov.companieshouse.items.orders.api.model.IncludeAddressRecordsType;

/**
 * Implements {@link Converter} to alter the way the include address records type values are written to the database.
 */
@WritingConverter
public class WritingIncludeAddressRecordsTypeConverter implements Converter<IncludeAddressRecordsType, String> {

    @Override
    public String convert(final IncludeAddressRecordsType includeAddressRecordsType) {
        return includeAddressRecordsType.name().toLowerCase();
    }
}
