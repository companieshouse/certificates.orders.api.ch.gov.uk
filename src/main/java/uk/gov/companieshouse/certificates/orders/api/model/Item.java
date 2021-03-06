package uk.gov.companieshouse.certificates.orders.api.model;

import com.google.gson.Gson;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * An instance of this represents an item of any type.
 */
public class Item {

    @Transient
    public static final String SEQUENCE_NAME = "items_sequence";

    @Id
    private String id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private ItemData data  = new ItemData();

    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        data.setId(id);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ItemData getData() {
        return data;
    }

    public void setData(ItemData data) {
        this.data = data;
    }

    public String getCompanyName() {
        return data.getCompanyName();
    }

    public void setCompanyName(String companyName) {
        data.setCompanyName(companyName);
    }

    public String getCompanyNumber() {
        return data.getCompanyNumber();
    }

    public void setCompanyNumber(String companyNumber) {
        data.setCompanyNumber(companyNumber);
    }

    public String getCustomerReference() {
        return data.getCustomerReference();
    }

    public void setCustomerReference(String companyReference) {
        data.setCustomerReference(companyReference);
    }

    public String getDescription() {
        return data.getDescription();
    }

    public void setDescription(String description) {
        data.setDescription(description);
    }

    public String getDescriptionIdentifier() {
        return data.getDescriptionIdentifier();
    }

    public void setDescriptionIdentifier(String descriptionIdentifier) {
        data.setDescriptionIdentifier(descriptionIdentifier);
    }

    public Map<String, String> getDescriptionValues() {
        return data.getDescriptionValues();
    }

    public void setDescriptionValues(Map<String, String> descriptionValues) {
        data.setDescriptionValues(descriptionValues);
    }

    public List<ItemCosts> getItemCosts() {
        return data.getItemCosts();
    }

    public void setItemCosts(List<ItemCosts> itemCosts) {
        data.setItemCosts(itemCosts);
    }

    public CertificateItemOptions getItemOptions() {
        return data.getItemOptions();
    }

    public void setItemOptions(CertificateItemOptions itemOptions) {
        data.setItemOptions(itemOptions);
    }

    public String getEtag() {
        return data.getEtag();
    }

    public void setEtag(String etag) {
        data.setEtag(etag);
    }

    public String getKind() {
        return data.getKind();
    }

    public void setKind(String kind) {
        data.setKind(kind);
    }

    public Links getLinks() {
        return data.getLinks();
    }

    public void setLinks(Links links) {
        data.setLinks(links);
    }

    public String getPostageCost() {
        return data.getPostageCost();
    }

    public void setPostageCost(String postageCost) {
        data.setPostageCost(postageCost);
    }

    public Boolean isPostalDelivery() {
        return data.isPostalDelivery();
    }

    public void setPostalDelivery(boolean postalDelivery) {
        data.setPostalDelivery(postalDelivery);
    }

    public Integer getQuantity() {
        return data.getQuantity();
    }

    public void setQuantity(Integer quantity) {
        data.setQuantity(quantity);
    }

    public String getTotalItemCost() {
        return data.getTotalItemCost();
    }

    public void setTotalItemCost(String totalItemCost) {
        data.setTotalItemCost(totalItemCost);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() { return new Gson().toJson(this); }
}
