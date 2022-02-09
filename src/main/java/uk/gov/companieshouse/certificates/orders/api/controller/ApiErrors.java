package uk.gov.companieshouse.certificates.orders.api.controller;

import uk.gov.companieshouse.api.error.ApiError;

final class ApiErrors {

    public static final String ERROR_TYPE_VALIDATION = "ch:validation";
    public static final String STRING_LOCATION_TYPE = "string";

    private static final String COMPANY_NUMBER_LOCATION = "company_number";
    private static final String ERROR_TYPE_SERVICE = "ch:service";
    private static final String COMPANY_NOT_FOUND_ERROR = "company-not-found";
    private static final String COMPANY_NUMBER_IS_NULL_ERROR = "company-number-is-null";
    private static final String COMPANY_SERVICE_UNAVAILABLE_ERROR = "company-service-unavailable";
    private static final String COMPANY_STATUS_INVALID_ERROR = "company-status-invalid";

    private static final String INVALID_COMPANY_TYPE_ERROR = "invalid-company-type";
    private static final String COMPANY_TYPE_LOCATION = "company_type";

    static final ApiError ERR_COMPANY_NOT_FOUND = new ApiError(COMPANY_NOT_FOUND_ERROR, COMPANY_NUMBER_LOCATION, STRING_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    static final ApiError ERR_COMPANY_NUMBER_IS_NULL = new ApiError(COMPANY_NUMBER_IS_NULL_ERROR, COMPANY_NUMBER_LOCATION, STRING_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    static final ApiError ERR_SERVICE_UNAVAILABLE = new ApiError(COMPANY_SERVICE_UNAVAILABLE_ERROR, COMPANY_NUMBER_LOCATION, STRING_LOCATION_TYPE, ERROR_TYPE_SERVICE);
    static final ApiError ERR_COMPANY_STATUS_INVALID = new ApiError(COMPANY_STATUS_INVALID_ERROR, COMPANY_NUMBER_LOCATION, STRING_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    static final ApiError ERR_INVALID_COMPANY_TYPE = new ApiError(INVALID_COMPANY_TYPE_ERROR, COMPANY_TYPE_LOCATION, STRING_LOCATION_TYPE, ERROR_TYPE_VALIDATION);

    private ApiErrors() {}
}
