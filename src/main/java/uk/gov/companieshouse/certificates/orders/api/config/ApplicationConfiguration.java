package uk.gov.companieshouse.certificates.orders.api.config;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import uk.gov.companieshouse.api.interceptor.CRUDAuthenticationInterceptor;
import uk.gov.companieshouse.api.util.security.Permission.Key;
import uk.gov.companieshouse.certificates.orders.api.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.certificates.orders.api.interceptor.UserAuthenticationInterceptor;
import uk.gov.companieshouse.certificates.orders.api.interceptor.UserAuthorisationInterceptor;

@Configuration
public class ApplicationConfiguration implements WebMvcConfigurer {

    @Autowired
    private LoggingInterceptor loggingInterceptor;

    @Autowired
    private UserAuthenticationInterceptor userAuthenticationInterceptor;

    @Autowired
    private UserAuthorisationInterceptor userAuthorisationInterceptor;

    @Autowired
    private CRUDAuthenticationInterceptor crudPermissionsInterceptor;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor);
        registry.addInterceptor(userAuthenticationInterceptor).addPathPatterns("/orderable/**");
        registry.addInterceptor(userAuthorisationInterceptor).addPathPatterns("/orderable/certificates/**");
        registry.addInterceptor(crudPermissionsInterceptor).addPathPatterns("/orderable/**");
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .findAndRegisterModules();
    }

    @Bean
    public CRUDAuthenticationInterceptor crudPermissionsInterceptor() {
        return new CRUDAuthenticationInterceptor(Key.USER_ORDERS);
    }

}
