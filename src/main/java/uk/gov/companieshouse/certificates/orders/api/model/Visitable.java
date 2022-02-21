package uk.gov.companieshouse.certificates.orders.api.model;

import java.util.function.Consumer;

public interface Visitable<T> {
    void accept(Visitor<T> visitor);
}
