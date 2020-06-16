package uk.gov.companieshouse.certificates.orders.api.service;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.certificates.orders.api.model.DatabaseSequence;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Generates db sequence number values for populating item ids as per
 * https://www.baeldung.com/spring-boot-mongodb-auto-generated-field.
 */
@Service
public class SequenceGeneratorService {

    private final MongoOperations mongoOperations;

    public SequenceGeneratorService(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    long generateSequence(final String seqName) {
        final DatabaseSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), options().returnNew(true).upsert(true),
                DatabaseSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }
}
