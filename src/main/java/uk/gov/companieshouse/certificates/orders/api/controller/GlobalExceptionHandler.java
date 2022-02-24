package uk.gov.companieshouse.certificates.orders.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.certificates.orders.api.util.FieldNameConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static uk.gov.companieshouse.certificates.orders.api.controller.ApiErrors.BAD_REQUEST_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final FieldNameConverter converter;

    public GlobalExceptionHandler(FieldNameConverter converter) {
        this.converter = converter;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request) {

        return ApiErrors.errorResponse(HttpStatus.BAD_REQUEST, buildBadRequestApiError(ex));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            final HttpMessageNotReadableException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request) {

        if (ex.getCause() instanceof JsonProcessingException) {
            final ApiResponse<List<ApiError>> apiResponse = new ApiResponse<>(Collections.singletonList(ApiErrors.raiseError(ApiErrors.ERR_JSON_PROCESSING, ((JsonProcessingException) ex.getCause()).getOriginalMessage())));
            return handleExceptionInternal(ex, apiResponse, headers, HttpStatus.BAD_REQUEST, request);
        }

        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    /**
     * Utility to build ApiError from MethodArgumentNotValidException.
     *
     * @param ex the MethodArgumentNotValidException handled
     * @return the resulting ApiError
     */
    List<ApiError> buildBadRequestApiError(final MethodArgumentNotValidException ex) {
        final List<ApiError> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            raiseBadRequestError(error, errors, error.getField());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            raiseBadRequestError(error, errors, error.getObjectName());
        }

        return errors;
    }

    private void raiseBadRequestError(ObjectError error, List<ApiError> errors, String objectName) {
        errors.add(ApiErrors.raiseError(new ApiError(BAD_REQUEST_ERROR, converter.toSnakeCase(objectName), ApiErrors.OBJECT_LOCATION_TYPE, ApiErrors.ERROR_TYPE_VALIDATION), error.getDefaultMessage()));
    }
}
