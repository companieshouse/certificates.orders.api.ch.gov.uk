package uk.gov.companieshouse.certificates.orders.api.converter;

import jakarta.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.data.convert.WritingConverter;

import static uk.gov.companieshouse.certificates.orders.api.converter.EnumValueNameConverter.convertEnumValueNameToJson;

@WritingConverter
public final class EnumToStringConverterFactory implements ConverterFactory<Enum<?>, String> {

    @Nonnull
    @Override
    public <T extends String> Converter<Enum<?>, T> getConverter(@Nonnull Class<T> targetType) {
        return new EnumToStringConverter<>(targetType);
    }

    private record EnumToStringConverter<T extends String>(Class<T> targetType) implements Converter<Enum<?>, T> {

        public T convert(@Nonnull Enum source) {
                return targetType.cast(convertEnumValueNameToJson(source));
            }
        }
}
