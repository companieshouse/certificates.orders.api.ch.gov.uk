package uk.gov.companieshouse.items.orders.api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.companieshouse.items.orders.api.util.FieldNameConverter;

import java.util.ArrayList;
import java.util.List;

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
        final ApiError apiError = buildBadRequestApiError(ex);
        return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
    }

    /**
     * Uility to build ApiError from MethodArgumentNotValidException.
     * @param ex the MethodArgumentNotValidException handled
     * @return the resulting ApiError
     */
    ApiError buildBadRequestApiError(final MethodArgumentNotValidException ex) {
        final List<String> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(converter.toSnakeCase(error.getField()) + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        return new ApiError(HttpStatus.BAD_REQUEST, errors);
    }

}
