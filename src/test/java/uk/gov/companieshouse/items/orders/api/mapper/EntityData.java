package uk.gov.companieshouse.items.orders.api.mapper;

class EntityData {
    private String datum;

    public EntityData() {
    }

    public EntityData(String datum) {
        this.datum = datum;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }
}
