package uk.gov.companieshouse.items.orders.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale;

/**
 * Implements {@link Converter} to alter the way the timescale values are written to the database.
 */
@WritingConverter
public class WritingDeliveryTimescaleConverter implements Converter<DeliveryTimescale, String> {

    @Override
    public String convert(final DeliveryTimescale deliveryTimescale) {
        return deliveryTimescale.name().toLowerCase();
    }
}
