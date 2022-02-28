package uk.gov.companieshouse.certificates.orders.api.util;

import uk.gov.companieshouse.api.error.ApiError;

import java.util.HashMap;
import java.util.Map;

public class ApiErrorBuilder {
    /** Defines ApiError value containing an error message */
    public static final String ERROR_MESSAGE_FIELD = "error_message";
    private final Map<String, String> errorValues = new HashMap<>();
    private final ApiError apiError;

    private ApiErrorBuilder(ApiError apiError) {
        this.apiError = apiError;
    }

    public static ApiErrorBuilder builder(ApiError apiError) {
        return new ApiErrorBuilder(apiError);
    }

    public ApiErrorBuilder withErrorMessage(String errorMessage) {
        this.errorValues.put(ERROR_MESSAGE_FIELD, errorMessage);
        return this;
    }

    public ApiError build() {
        ApiError result = new ApiError(this.apiError);
        result.setErrorValues(errorValues);
        return result;
    }
}
