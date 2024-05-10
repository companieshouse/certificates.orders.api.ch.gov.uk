package uk.gov.companieshouse.certificates.orders.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

import static uk.gov.companieshouse.certificates.orders.api.converter.EnumValueNameConverter.convertEnumValueJsonToName;

@ReadingConverter
public final class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    @NonNull
    public <T extends Enum> Converter<String, T> getConverter(@NonNull Class<T> targetType) {
        return new StringToEnumConverter(targetType);
    }

    private record StringToEnumConverter<T extends Enum>(Class<T> enumType) implements Converter<String, T> {

        @SuppressWarnings("squid:S1905") // SonarQube false positive - cast is necessary.
            public T convert(@NonNull String source) {
                return (T) Enum.valueOf(this.enumType, convertEnumValueJsonToName(source));
            }
        }
}
