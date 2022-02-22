package uk.gov.companieshouse.certificates.orders.api.controller;

import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.util.ApiErrorBuilder;

import java.util.List;

public final class ApiErrors {
    private static final String COMPANY_NOT_FOUND_ERROR = "company-not-found";
    private static final String COMPANY_NUMBER_REQUIRED_ERROR = "company-number-is-null";
    private static final String COMPANY_SERVICE_UNAVAILABLE_ERROR = "company-service-unavailable";
    private static final String COMPANY_STATUS_INVALID_ERROR = "company-status-invalid";
    private static final String INVALID_COMPANY_TYPE_ERROR = "invalid-company-type";
    private static final String CERTIFICATE_ID_SUPPLIED_ERROR = "id-error";
    private static final String INCLUDE_COMPANY_OBJECTS_INFORMATION_SUPPLIED_ERROR = "include-company-objects-information-error";
    private static final String INCLUDE_GENERAL_NATURE_OF_BUSINESS_INFORMATION_SUPPLIED_ERROR = "include-general-nature-of-business-information-error";
    private static final String INCLUDE_GOOD_STANDING_INFORMATION_SUPPLIED_ERROR = "include-good-standing-information-error";
    private static final String REGISTERED_OFFICE_ADDRESS_DETAILS_SUPPLIED_ERROR = "registered-office-address-details-error";
    private static final String SECRETARY_DETAILS_SUPPLIED_ERROR = "secretary-details-error";
    private static final String DIRECTOR_DETAILS_SUPPLIED_ERROR = "director-details-error";
    private static final String DESIGNATED_MEMBERS_DETAILS_SUPPLIED_ERROR = "designated-members-details-error";
    private static final String MEMBERS_DETAILS_SUPPLIED_ERROR = "members-details-error";
    private static final String GENERAL_PARTNER_DETAILS_SUPPLIED_ERROR = "general-partner-details-error";
    private static final String LIMITED_PARTNER_DETAILS_SUPPLIED_ERROR = "limited-partner-details-error";
    private static final String PRINCIPAL_PLACE_OF_BUSINESS_DETAILS_SUPPLIED_ERROR = "principal-place-of-business-details-error";
    private static final String INCLUDE_EMAIL_COPY_NOT_ALLOWED_ERROR = "include-email-copy-is-true-error";
    private static final String COLLECTION_LOCATION_REQUIRED_ERROR = "collection-location-required-error";
    private static final String FORENAME_REQUIRED_ERROR = "forename-required-error";
    private static final String SURNAME_REQUIRED_ERROR = "surname-required-error";
    private static final String JSON_PROCESSING_ERROR = "json-processing-error";
    private static final String CERTIFICATE_NOT_FOUND_ERROR = "certificate-not-found-error";
    private static final String LIQUIDATORS_DETAILS_SUPPLIED_ERROR = "liquidators-details-supplied-error";
    private static final String ADMINISTRATORS_DETAILS_SUPPLIED_ERROR = "administrators-details-supplied-error";
    private static final String COMPANY_TYPE_REQUIRED_ERROR = "company-type-required-error";
    private static final String DIRECTORS_DETAILS_REQUIRED_ERROR = "directors-details-required-error";
    private static final String SECRETARY_DETAILS_REQUIRED_ERROR = "secretary-details-required-error";
    private static final String DESCRIPTION_SUPPLIED_ERROR = "description-error";
    private static final String DESCRIPTION_VALUES_SUPPLIED_ERROR = "description-values-error";
    private static final String ETAG_SUPPLIED_ERROR = "etag-error";
    private static final String ITEM_COSTS_SUPPLIED_ERROR = "item-costs-error";
    private static final String KIND_SUPPLIED_ERROR = "kind-error";
    private static final String QUANTITY_AMOUNT_ERROR = "quantity-error";
    private static final String DESCRIPTION_IDENTIFIER_ERROR = "description-identifier-error";
    private static final String DESCRIPTION_VALUES_ERROR = "description-values-error";
    private static final String POSTAL_DELIVERY_ERROR = "postal-delivery-error";
    private static final String INCLUDE_ADDRESS_ERROR = "include-address-error";
    private static final String INCLUDE_APPOINTMENT_DATE_ERROR = "include-appointment-date-error";
    private static final String INCLUDE_COUNTRY_OF_RESIDENCE_ERROR = "include-country-of-residence-error";
    private static final String INCLUDE_NATIONALITY_ERROR = "include-nationality-error";
    private static final String INCLUDE_OCCUPATION_ERROR = "include-occupation-error";
    private static final String INCLUDE_DOB_TYPE_ERROR = "include-dob-type-error";

    private static final String COMPANY_NUMBER_LOCATION = "company_number";
    private static final String COMPANY_TYPE_LOCATION = "company_type";
    private static final String COMPANY_STATUS_LOCATION = "company_status";
    private static final String ID_LOCATION = "id";
    private static final String INCLUDE_COMPANY_OBJECTS_INFORMATION_LOCATION = "item_options.include_company_objects_information";
    private static final String INCLUDE_GENERAL_NATURE_OF_BUSINESS_INFORMATION_LOCATION = "item_options.include_general_nature_of_business_information";
    private static final String INCLUDE_GOOD_STANDING_INFORMATION_LOCATION = "item_options.include_good_standing_information";
    private static final String REGISTERED_OFFICE_ADDRESS_DETAILS_LOCATION = "item_options.registered_office_address_details";
    private static final String SECRETARY_DETAILS_LOCATION = "item_options.secretary_details_location";
    private static final String DIRECTOR_DETAILS_LOCATION = "item_options.director_details_location";
    private static final String DESIGNATED_MEMBERS_DETAILS_LOCATION = "item_options.designated_members_details_location";
    private static final String MEMBERS_DETAILS_LOCATION = "item_options.members_details_location";
    private static final String GENERAL_PARTNER_DETAILS_LOCATION = "item_options.general_partner_details_location";
    private static final String LIMITED_PARTNER_DETAILS_LOCATION = "item_options.limited_partner_details_location";
    private static final String PRINCIPAL_PLACE_OF_BUSINESS_DETAILS_LOCATION = "item_options.principal_place_of_business_details_location";
    private static final String INCLUDE_DOB_TYPE_LOCATION = "item_options.include_dob_type_error";
    private static final String INCLUDE_EMAIL_COPY_LOCATION = "item_options.include_email_copy";
    private static final String COLLECTION_LOCATION_LOCATION = "item_options.collection_location";
    private static final String FORENAME_LOCATION = "item_options.forename";
    private static final String SURNAME_LOCATION = "item_options.surname";
    private static final String JSON_PROCESSING_LOCATION = "certificate_item";
    private static final String LIQUIDATORS_DETAILS_LOCATION = "item_options.liquidators_details";
    private static final String ADMINISTRATORS_DETAILS_LOCATION = "item_options.administrators_details";
    private static final String DESCRIPTION_SUPPLIED_LOCATION = "description";
    private static final String DESCRIPTION_VALUES_SUPPLIED_LOCATION = "description_values";
    private static final String ETAG_SUPPLIED_LOCATION = "etag";
    private static final String ITEM_COSTS_SUPPLIED_LOCATION = "item_costs";
    private static final String KIND_SUPPLIED_LOCATION = "kind";
    private static final String QUANTITY_LOCATION = "quantity";
    private static final String DESCRIPTION_IDENTIFIER_LOCATION = "description_identifier";
    private static final String DESCRIPTION_VALUES_LOCATION = "description_values";
    private static final String POSTAL_DELIVERY_LOCATION = "postal_delivery";
    private static final String DIRECTOR_DETAILS_INCLUDE_ADDRESS_LOCATION = "director_details.include_address";
    private static final String DIRECTOR_DETAILS_INCLUDE_APPOINTMENT_DATE_LOCATION = "director_details.include_appointment_date";
    private static final String DIRECTOR_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_LOCATION = "director_details.include_country_of_residence";
    private static final String DIRECTOR_DETAILS_INCLUDE_NATIONALITY_LOCATION = "director_details.include_nationality";
    private static final String DIRECTOR_DETAILS_INCLUDE_OCCUPATION_LOCATION = "director_details.include_occupation";
    private static final String DIRECTOR_DETAILS_INCLUDE_DOB_TYPE_LOCATION = "director_details.include_dob_type";
    private static final String SECRETARY_DETAILS_INCLUDE_ADDRESS_LOCATION = "secretary_details.include_address";
    private static final String SECRETARY_DETAILS_INCLUDE_APPOINTMENT_DATE_LOCATION = "secretary_details.include_appointment_date";
    private static final String SECRETARY_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_LOCATION = "secretary_details.include_country_of_residence";
    private static final String SECRETARY_DETAILS_INCLUDE_NATIONALITY_LOCATION = "secretary_details.include_nationality";
    private static final String SECRETARY_DETAILS_INCLUDE_OCCUPATION_LOCATION = "secretary_details.include_occupation";
    private static final String SECRETARY_DETAILS_INCLUDE_DOB_TYPE_LOCATION = "secretary_details.include_dob_type";

    private static final String DESIGNATED_MEMBER_DETAILS_INCLUDE_ADDRESS_LOCATION = "designated_member_details.include_address";
    private static final String DESIGNATED_MEMBER_DETAILS_INCLUDE_APPOINTMENT_DATE_LOCATION = "designated_member_details.include_appointment_date";
    private static final String DESIGNATED_MEMBER_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_LOCATION = "designated_member_details.include_country_of_residence";
    private static final String DESIGNATED_MEMBER_DETAILS_INCLUDE_DOB_TYPE_LOCATION = "designated_member_details.include_dob_type";

    private static final String MEMBER_DETAILS_INCLUDE_ADDRESS_LOCATION = "member_details.include_address";
    private static final String MEMBER_DETAILS_INCLUDE_APPOINTMENT_DATE_LOCATION = "member_details.include_appointment_date";
    private static final String MEMBER_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_LOCATION = "member_details.include_country_of_residence";
    private static final String MEMBER_DETAILS_INCLUDE_DOB_TYPE_LOCATION = "member_details.include_dob_type";


    public static final String STRING_LOCATION_TYPE = "string";
    public static final String BOOLEAN_LOCATION_TYPE = "boolean";
    public static final String OBJECT_LOCATION_TYPE = "object";

    public static final String ERROR_TYPE_VALIDATION = "ch:validation";
    private static final String ERROR_TYPE_SERVICE = "ch:service";

    static final ApiError ERR_COMPANY_NOT_FOUND = new ApiError(COMPANY_NOT_FOUND_ERROR, COMPANY_NUMBER_LOCATION, STRING_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    static final ApiError ERR_COMPANY_NUMBER_REQUIRED = new ApiError(COMPANY_NUMBER_REQUIRED_ERROR, COMPANY_NUMBER_LOCATION, STRING_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    static final ApiError ERR_SERVICE_UNAVAILABLE = new ApiError(COMPANY_SERVICE_UNAVAILABLE_ERROR, COMPANY_NUMBER_LOCATION, STRING_LOCATION_TYPE, ERROR_TYPE_SERVICE);
    public static final ApiError ERR_COMPANY_STATUS_INVALID = new ApiError(COMPANY_STATUS_INVALID_ERROR, COMPANY_STATUS_LOCATION, STRING_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    static final ApiError ERR_INVALID_COMPANY_TYPE = new ApiError(INVALID_COMPANY_TYPE_ERROR, COMPANY_TYPE_LOCATION, STRING_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_CERTIFICATE_ID_SUPPLIED = new ApiError(CERTIFICATE_ID_SUPPLIED_ERROR, ID_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_INCLUDE_COMPANY_OBJECTS_INFORMATION_SUPPLIED = new ApiError(INCLUDE_COMPANY_OBJECTS_INFORMATION_SUPPLIED_ERROR, INCLUDE_COMPANY_OBJECTS_INFORMATION_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_INCLUDE_GENERAL_NATURE_OF_BUSINESS_INFORMATION_SUPPLIED = new ApiError(INCLUDE_GENERAL_NATURE_OF_BUSINESS_INFORMATION_SUPPLIED_ERROR, INCLUDE_GENERAL_NATURE_OF_BUSINESS_INFORMATION_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_INCLUDE_GOOD_STANDING_INFORMATION_SUPPLIED = new ApiError(INCLUDE_GOOD_STANDING_INFORMATION_SUPPLIED_ERROR, INCLUDE_GOOD_STANDING_INFORMATION_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_REGISTERED_OFFICE_ADDRESS_DETAILS_SUPPLIED = new ApiError(REGISTERED_OFFICE_ADDRESS_DETAILS_SUPPLIED_ERROR, REGISTERED_OFFICE_ADDRESS_DETAILS_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_SECRETARY_DETAILS_SUPPLIED = new ApiError(SECRETARY_DETAILS_SUPPLIED_ERROR, SECRETARY_DETAILS_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_DIRECTOR_DETAILS_SUPPLIED = new ApiError(DIRECTOR_DETAILS_SUPPLIED_ERROR, DIRECTOR_DETAILS_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_DESIGNATED_MEMBERS_DETAILS_SUPPLIED = new ApiError(DESIGNATED_MEMBERS_DETAILS_SUPPLIED_ERROR, DESIGNATED_MEMBERS_DETAILS_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_MEMBERS_DETAILS_SUPPLIED = new ApiError(MEMBERS_DETAILS_SUPPLIED_ERROR, MEMBERS_DETAILS_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_GENERAL_PARTNER_DETAILS_SUPPLIED = new ApiError(GENERAL_PARTNER_DETAILS_SUPPLIED_ERROR, GENERAL_PARTNER_DETAILS_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_LIMITED_PARTNER_DETAILS_SUPPLIED = new ApiError(LIMITED_PARTNER_DETAILS_SUPPLIED_ERROR, LIMITED_PARTNER_DETAILS_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_PRINCIPAL_PLACE_OF_BUSINESS_DETAILS_SUPPLIED = new ApiError(PRINCIPAL_PLACE_OF_BUSINESS_DETAILS_SUPPLIED_ERROR, PRINCIPAL_PLACE_OF_BUSINESS_DETAILS_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_INCLUDE_EMAIL_COPY_NOT_ALLOWED = new ApiError(INCLUDE_EMAIL_COPY_NOT_ALLOWED_ERROR, INCLUDE_EMAIL_COPY_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_COLLECTION_LOCATION_REQUIRED = new ApiError(COLLECTION_LOCATION_REQUIRED_ERROR, COLLECTION_LOCATION_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_FORENAME_REQUIRED = new ApiError(FORENAME_REQUIRED_ERROR, FORENAME_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_SURNAME_REQUIRED = new ApiError(SURNAME_REQUIRED_ERROR, SURNAME_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_JSON_PROCESSING = new ApiError(JSON_PROCESSING_ERROR, JSON_PROCESSING_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_SERVICE);
    public static final ApiError ERR_CERTIFICATE_NOT_FOUND = new ApiError(CERTIFICATE_NOT_FOUND_ERROR, ID_LOCATION, STRING_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_LIQUIDATORS_DETAILS_SUPPLIED = new ApiError(LIQUIDATORS_DETAILS_SUPPLIED_ERROR, LIQUIDATORS_DETAILS_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_ADMINISTRATORS_DETAILS_SUPPLIED = new ApiError(ADMINISTRATORS_DETAILS_SUPPLIED_ERROR, ADMINISTRATORS_DETAILS_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_COMPANY_TYPE_REQUIRED = new ApiError(COMPANY_TYPE_REQUIRED_ERROR, COMPANY_TYPE_LOCATION, STRING_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_DESCRIPTION_SUPPLIED = new ApiError(DESCRIPTION_SUPPLIED_ERROR, DESCRIPTION_SUPPLIED_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_DESCRIPTION_VALUES_SUPPLIED = new ApiError(DESCRIPTION_VALUES_SUPPLIED_ERROR, DESCRIPTION_VALUES_SUPPLIED_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_ETAG_SUPPLIED = new ApiError(ETAG_SUPPLIED_ERROR, ETAG_SUPPLIED_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_ITEM_COSTS_SUPPLIED = new ApiError(ITEM_COSTS_SUPPLIED_ERROR, ITEM_COSTS_SUPPLIED_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_KIND_SUPPLIED_LOCATION = new ApiError(KIND_SUPPLIED_ERROR, KIND_SUPPLIED_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_QUANTITY_AMOUNT = new ApiError(QUANTITY_AMOUNT_ERROR, QUANTITY_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_DESCRIPTION_IDENTIFIER = new ApiError(DESCRIPTION_IDENTIFIER_ERROR, DESCRIPTION_IDENTIFIER_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_POSTAL_DELIVERY = new ApiError(POSTAL_DELIVERY_ERROR, POSTAL_DELIVERY_LOCATION, OBJECT_LOCATION_TYPE, ERROR_TYPE_VALIDATION);

    public static final ApiError ERR_DIRECTOR_DETAILS_INCLUDE_ADDRESS = new ApiError(INCLUDE_ADDRESS_ERROR, DIRECTOR_DETAILS_INCLUDE_ADDRESS_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_DIRECTOR_DETAILS_INCLUDE_APPOINTMENT_DATE = new ApiError(INCLUDE_APPOINTMENT_DATE_ERROR, DIRECTOR_DETAILS_INCLUDE_APPOINTMENT_DATE_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_DIRECTOR_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_ERROR = new ApiError(INCLUDE_COUNTRY_OF_RESIDENCE_ERROR, DIRECTOR_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_DIRECTOR_DETAILS_INCLUDE_NATIONALITY = new ApiError(INCLUDE_NATIONALITY_ERROR, DIRECTOR_DETAILS_INCLUDE_NATIONALITY_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_DIRECTOR_DETAILS_INCLUDE_OCCUPATION = new ApiError(INCLUDE_OCCUPATION_ERROR, DIRECTOR_DETAILS_INCLUDE_OCCUPATION_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_DIRECTOR_DETAILS_INCLUDE_DOB_TYPE_SUPPLIED_ERROR = new ApiError(INCLUDE_DOB_TYPE_ERROR, DIRECTOR_DETAILS_INCLUDE_DOB_TYPE_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);

    public static final ApiError ERR_SECRETARY_DETAILS_INCLUDE_ADDRESS = new ApiError(INCLUDE_ADDRESS_ERROR, SECRETARY_DETAILS_INCLUDE_ADDRESS_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_SECRETARY_DETAILS_INCLUDE_APPOINTMENT_DATE = new ApiError(INCLUDE_APPOINTMENT_DATE_ERROR, SECRETARY_DETAILS_INCLUDE_APPOINTMENT_DATE_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_SECRETARY_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_ERROR = new ApiError(INCLUDE_COUNTRY_OF_RESIDENCE_ERROR, SECRETARY_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_SECRETARY_DETAILS_INCLUDE_NATIONALITY = new ApiError(INCLUDE_NATIONALITY_ERROR, SECRETARY_DETAILS_INCLUDE_NATIONALITY_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_SECRETARY_DETAILS_INCLUDE_OCCUPATION = new ApiError(INCLUDE_OCCUPATION_ERROR, SECRETARY_DETAILS_INCLUDE_OCCUPATION_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_SECRETARY_DETAILS_INCLUDE_DOB_TYPE_SUPPLIED_ERROR = new ApiError(INCLUDE_DOB_TYPE_ERROR, SECRETARY_DETAILS_INCLUDE_DOB_TYPE_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);

    public static final ApiError ERR_DESIGNATED_MEMBER_DETAILS_INCLUDE_ADDRESS = new ApiError(INCLUDE_ADDRESS_ERROR, DESIGNATED_MEMBER_DETAILS_INCLUDE_ADDRESS_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_DESIGNATED_MEMBER_DETAILS_INCLUDE_APPOINTMENT_DATE = new ApiError(INCLUDE_APPOINTMENT_DATE_ERROR, DESIGNATED_MEMBER_DETAILS_INCLUDE_APPOINTMENT_DATE_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_DESIGNATED_MEMBER_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_ERROR = new ApiError(INCLUDE_COUNTRY_OF_RESIDENCE_ERROR, DESIGNATED_MEMBER_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_DESIGNATED_MEMBER_DETAILS_INCLUDE_DOB_TYPE_SUPPLIED_ERROR = new ApiError(INCLUDE_DOB_TYPE_ERROR, DESIGNATED_MEMBER_DETAILS_INCLUDE_DOB_TYPE_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);

    public static final ApiError ERR_MEMBER_DETAILS_INCLUDE_ADDRESS = new ApiError(INCLUDE_ADDRESS_ERROR, MEMBER_DETAILS_INCLUDE_ADDRESS_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_MEMBER_DETAILS_INCLUDE_APPOINTMENT_DATE = new ApiError(INCLUDE_APPOINTMENT_DATE_ERROR, MEMBER_DETAILS_INCLUDE_APPOINTMENT_DATE_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_MEMBER_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_ERROR = new ApiError(INCLUDE_COUNTRY_OF_RESIDENCE_ERROR, MEMBER_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);
    public static final ApiError ERR_MEMBER_DETAILS_INCLUDE_DOB_TYPE_SUPPLIED_ERROR = new ApiError(INCLUDE_DOB_TYPE_ERROR, MEMBER_DETAILS_INCLUDE_DOB_TYPE_LOCATION, BOOLEAN_LOCATION_TYPE, ERROR_TYPE_VALIDATION);




    private ApiErrors() {}

    public static void raiseError(List<ApiError> apiErrors, ApiError apiError, String errorMessage, Object ...objects) {
        apiErrors.add(raiseError(apiError, errorMessage, objects));
    }

    public static ApiError raiseError(ApiError apiError, String errorMessage, Object ...objects) {
        return ApiErrorBuilder.builder(apiError).withErrorMessage(String.format(errorMessage, objects)).build();
    }
}
