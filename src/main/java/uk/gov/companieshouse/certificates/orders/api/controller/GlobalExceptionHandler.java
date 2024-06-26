package uk.gov.companieshouse.certificates.orders.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.certificates.orders.api.util.FieldNameConverter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final FieldNameConverter converter;

    public GlobalExceptionHandler(FieldNameConverter converter) {
        this.converter = converter;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
             @NonNull final MethodArgumentNotValidException ex,
             @NonNull final HttpHeaders headers,
             @NonNull final HttpStatusCode status,
             @NonNull final WebRequest request) {

        return ApiErrors.errorResponse(HttpStatus.BAD_REQUEST, buildBadRequestApiError(ex));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            final HttpMessageNotReadableException ex,
            @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request) {

        if (ex.getCause() instanceof JsonProcessingException jsonProcessingException) {
            final ApiResponse<List<ApiError>> apiResponse = new ApiResponse<>(Collections.singletonList(ApiErrors.raiseError(ApiErrors.ERR_JSON_PROCESSING, (jsonProcessingException.getOriginalMessage()))));
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

        BindingResult bindingResult = ex.getBindingResult();
        return Stream.concat(bindingResult.getFieldErrors().stream()
                                .map(field -> raiseBadRequestError(field, field.getField())),
                        bindingResult.getGlobalErrors().stream()
                                .map(field -> raiseBadRequestError(field, field.getObjectName())))
                .sorted(Comparator.comparing(ApiError::getError))
                .toList();
    }

    private ApiError raiseBadRequestError(ObjectError error, String objectName) {
        return ApiErrors.raiseError(new ApiError(converter.fromCamelToLowerHyphenCase(objectName) + "-error", converter.fromUpperCamelToSnakeCase(objectName), ApiErrors.OBJECT_LOCATION_TYPE, ApiErrors.ERROR_TYPE_VALIDATION), error.getDefaultMessage());
    }
}
