package uk.gov.companieshouse.items.orders.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import uk.gov.companieshouse.items.orders.api.model.DeliveryMethod;

/**
 * Implements {@link Converter} to alter the way the delivery method values are read from the database.
 */
@ReadingConverter
public class ReadingDeliveryMethodConverter implements Converter<String, DeliveryMethod> {

    @Override
    public DeliveryMethod convert(final String deliveryMethod) {
        return DeliveryMethod.valueOf(deliveryMethod.toUpperCase());
    }
}
