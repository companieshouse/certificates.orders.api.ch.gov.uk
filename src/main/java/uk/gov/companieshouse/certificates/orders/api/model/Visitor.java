package uk.gov.companieshouse.certificates.orders.api.model;

import java.util.Map;
import java.util.function.Consumer;

public interface Visitor<T> {
    void visit(T value);
}
