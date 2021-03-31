package uk.gov.companieshouse.certificates.orders.api.config;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import uk.gov.companieshouse.api.interceptor.CRUDAuthenticationInterceptor;
import uk.gov.companieshouse.certificates.orders.api.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.certificates.orders.api.interceptor.UserAuthenticationInterceptor;
import uk.gov.companieshouse.certificates.orders.api.interceptor.UserAuthorisationInterceptor;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigurationTest {

    @Mock
    private LoggingInterceptor loggingInterceptor;

    @Mock
    private UserAuthenticationInterceptor userAuthenticationInterceptor;

    @Mock
    private UserAuthorisationInterceptor userAuthorisationInterceptor;

    @Mock
    private CRUDAuthenticationInterceptor crudPermissionInterceptor;

    @InjectMocks
    private ApplicationConfiguration config;

    @Test
    void addInterceptors() {
        InterceptorRegistry registry = Mockito.mock(InterceptorRegistry.class);

        InterceptorRegistration loggingInterceptorRegistration = Mockito.mock(InterceptorRegistration.class);
        doReturn(loggingInterceptorRegistration).when(registry).addInterceptor(loggingInterceptor);

        InterceptorRegistration crudPermissionInterceptorRegistration = Mockito.mock(InterceptorRegistration.class);
        doReturn(crudPermissionInterceptorRegistration).when(registry).addInterceptor(crudPermissionInterceptor);

        InterceptorRegistration userAuthenticationInterceptorRegistration = Mockito.mock(InterceptorRegistration.class);
        doReturn(userAuthenticationInterceptorRegistration).when(registry)
                .addInterceptor(userAuthenticationInterceptor);

        InterceptorRegistration userAuthorisationInterceptorRegistration = Mockito.mock(InterceptorRegistration.class);
        doReturn(userAuthorisationInterceptorRegistration).when(registry).addInterceptor(userAuthorisationInterceptor);

        config.addInterceptors(registry);

        verifyNoMoreInteractions(loggingInterceptorRegistration);
        verify(userAuthenticationInterceptorRegistration).addPathPatterns("/orderable/**");
        verify(userAuthorisationInterceptorRegistration).addPathPatterns("/orderable/certificates/**");
        verify(crudPermissionInterceptorRegistration).addPathPatterns("/orderable/**");

        verifyNoMoreInteractions(loggingInterceptorRegistration);
        verifyNoMoreInteractions(userAuthenticationInterceptorRegistration);
        verifyNoMoreInteractions(userAuthorisationInterceptorRegistration);
        verifyNoMoreInteractions(crudPermissionInterceptorRegistration);

        InOrder inOrder = Mockito.inOrder(registry);
        inOrder.verify(registry).addInterceptor(loggingInterceptor);
        inOrder.verify(registry).addInterceptor(crudPermissionInterceptor);
        inOrder.verify(registry).addInterceptor(userAuthenticationInterceptor);
        inOrder.verify(registry).addInterceptor(userAuthorisationInterceptor);
    }

}