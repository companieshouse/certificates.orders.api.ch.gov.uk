package uk.gov.companieshouse.items.orders.api.model;

import com.google.gson.Gson;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

/**
 * An instance of this represents an item of any type.
 */
public class ItemData {

    @Transient
    public static final String SEQUENCE_NAME = "items_sequence";

    @Field("id")
    private String id;

    private String companyName;

    private String companyNumber;

    private String customerReference;

    private String description;

    private String descriptionIdentifier;

    private Map<String, String> descriptionValues;

    private ItemCosts itemCosts;

    private CertificateItemOptions itemOptions;

    private String etag;

    private String kind;

    private Boolean isPostalDelivery;

    private Integer quantity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionIdentifier() {
        return descriptionIdentifier;
    }

    public void setDescriptionIdentifier(String descriptionIdentifier) {
        this.descriptionIdentifier = descriptionIdentifier;
    }

    public Map<String, String> getDescriptionValues() {
        return descriptionValues;
    }

    public void setDescriptionValues(Map<String, String> descriptionValues) {
        this.descriptionValues = descriptionValues;
    }

    public ItemCosts getItemCosts() {
        return itemCosts;
    }

    public void setItemCosts(ItemCosts itemCosts) {
        this.itemCosts = itemCosts;
    }

    public CertificateItemOptions getItemOptions() {
        return itemOptions;
    }

    public void setItemOptions(CertificateItemOptions itemOptions) {
        this.itemOptions = itemOptions;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Boolean isPostalDelivery() {
        return isPostalDelivery;
    }

    public void setPostalDelivery(boolean postalDelivery) {
        isPostalDelivery = postalDelivery;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() { return new Gson().toJson(this); }
}
