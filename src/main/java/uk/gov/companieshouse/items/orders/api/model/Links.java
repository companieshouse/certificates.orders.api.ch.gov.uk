package uk.gov.companieshouse.items.orders.api.model;

import com.google.gson.Gson;

import java.util.Objects;

/**
 * An instance of this represents the links for an item.
 */
public class Links {

    private String self;

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    @Override
    public String toString() { return new Gson().toJson(this); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Links)) return false;
        Links links = (Links) o;
        return Objects.equals(self, links.self);
    }

    @Override
    public int hashCode() {
        return Objects.hash(self);
    }
}
