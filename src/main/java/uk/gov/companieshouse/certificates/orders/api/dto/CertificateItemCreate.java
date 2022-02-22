package uk.gov.companieshouse.certificates.orders.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.Gson;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptionsRequest;
import uk.gov.companieshouse.certificates.orders.api.model.ItemCosts;
import uk.gov.companieshouse.certificates.orders.api.model.Links;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;
import java.util.Map;

/**
 * An instance of this represents the JSON serializable certificate item for use in REST requests and responses.
 */
@JsonPropertyOrder(alphabetic = true)
public class CertificateItemCreate {
    @NotNull
    @JsonProperty("item_options")
    private CertificateItemOptionsRequest itemOptions;

    @NotNull
    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("customer_reference")
    private String customerReference;

    @Null
    @JsonProperty("item_costs")
    private List<ItemCosts> itemCosts;

    @Null
    @JsonProperty("etag")
    private String etag;

    @JsonProperty("kind")
    private String kind;

    private boolean isPostalDelivery;
    @Min(1)
    @JsonProperty("quantity")
    private int quantity;

    @Null
    @JsonProperty("description")
    private String description;

    @Null
    @JsonProperty("description_identifier")
    private String descriptionIdentifier;

    @Null
    @JsonProperty("description_values")
    private Map<String, String> descriptionValues;

    @Null
    @JsonProperty("links")
    private Links links;

    @Null
    @JsonProperty("postage_cost")
    private String postageCost;

    @Null
    @JsonProperty("total_item_cost")
    private String totalItemCost;

    public CertificateItemOptionsRequest getItemOptions() {
        return itemOptions;
    }

    public void setItemOptions(CertificateItemOptionsRequest itemOptions) {
        this.itemOptions = itemOptions;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public String getCustomerReference() {
        return customerReference;
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

    @Override
    public String toString() { return new Gson().toJson(this); }

}
