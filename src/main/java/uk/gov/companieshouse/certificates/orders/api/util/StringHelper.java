package uk.gov.companieshouse.certificates.orders.api.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public final class StringHelper {

    public Set<String> asSet(String regexDelim, String values) {
        return Stream.of(Optional.ofNullable(values).orElse("").split(regexDelim))
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toSet());
    }
}