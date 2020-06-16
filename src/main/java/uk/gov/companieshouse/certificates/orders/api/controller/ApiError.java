package uk.gov.companieshouse.certificates.orders.api.controller;

import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Wraps up status and list of messages for rendering in non-2xx REST response payload.
 */
public class ApiError {

    private final HttpStatus status;
    private final List<String> errors;

    public ApiError(final HttpStatus status, final List<String> errors) {
        super();
        this.status = status;
        this.errors = errors;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public List<String> getErrors() {
        return errors;
    }

}
