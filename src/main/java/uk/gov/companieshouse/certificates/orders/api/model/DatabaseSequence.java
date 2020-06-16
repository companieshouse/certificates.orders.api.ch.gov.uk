package uk.gov.companieshouse.certificates.orders.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *  An instance of this is used to track the ID value assigned to the latest
 *  item to have been created so that IDs can be generated in sequence as per
 *  https://www.baeldung.com/spring-boot-mongodb-auto-generated-field.
 */
@Document(collection = "database_sequences")
public class DatabaseSequence {

    @Id
    private String id;

    private long seq;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }
}
