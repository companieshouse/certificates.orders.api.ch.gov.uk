package uk.gov.companieshouse.items.orders.api.model;

import com.google.gson.Gson;

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
}
