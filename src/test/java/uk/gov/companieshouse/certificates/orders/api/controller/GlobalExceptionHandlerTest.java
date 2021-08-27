package uk.gov.companieshouse.certificates.orders.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import uk.gov.companieshouse.certificates.orders.api.util.FieldNameConverter;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.HttpStatus.MULTI_STATUS;

/**
 * Unit tests the {@link GlobalExceptionHandler} class.
 */
@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    private static final String OBJECT1 =  "object1";
    private static final String OBJECT2 =  "object2";
    private static final String FIELD1 =   "field1";
    private static final String MESSAGE1 = "message1";
    private static final String MESSAGE2 = "message2";
    private static final String ORIGINAL_MESSAGE = "original";
    private static final HttpStatus ORIGINAL_STATUS = MULTI_STATUS;

    /**
     * Extends {@link GlobalExceptionHandler} to facilitate its unit testing.
     */
    private static final class TestGlobalExceptionHandler extends GlobalExceptionHandler {

        public TestGlobalExceptionHandler(FieldNameConverter converter) {
            super(converter);
        }

        @Override
        protected ResponseEntity<Object> handleExceptionInternal(final Exception ex,
                                                                 final Object body,
                                                                 final HttpHeaders headers,
                                                                 final HttpStatus status,
                                                                 final WebRequest request) {
            return new ResponseEntity<>(body, status);
        }
    }

    @InjectMocks
    private TestGlobalExceptionHandler handlerUnderTest;

    @Mock
    private MethodArgumentNotValidException mex;

    @Mock
    private HttpMessageNotReadableException hex;

    @Mock
    private JsonProcessingException jpe;

    @Mock
    private BindingResult result;

    @Mock
    private FieldNameConverter converter;

    @Mock
    private HttpHeaders headers;

    @Mock
    private WebRequest request;

    @Test
    void buildsApiErrorFromMethodArgumentNotValidException() {

        // Given
        when(mex.getBindingResult()).thenReturn(result);
        when(result.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError(OBJECT1, FIELD1, MESSAGE1)));
        when(result.getGlobalErrors()).thenReturn(Collections.singletonList(new ObjectError(OBJECT2, MESSAGE2)));
        when(converter.toSnakeCase(FIELD1)).thenReturn(FIELD1);

        // When
        final ResponseEntity<Object> response = handlerUnderTest.handleMethodArgumentNotValid(mex, headers, ORIGINAL_STATUS, request);

        // Then
        final ApiError error = (ApiError) response.getBody();
        assertThat(error, is(notNullValue()));
        assertThat(error.getStatus(), is(HttpStatus.BAD_REQUEST));
        assertThat(error.getErrors().stream()
                .anyMatch(o -> o.equals(FIELD1 + ": " + MESSAGE1)), is(true));
        assertThat(error.getErrors().stream()
                .anyMatch(o -> o.equals(OBJECT2 + ": " + MESSAGE2)), is(true));
    }

    @Test
    void buildsApiErrorFromJsonProcessingException() {

        // Given
        when(hex.getCause()).thenReturn(jpe);
        when(jpe.getOriginalMessage()).thenReturn(ORIGINAL_MESSAGE);

        // When
        final ResponseEntity<Object> response =
                handlerUnderTest.handleHttpMessageNotReadable(hex, headers, ORIGINAL_STATUS, request);

        // Then
        final ApiError error = (ApiError) response.getBody();
        assertThat(error, is(notNullValue()));
        assertThat(error.getStatus(), is(HttpStatus.BAD_REQUEST));
        assertThat(error.getErrors().get(0), is(ORIGINAL_MESSAGE));
    }

    @Test
    void delegatesHandlingOfNonJsonProcessingExceptionsToSpring() {

        // Given
        when(hex.getCause()).thenReturn(hex);

        // When

        final ResponseEntity<Object> response =
                handlerUnderTest.handleHttpMessageNotReadable(hex, headers, ORIGINAL_STATUS, request);

        // Then
        // Note these assertions are testing behaviour implemented in the Spring framework.
        assertThat(response.getStatusCode(), is(ORIGINAL_STATUS));
        assertThat(response.getBody(), is(nullValue()));
    }


}
