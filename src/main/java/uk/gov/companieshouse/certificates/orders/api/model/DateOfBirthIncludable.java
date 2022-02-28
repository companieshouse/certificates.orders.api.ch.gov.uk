package uk.gov.companieshouse.certificates.orders.api.model;

public interface DateOfBirthIncludable<T> extends BasicInformationIncludable<T> {

    IncludeDobType getIncludeDobType();
}
