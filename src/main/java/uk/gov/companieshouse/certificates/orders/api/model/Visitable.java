package uk.gov.companieshouse.certificates.orders.api.model;

public interface Visitable<T> {
    void accept(Visitor<T> visitor);
}
