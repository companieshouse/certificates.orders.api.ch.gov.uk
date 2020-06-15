package uk.gov.companieshouse.certificates.orders.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public final class EnumToStringConverterFactory implements ConverterFactory<Enum, String> {

    @Override
    public <T extends String> Converter<Enum, T> getConverter(Class<T> targetType) {
        return new EnumToStringConverter();
    }

    private final class EnumToStringConverter<T extends Enum> implements Converter<T, String> {
        public String convert(T source) {
            return EnumValueNameConverter.convertEnumValueNameToJson(source);
        }
    }
}
