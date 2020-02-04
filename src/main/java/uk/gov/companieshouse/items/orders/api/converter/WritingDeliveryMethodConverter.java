package uk.gov.companieshouse.items.orders.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import uk.gov.companieshouse.items.orders.api.model.DeliveryMethod;

/**
 * Implements {@link Converter} to alter the way the delivery method values are written to the database.
 */
@WritingConverter
public class WritingDeliveryMethodConverter implements Converter<DeliveryMethod, String> {

    @Override
    public String convert(final DeliveryMethod deliveryMethod) {
        return deliveryMethod.name().toLowerCase();
    }
}
