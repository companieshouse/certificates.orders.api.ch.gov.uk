package uk.gov.companieshouse.certificates.orders.api.model;

public interface BasicInformationIncludable<T> extends Visitable<T> {
    Boolean getIncludeBasicInformation();
}
