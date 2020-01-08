package uk.gov.companieshouse.items.orders.api.mapper;

class Dto {
    private String id;
    private String datum;

    public Dto() {
    }

    public Dto(String id, String datum) {
        this.id = id;
        this.datum = datum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }
}
