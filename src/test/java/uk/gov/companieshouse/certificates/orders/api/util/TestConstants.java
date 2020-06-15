package uk.gov.companieshouse.certificates.orders.api.util;

public class TestConstants {

    /** The HTTP request ID header name. */
    public static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    public static final String TOKEN_REQUEST_ID_VALUE = "f058ebd6-02f7-4d3f-942e-904344e8cde5";
    public static final String ERIC_IDENTITY_HEADER_NAME = "ERIC-Identity";
    public static final String ERIC_IDENTITY_VALUE = "Y2VkZWVlMzhlZWFjY2M4MzQ3MT";
    public static final String ERIC_IDENTITY_TYPE_HEADER_NAME = "ERIC-Identity-Type";
    public static final String ERIC_IDENTITY_TYPE_OAUTH2_VALUE = "oauth2";
    public static final String ERIC_IDENTITY_TYPE_API_KEY_VALUE = "key";
    public static final String ERIC_AUTHORISED_USER_HEADER_NAME = "ERIC-Authorised-User";
    public static final String ERIC_AUTHORISED_USER_VALUE = "demo@ch.gov.uk; forename=demoForename; surname=demoSurname";

    public static final int STANDARD_INDIVIDUAL_CERTIFICATE_COST = 15;
    public static final int SAME_DAY_INDIVIDUAL_CERTIFICATE_COST = 50;
    public static final int STANDARD_EXTRA_CERTIFICATE_DISCOUNT = 5;
    public static final int SAME_DAY_EXTRA_CERTIFICATE_DISCOUNT = 40;

}
