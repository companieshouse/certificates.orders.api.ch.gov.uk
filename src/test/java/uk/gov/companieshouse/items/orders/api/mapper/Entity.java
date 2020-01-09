package uk.gov.companieshouse.items.orders.api.mapper;

class Entity {
    private String id;
    private EntityData data;

    public Entity() {
        data = new EntityData();
    }

    public Entity(String id, EntityData data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EntityData getData() {
        return data;
    }

    public void setData(EntityData data) {
        this.data = data;
    }

    public String getDatum() {
        return data.getDatum();
    }

    public void setDatum(String datum) {
        data.setDatum(datum);
    }
}
