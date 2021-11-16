package uk.gov.companieshouse.certificates.orders.api.converter;

import javax.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.data.convert.WritingConverter;

import static uk.gov.companieshouse.certificates.orders.api.converter.EnumValueNameConverter.convertEnumValueNameToJson;

@WritingConverter
public final class EnumToStringConverterFactory implements ConverterFactory<Enum<?>, String> {

    @Override
    @Nonnull
    public <T extends String> Converter<Enum<?>, T> getConverter(@Nonnull Class<T> targetType) {
        return new EnumToStringConverter<>();
    }

    private static class EnumToStringConverter<T extends String> implements Converter<Enum<?>, T> {

        @SuppressWarnings("unchecked")
        public T convert(@Nonnull Enum source) {
            return (T) convertEnumValueNameToJson(source);
        }
    }
}
