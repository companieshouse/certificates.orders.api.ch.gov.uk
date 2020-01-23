package uk.gov.companieshouse.items.orders.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale;

/**
 * Implements {@link Converter} to alter the way the timescale values are read from the database.
 */
@ReadingConverter
public class ReadingDeliveryTimescaleConverter implements Converter<String, DeliveryTimescale> {

    @Override
    public DeliveryTimescale convert(final String deliveryTimescale) {
        return DeliveryTimescale.valueOf(deliveryTimescale.toUpperCase());
    }
}
