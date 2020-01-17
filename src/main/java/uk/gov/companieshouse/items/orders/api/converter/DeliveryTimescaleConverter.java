package uk.gov.companieshouse.items.orders.api.converter;

import org.springframework.core.convert.converter.Converter;
import uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale;

/**
 * Extends {@link Converter} to alter the way the timescale values are written to the database.
 */
public class DeliveryTimescaleConverter implements Converter<DeliveryTimescale, String> {

    @Override
    public String convert(DeliveryTimescale source) {
        return source.name().toLowerCase();
    }
}
