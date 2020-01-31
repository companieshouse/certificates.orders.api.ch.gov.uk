package uk.gov.companieshouse.items.orders.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import uk.gov.companieshouse.items.orders.api.model.CertificateType;

/**
 * Implements {@link Converter} to alter the way the type values are read from the database.
 */
@ReadingConverter
public class ReadingCertificateTypeConverter implements Converter<String, CertificateType> {

    @Override
    public CertificateType convert(final String certificateType) {
        return CertificateType.valueOf(certificateType.toUpperCase());
    }
}
