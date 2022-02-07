package uk.gov.companieshouse.certificates.orders.api.service;

public class CompanyNotFoundException extends CompanyServiceException {
    public CompanyNotFoundException() {
    }

    public CompanyNotFoundException(String message) {
        super(message);
    }
}
