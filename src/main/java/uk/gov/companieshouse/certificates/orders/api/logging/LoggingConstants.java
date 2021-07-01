package uk.gov.companieshouse.certificates.orders.api.logging;

public final class LoggingConstants {
	
    private LoggingConstants() {
        // not called
    }
	
    public static final String APPLICATION_NAMESPACE = "certificates.orders.api.ch.gov.uk";
    public static final String COMPANY_NUMBER_LOG_KEY = "company_number";
    public static final String REQUEST_ID_LOG_KEY = "request_id";
    public static final String CERTIFICATE_ID_LOG_KEY = "certificate_id";
    public static final String USER_ID_LOG_KEY = "user_id";
    public static final String STATUS_LOG_KEY = "status";
    public static final String ERRORS_LOG_KEY = "errors";
    public static final String IDENTITY_LOG_KEY = "identity";
    public static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    public static final String PATCHED_COMPANY_NUMBER = "patched_company_number";

}
