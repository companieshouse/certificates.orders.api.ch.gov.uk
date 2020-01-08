package uk.gov.companieshouse.items.orders.api.mapper;

class Entity {
    private String id;
    private EntityData data;

    public Entity() {
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
}
