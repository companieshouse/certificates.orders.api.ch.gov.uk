package uk.gov.companieshouse.certificates.orders.api.config;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import jakarta.annotation.Nonnull;
import org.springframework.lang.NonNull;

import java.io.IOException;

/**
 * Workaround to allow use of YAML properties files. See
 * <a href="https://stackoverflow.com/questions/21271468/spring-propertysource-using-yaml">...</a>.
 */
public class YamlPropertyLoaderFactory extends DefaultPropertySourceFactory {

    @Override
    @NonNull
    public PropertySource<?> createPropertySource(final String name, final @Nonnull EncodedResource resource)
            throws IOException {
        final Resource res = resource.getResource();
        return new YamlPropertySourceLoader().load(res.getFilename(), res).getFirst();
    }
}
