package uk.gov.companieshouse.certificates.orders.api.validator;

public enum CompanyType {
    LIMITED_PARTNERSHIP("limited-partnership"),
    LIMITED_LIABILITY_PARTNERSHIP("llp");

    private final String type;

    CompanyType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
