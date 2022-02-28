package uk.gov.companieshouse.certificates.orders.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.Gson;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.ItemCosts;
import uk.gov.companieshouse.certificates.orders.api.model.Links;

import java.util.List;
import java.util.Map;

/**
 * An instance of this represents the JSON serializable certificate item for use in REST requests and responses.
 */
@JsonPropertyOrder(alphabetic = true)
public class CertificateItemResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("item_options")
    private CertificateItemOptions itemOptions;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("customer_reference")
    private String customerReference;

    @JsonProperty("user_id")
    private String userId;

    private String description;

    @JsonProperty("description_identifier")
    private String descriptionIdentifier;

    @JsonProperty("description_values")
    private Map<String, String> descriptionValues;

    private Links links;

    @JsonProperty("postage_cost")
    private String postageCost;

    @JsonProperty("total_item_cost")
    private String totalItemCost;

    @JsonProperty("item_costs")
    private List<ItemCosts> itemCosts;

    @JsonProperty("etag")
    private String etag;

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("postal_delivery")
    private boolean isPostalDelivery;

    @JsonProperty("quantity")
    private int quantity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CertificateItemOptions getItemOptions() {
        return itemOptions;
    }

    public void setItemOptions(CertificateItemOptions itemOptions) {
        this.itemOptions = itemOptions;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getPostageCost() {
        return postageCost;
    }

    public void setPostageCost(String postageCost) {
        this.postageCost = postageCost;
    }

    public String getTotalItemCost() {
        return totalItemCost;
    }

    public void setTotalItemCost(String totalItemCost) {
        this.totalItemCost = totalItemCost;
    }

    public List<ItemCosts> getItemCosts() {
        return itemCosts;
    }

    public void setItemCosts(List<ItemCosts> itemCosts) {
        this.itemCosts = itemCosts;
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

    public boolean isPostalDelivery() {
        return isPostalDelivery;
    }

    public void setPostalDelivery(boolean postalDelivery) {
        isPostalDelivery = postalDelivery;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
