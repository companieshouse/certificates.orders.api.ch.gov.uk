package uk.gov.companieshouse.items.orders.api.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import uk.gov.companieshouse.items.orders.api.util.FieldNameConverter;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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

    @InjectMocks
    private GlobalExceptionHandler handlerUnderTest;

    @Mock
    private MethodArgumentNotValidException ex;

    @Mock
    private BindingResult result;

    @Mock
    private FieldNameConverter converter;

    @Test
    void buildsApiError() {

        when(ex.getBindingResult()).thenReturn(result);
        when(result.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError(OBJECT1, FIELD1, MESSAGE1)));
        when(result.getGlobalErrors()).thenReturn(Collections.singletonList(new ObjectError(OBJECT2, MESSAGE2)));
        when(converter.toSnakeCase(FIELD1)).thenReturn(FIELD1);

        final ApiError error = handlerUnderTest.buildBadRequestApiError(ex);
        assertThat(error.getStatus(), is(HttpStatus.BAD_REQUEST));
        assertThat(error.getErrors().stream()
                .anyMatch(o -> o.equals(FIELD1 + ": " + MESSAGE1)), is(true));
        assertThat(error.getErrors().stream()
                .anyMatch(o -> o.equals(OBJECT2 + ": " + MESSAGE2)), is(true));
    }


}
