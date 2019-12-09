package uk.gov.companieshouse.items.orders.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * Custom configuration for Mongo added so that _class attributes are not saved on objects stored in MongoDB.
 */
@Configuration
public class MongoConfig {

    /**
     * _class maps to the model class in mongoDB (e.g. _class : uk.gov.companieshouse.items.orders.api.model.CertificateItem)
     * when using spring data mongo it by default adds a _class key to your collection to be able to
     * handle inheritance. But if your domain model is simple and flat, you can remove it by overriding
     * the default MappingMongoConverter.
     */
    @Bean
    public MappingMongoConverter mappingMongoConverter(final MongoDbFactory factory,
                                                       final MongoMappingContext context) {
        final DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        final MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);

        // Don't save _class to mongo
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return mappingConverter;
    }
}
