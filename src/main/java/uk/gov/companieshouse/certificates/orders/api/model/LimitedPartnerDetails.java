package uk.gov.companieshouse.certificates.orders.api.model;

import com.google.gson.Gson;
import org.springframework.data.annotation.Transient;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * An instance of this represents the limited partner details item options selected.
 */
public class LimitedPartnerDetails implements BasicInformationIncludable<Map<String, Object>> {

    private Boolean includeBasicInformation;
    @Transient
    private final Map<String, Object> fieldValues = Collections.emptyMap();

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
        LimitedPartnerDetails that = (LimitedPartnerDetails) o;
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
