package uk.gov.companieshouse.certificates.orders.api.validator;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

class FindByValueVisitorTest {

    @Test
    void testVisitAddsAllEntriesWhenPredicateIsMatched() {
        //given {the predicate will always return true}
        FindByValueVisitor visitor = new FindByValueVisitor(a -> true);

        //when {the map is visited}
        visitor.visit(Collections.singletonMap("key", "value"));

        //then {all keys should be present}
        assertThat(visitor.getKeys(), contains("key"));
    }

    @Test
    void testVisitAddsNoEntriesIfPredicateNotMatched() {
        //given {the predicate will always return false}
        FindByValueVisitor visitor = new FindByValueVisitor(a -> false);

        //when {the map is visited}
        visitor.visit(Collections.singletonMap("key", "value"));

        //then {no keys should be present}
        assertThat(visitor.getKeys(), empty());
    }
}
