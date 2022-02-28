package uk.gov.companieshouse.certificates.orders.api.model;

public interface Visitor<T> {
    void visit(T value);
}
