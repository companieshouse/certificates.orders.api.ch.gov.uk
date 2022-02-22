package uk.gov.companieshouse.certificates.orders.api.model;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class LiquidatorsDetails implements BasicInformationIncludable<Map<String, Object>> {
    private Boolean includeBasicInformation;
    private final Map<String, Object> fieldValues = Collections.emptyMap();

    @Override
    public Boolean getIncludeBasicInformation() {
        return includeBasicInformation;
    }

    public void setIncludeBasicInformation(Boolean includeBasicInformation) {
        this.includeBasicInformation = includeBasicInformation;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LiquidatorsDetails that = (LiquidatorsDetails) o;
        return Objects.equals(includeBasicInformation, that.includeBasicInformation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(includeBasicInformation);
    }

    @Override
    public void accept(Visitor<Map<String, Object>> visitor) {
        visitor.visit(fieldValues);
    }
}
