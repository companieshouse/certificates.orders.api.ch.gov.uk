package uk.gov.companieshouse.items.orders.api.config;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import javax.annotation.Nonnull;

import java.io.IOException;

/**
 * Workaround to allow use of YAML properties files. See
 * https://stackoverflow.com/questions/21271468/spring-propertysource-using-yaml.
 */
public class YamlPropertyLoaderFactory extends DefaultPropertySourceFactory {

    /** The index of the only property source available. This assumes there are no Spring profiles in use. */
    private static final int ONLY_PROPERTY_SOURCE = 0;

    @Override
    public PropertySource<?> createPropertySource(final String name, final @Nonnull EncodedResource resource)
            throws IOException {
        final Resource res = resource.getResource();
        return new YamlPropertySourceLoader().load(res.getFilename(), res).get(ONLY_PROPERTY_SOURCE);
    }
}
