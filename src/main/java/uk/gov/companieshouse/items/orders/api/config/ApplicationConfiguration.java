package uk.gov.companieshouse.items.orders.api.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.items.orders.api.interceptor.CertificateItemsInterceptor;
import uk.gov.companieshouse.items.orders.api.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.items.orders.api.interceptor.UserAuthenticationInterceptor;
import uk.gov.companieshouse.items.orders.api.service.CertificateItemService;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE;

@Configuration
public class ApplicationConfiguration implements WebMvcConfigurer {

    @Autowired
    private CertificateItemService certificateItemService;

    @Bean
    public CertificateItemsInterceptor certificateItemsInterceptor() {
        return new CertificateItemsInterceptor(certificateItemService);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor());
        registry.addInterceptor(new UserAuthenticationInterceptor());
        registry.addInterceptor(certificateItemsInterceptor()).addPathPatterns("/certificates/**");
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setPropertyNamingStrategy(SNAKE_CASE)
                .findAndRegisterModules();
    }

}
