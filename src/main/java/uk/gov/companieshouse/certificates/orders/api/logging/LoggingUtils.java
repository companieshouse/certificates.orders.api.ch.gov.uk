package uk.gov.companieshouse.certificates.orders.api.logging;

import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LoggingUtils {
    
    public static final String APPLICATION_NAMESPACE = "orders.api.ch.gov.uk";
    public static final String AUTHORIZATION_TYPE = "authorization_type";
    public static final String AUTHORIZED = "authorized";
    public static final String BASKET_ID = "basket_id";
    public static final String CHECKOUT_ID = "checkout_id";
    public static final String COMPANY_NUMBER = "company_number";
    public static final String ERROR_TYPE = "error_type";
    public static final String EXCEPTION = "exception";
    public static final String IDENTITY_TYPE = "identity_type";
    public static final String ITEM_ID = "item_id";
    public static final String ITEM_URI = "item_uri";
    public static final String OFFSET = "offset";
    public static final String ORDER_ID = "order_id";
    public static final String ORDER_TOTAL_COST = "order_total_cost";
    public static final String PAYMENT_AMOUNT = "payment_amount";
    public static final String PAYMENT_REF = "payment_reference";
    public static final String PAYMENT_STATUS = "payment_status";
    public static final String PAYMENT_URI = "payment_uri";
    public static final String REQUEST_ID = "request_id";
    public static final String REQUEST_URI = "request_uri";
    public static final String STATUS = "status";
    public static final String TOPIC = "topic";
    public static final String USER_ID = "user_id";
    public static final String VALIDATION_ERRORS = "validation_errors";
    public static final String IDENTITY = "identity";
    public static final String AUTHORISED_ROLES = "authorised_roles";
    public static final String AUTHORISED_KEY_PRIVILEGES = "authorised_key_privileges";

    public static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    public static Map<String, Object> createLogMapWithRequestId(String requestId){
        Map<String, Object> logMap = new HashMap<>();
        logMap.put(REQUEST_ID, requestId);
        return logMap;
    }

    public static Map<String, Object> createLogMap() {
        return new HashMap<String, Object>();
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void logIfNotNull(Map<String, Object> logMap, String key, Object loggingObject) {
        if(loggingObject != null) {
            logMap.put(key, loggingObject);
        }
    }

}
