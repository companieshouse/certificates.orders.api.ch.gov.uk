package uk.gov.companieshouse.items.orders.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import uk.gov.companieshouse.items.orders.api.model.CertificateType;

/**
 * Implements {@link Converter} to alter the way the type values are written to the database.
 */
@WritingConverter
public class WritingCertificateTypeConverter implements Converter<CertificateType, String> {

    @Override
    public String convert(final CertificateType certificateType) {
        return certificateType.name().toLowerCase();
    }
}
