package uk.gov.companieshouse.certificates.orders.api.controller;

import uk.gov.companieshouse.api.error.ApiError;

class ApiErrors {
    static final uk.gov.companieshouse.api.error.ApiError ERR_COMPANY_NOT_FOUND = new uk.gov.companieshouse.api.error.ApiError("company-not-found", "company_number", "string", "ch:validation");
    static final uk.gov.companieshouse.api.error.ApiError ERR_COMPANY_NUMBER_IS_NULL = new uk.gov.companieshouse.api.error.ApiError("company-number-is-null", "company_number", "string", "ch:validation");
    static final uk.gov.companieshouse.api.error.ApiError ERR_SERVICE_UNAVAILABLE = new ApiError("company-service-unavailable", "company_number", "string", "ch:service");
    static final uk.gov.companieshouse.api.error.ApiError ERR_COMPANY_STATUS_INVALID = new ApiError("company-status-invalid", "company_number", "string", "ch:validation");
}
