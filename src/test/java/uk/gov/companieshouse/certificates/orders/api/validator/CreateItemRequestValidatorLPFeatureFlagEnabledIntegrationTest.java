package uk.gov.companieshouse.certificates.orders.api.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.controller.ApiErrors;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.DesignatedMemberDetails;
import uk.gov.companieshouse.certificates.orders.api.model.DirectorOrSecretaryDetails;
import uk.gov.companieshouse.certificates.orders.api.model.GeneralPartnerDetails;
import uk.gov.companieshouse.certificates.orders.api.model.IncludeAddressRecordsType;
import uk.gov.companieshouse.certificates.orders.api.model.LimitedPartnerDetails;
import uk.gov.companieshouse.certificates.orders.api.model.MemberDetails;
import uk.gov.companieshouse.certificates.orders.api.model.PrincipalPlaceOfBusinessDetails;
import uk.gov.companieshouse.certificates.orders.api.model.RegisteredOfficeAddressDetails;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.certificates.orders.api.model.CertificateType.DISSOLUTION;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryMethod.COLLECTION;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale.SAME_DAY;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale.STANDARD;
import static uk.gov.companieshouse.certificates.orders.api.model.IncludeDobType.PARTIAL;

/**
 * Unit tests the {@link CreateItemRequestValidator} class.
 */
@SpringBootTest
@ActiveProfiles("lp-feature-flag-enabled")
class CreateItemRequestValidatorLPFeatureFlagEnabledIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateItemRequestValidatorLPFeatureFlagEnabledIntegrationTest.class.getName());

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private CertificateItemOptions certificateItemOptions;

    @MockBean
    private RequestValidatable requestValidatable;

    @Autowired
    private CreateItemRequestValidator validatorUnderTest;

    @BeforeEach
    void setUp() {
        LOGGER.debug("Active profile " + activeProfile);
        certificateItemOptions = new CertificateItemOptions();
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
    }

    @Test
    @DisplayName("Collection location is optional by default")
    void collectionLocationIsOptionalByDefault() {
        // Given
        certificateItemOptions.setCompanyType("limited-partnership");
        certificateItemOptions.setCollectionLocation(null);
        certificateItemOptions.setCompanyStatus(CompanyStatus.ACTIVE.getStatusName());

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Collection details are mandatory for collection delivery method")
    void collectionDetailsAreMandatoryForCollectionDeliveryMethod() {
        // Given
        certificateItemOptions.setDeliveryMethod(COLLECTION);
        certificateItemOptions.setCompanyType("any");

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, containsInAnyOrder(
                ApiErrors.raiseError(ApiErrors.ERR_COLLECTION_LOCATION_REQUIRED, "collection_location: must not be null when delivery method is collection"),
                ApiErrors.raiseError(ApiErrors.ERR_FORENAME_REQUIRED, "forename: must not be blank when delivery method is collection"),
                ApiErrors.raiseError(ApiErrors.ERR_SURNAME_REQUIRED, "surname: must not be blank when delivery method is collection")
        ));
    }

    @Test
    @DisplayName("Company objects and good standing info may be requested by default")
    void companyObjectsAndGoodStandingInfoMayBeRequestedByDefault() {
        // Given
        certificateItemOptions.setIncludeCompanyObjectsInformation(true);
        certificateItemOptions.setIncludeGoodStandingInformation(true);
        certificateItemOptions.setCompanyType("any");
        certificateItemOptions.setCompanyStatus(CompanyStatus.ACTIVE.getStatusName());

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Company objects, general nature of business information, good standing, registered office details, " +
            "secretary details or director details should not be requested for dissolution")
    void certainCompanyObjectsMustNotBeRequestedForDissolution() {
        // Given
        RegisteredOfficeAddressDetails registeredOfficeAddressDetails = new RegisteredOfficeAddressDetails();
        registeredOfficeAddressDetails.setIncludeAddressRecordsType(IncludeAddressRecordsType.CURRENT);
        registeredOfficeAddressDetails.setIncludeDates(true);

        DirectorOrSecretaryDetails directorOrSecretaryDetails = new DirectorOrSecretaryDetails();
        directorOrSecretaryDetails.setIncludeAddress(true);
        directorOrSecretaryDetails.setIncludeAppointmentDate(false);
        directorOrSecretaryDetails.setIncludeBasicInformation(true);
        directorOrSecretaryDetails.setIncludeCountryOfResidence(false);
        directorOrSecretaryDetails.setIncludeDobType(PARTIAL);
        directorOrSecretaryDetails.setIncludeNationality(false);
        directorOrSecretaryDetails.setIncludeOccupation(true);

        MemberDetails memberDetails = new MemberDetails();
        DesignatedMemberDetails designatedMemberDetails = new DesignatedMemberDetails();
        GeneralPartnerDetails generalPartnerDetails = new GeneralPartnerDetails();
        LimitedPartnerDetails limitedPartnerDetails = new LimitedPartnerDetails();
        PrincipalPlaceOfBusinessDetails principalPlaceOfBusinessDetails = new PrincipalPlaceOfBusinessDetails();
	
        certificateItemOptions.setCertificateType(DISSOLUTION);
        certificateItemOptions.setIncludeCompanyObjectsInformation(true);
        certificateItemOptions.setIncludeGoodStandingInformation(true);
        certificateItemOptions.setIncludeGeneralNatureOfBusinessInformation(true);
        certificateItemOptions.setRegisteredOfficeAddressDetails(registeredOfficeAddressDetails);
        certificateItemOptions.setSecretaryDetails(directorOrSecretaryDetails);
        certificateItemOptions.setDirectorDetails(directorOrSecretaryDetails);
        certificateItemOptions.setMemberDetails(memberDetails);
        certificateItemOptions.setDesignatedMemberDetails(designatedMemberDetails);
        certificateItemOptions.setGeneralPartnerDetails(generalPartnerDetails);
        certificateItemOptions.setLimitedPartnerDetails(limitedPartnerDetails);
        certificateItemOptions.setPrincipalPlaceOfBusinessDetails(principalPlaceOfBusinessDetails);
        certificateItemOptions.setCompanyType("limited");
        certificateItemOptions.setCompanyStatus(CompanyStatus.DISSOLVED.getStatusName());

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, containsInAnyOrder(
                ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_COMPANY_OBJECTS_INFORMATION_SUPPLIED, "include_company_objects_information: must not exist when company status is dissolved"),
                ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_GOOD_STANDING_INFORMATION_SUPPLIED, "include_good_standing_information: must not exist when company status is dissolved"),
                ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_GENERAL_NATURE_OF_BUSINESS_INFORMATION_SUPPLIED, "include_general_nature_of_business_information: must not exist when company status is dissolved"),
                ApiErrors.raiseError(ApiErrors.ERR_REGISTERED_OFFICE_ADDRESS_DETAILS_SUPPLIED, "include_registered_office_address_details: must not exist when company status is dissolved"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_SUPPLIED, "include_secretary_details: must not exist when company status is dissolved"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_SUPPLIED, "include_director_details: must not exist when company status is dissolved"),
                ApiErrors.raiseError(ApiErrors.ERR_MEMBERS_DETAILS_SUPPLIED, "include_member_details: must not exist when company status is dissolved"),
                ApiErrors.raiseError(ApiErrors.ERR_DESIGNATED_MEMBERS_DETAILS_SUPPLIED, "include_designated_member_details: must not exist when company status is dissolved"),
                ApiErrors.raiseError(ApiErrors.ERR_GENERAL_PARTNER_DETAILS_SUPPLIED, "include_general_partner_details: must not exist when company status is dissolved"),
                ApiErrors.raiseError(ApiErrors.ERR_LIMITED_PARTNER_DETAILS_SUPPLIED, "include_limited_partner_details: must not exist when company status is dissolved"),
                ApiErrors.raiseError(ApiErrors.ERR_PRINCIPAL_PLACE_OF_BUSINESS_DETAILS_SUPPLIED, "include_principal_place_of_business_details: must not exist when company status is dissolved"),
                ApiErrors.raiseError(ApiErrors.ERR_MEMBERS_DETAILS_SUPPLIED, "include_member_details: must not exist when company type is limited"),
                ApiErrors.raiseError(ApiErrors.ERR_DESIGNATED_MEMBERS_DETAILS_SUPPLIED, "include_designated_member_details: must not exist when company type is limited"),
                ApiErrors.raiseError(ApiErrors.ERR_GENERAL_PARTNER_DETAILS_SUPPLIED, "include_general_partner_details: must not exist when company type is limited"),
                ApiErrors.raiseError(ApiErrors.ERR_LIMITED_PARTNER_DETAILS_SUPPLIED, "include_limited_partner_details: must not exist when company type is limited"),
                ApiErrors.raiseError(ApiErrors.ERR_PRINCIPAL_PLACE_OF_BUSINESS_DETAILS_SUPPLIED, "include_principal_place_of_business_details: must not exist when company type is limited"),
                ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_GENERAL_NATURE_OF_BUSINESS_INFORMATION_SUPPLIED, "include_general_nature_of_business_information: must not exist when company type is limited")
        ));
    }

    @Test
    @DisplayName(
            "Company objects and good standing set as null when certificate type is dissolution")
    void companyObjectsGoodStandingAsNullWhenRequestedForDissolution() {
        // Given
        certificateItemOptions.setCertificateType(DISSOLUTION);
        certificateItemOptions.setIncludeCompanyObjectsInformation(null);
        certificateItemOptions.setIncludeGoodStandingInformation(null);
        certificateItemOptions.setCompanyType("any");

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("(Only) include email copy for same day delivery timescale")
    void includeEmailCopyForSameDayDeliveryTimescale() {
        // Given
        certificateItemOptions.setDeliveryTimescale(SAME_DAY);
        certificateItemOptions.setIncludeEmailCopy(true);
        certificateItemOptions.setCompanyType("any");

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Do not include email copy for standard delivery timescale")
    void doNotIncludeEmailCopyForStandardDeliveryTimescale() {
        // Given
        certificateItemOptions.setDeliveryTimescale(STANDARD);
        certificateItemOptions.setIncludeEmailCopy(true);
        certificateItemOptions.setCompanyType("any");
        ApiError expectedError = ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_EMAIL_COPY_NOT_ALLOWED,
                "include_email_copy: can only be true when delivery timescale is same_day");

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, contains(expectedError));
    }

    @Test
    @DisplayName("Do not include other details without basic information")
    void doNotIncludeOtherDetailsWithoutBasicInformation() {
        // Given
        DirectorOrSecretaryDetails directorOrSecretaryDetails = new DirectorOrSecretaryDetails();
        directorOrSecretaryDetails.setIncludeAddress(true);
        directorOrSecretaryDetails.setIncludeAppointmentDate(true);
        directorOrSecretaryDetails.setIncludeCountryOfResidence(true);
        directorOrSecretaryDetails.setIncludeDobType(PARTIAL);
        directorOrSecretaryDetails.setIncludeNationality(true);
        directorOrSecretaryDetails.setIncludeOccupation(true);

        DesignatedMemberDetails designatedMemberDetails = new DesignatedMemberDetails();
        designatedMemberDetails.setIncludeAddress(true);
        designatedMemberDetails.setIncludeAppointmentDate(true);
        designatedMemberDetails.setIncludeCountryOfResidence(true);
        designatedMemberDetails.setIncludeDobType(PARTIAL);

        MemberDetails memberDetails = new MemberDetails();
        memberDetails.setIncludeAddress(true);
        memberDetails.setIncludeAppointmentDate(true);
        memberDetails.setIncludeCountryOfResidence(true);
        memberDetails.setIncludeDobType(PARTIAL);

        certificateItemOptions.setDirectorDetails(directorOrSecretaryDetails);
        certificateItemOptions.setSecretaryDetails(directorOrSecretaryDetails);
        certificateItemOptions.setDesignatedMemberDetails(designatedMemberDetails);
        certificateItemOptions.setMemberDetails(memberDetails);
        certificateItemOptions.setCompanyType("limited");
        certificateItemOptions.setCompanyStatus(CompanyStatus.ACTIVE.getStatusName());

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, contains(
                ApiErrors.raiseError(ApiErrors.ERR_DESIGNATED_MEMBERS_DETAILS_SUPPLIED, "include_designated_member_details: must not exist when company type is limited"),
                ApiErrors.raiseError(ApiErrors.ERR_MEMBERS_DETAILS_SUPPLIED, "include_member_details: must not exist when company type is limited"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_ADDRESS, "director_details.include_address: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_APPOINTMENT_DATE, "director_details.include_appointment_date: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_ERROR, "director_details.include_country_of_residence: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_DOB_TYPE_SUPPLIED_ERROR, "director_details.include_dob_type: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_NATIONALITY, "director_details.include_nationality: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_OCCUPATION, "director_details.include_occupation: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_ADDRESS, "secretary_details.include_address: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_APPOINTMENT_DATE, "secretary_details.include_appointment_date: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_ERROR, "secretary_details.include_country_of_residence: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_DOB_TYPE_SUPPLIED_ERROR, "secretary_details.include_dob_type: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_NATIONALITY, "secretary_details.include_nationality: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_OCCUPATION, "secretary_details.include_occupation: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DESIGNATED_MEMBER_DETAILS_INCLUDE_ADDRESS, "designated_member_details.include_address: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DESIGNATED_MEMBER_DETAILS_INCLUDE_APPOINTMENT_DATE, "designated_member_details.include_appointment_date: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DESIGNATED_MEMBER_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_ERROR, "designated_member_details.include_country_of_residence: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DESIGNATED_MEMBER_DETAILS_INCLUDE_DOB_TYPE_SUPPLIED_ERROR, "designated_member_details.include_dob_type: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_MEMBER_DETAILS_INCLUDE_ADDRESS, "member_details.include_address: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_MEMBER_DETAILS_INCLUDE_APPOINTMENT_DATE, "member_details.include_appointment_date: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_MEMBER_DETAILS_INCLUDE_COUNTRY_OF_RESIDENCE_ERROR, "member_details.include_country_of_residence: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_MEMBER_DETAILS_INCLUDE_DOB_TYPE_SUPPLIED_ERROR, "member_details.include_dob_type: must not be set when include_basic_information is false")
                ));
    }

    @Test
    @DisplayName("Can include other details with basic information")
    void canIncludeOtherDetailsWithBasicInformation() {
        // Given
        DirectorOrSecretaryDetails directorOrSecretaryDetails = new DirectorOrSecretaryDetails();
        directorOrSecretaryDetails.setIncludeAddress(true);
        directorOrSecretaryDetails.setIncludeAppointmentDate(true);
        directorOrSecretaryDetails.setIncludeBasicInformation(true);
        directorOrSecretaryDetails.setIncludeCountryOfResidence(true);
        directorOrSecretaryDetails.setIncludeDobType(PARTIAL);
        directorOrSecretaryDetails.setIncludeNationality(true);
        directorOrSecretaryDetails.setIncludeOccupation(true);

        certificateItemOptions.setDirectorDetails(directorOrSecretaryDetails);
        certificateItemOptions.setSecretaryDetails(directorOrSecretaryDetails);
        certificateItemOptions.setCompanyType("limited");

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Request is invalid if company type is llp and appropriate fields set")
    void rejectMembersAndDesignatedMembersFieldValuesForLlps() {
        //given
        DirectorOrSecretaryDetails directorOrSecretaryDetails = new DirectorOrSecretaryDetails();
        directorOrSecretaryDetails.setIncludeBasicInformation(true);

        MemberDetails memberDetails = new MemberDetails();
        memberDetails.setIncludeAddress(true);
        memberDetails.setIncludeDobType(PARTIAL);
        memberDetails.setIncludeAppointmentDate(true);
        memberDetails.setIncludeCountryOfResidence(true);
        memberDetails.setIncludeBasicInformation(true);

        DesignatedMemberDetails designatedMemberDetails = new DesignatedMemberDetails();
        designatedMemberDetails.setIncludeAddress(true);
        designatedMemberDetails.setIncludeDobType(PARTIAL);
        designatedMemberDetails.setIncludeAppointmentDate(true);
        designatedMemberDetails.setIncludeCountryOfResidence(true);
        designatedMemberDetails.setIncludeBasicInformation(true);

        certificateItemOptions.setDirectorDetails(directorOrSecretaryDetails);
        certificateItemOptions.setSecretaryDetails(directorOrSecretaryDetails);
        certificateItemOptions.setMemberDetails(memberDetails);
        certificateItemOptions.setDesignatedMemberDetails(designatedMemberDetails);
        certificateItemOptions.setCompanyType("llp");

        //when
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        //then
        assertThat(errors, containsInAnyOrder(
                ApiErrors.raiseError(ApiErrors.ERR_DESIGNATED_MEMBERS_DETAILS_SUPPLIED, "include_designated_member_details: must not exist when company type is llp"),
                ApiErrors.raiseError(ApiErrors.ERR_MEMBERS_DETAILS_SUPPLIED, "include_member_details: must not exist when company type is llp")
        ));
    }

    @Test
    @DisplayName("Request is valid if company type is limited-partnership and appropriate fields set")
    void allowGeneralPartnersLimitedPartnersPrincipalPlaceOfBusinessGeneralNatureOfBusinessInformationFieldValuesForLimitedPartnerships() {
        //given
        GeneralPartnerDetails generalPartnerDetails = new GeneralPartnerDetails();
        generalPartnerDetails.setIncludeBasicInformation(true);

        LimitedPartnerDetails limitedPartnerDetails = new LimitedPartnerDetails();
        limitedPartnerDetails.setIncludeBasicInformation(true);

        PrincipalPlaceOfBusinessDetails principalPlaceOfBusinessDetails = new PrincipalPlaceOfBusinessDetails();
        principalPlaceOfBusinessDetails.setIncludeDates(true);
        principalPlaceOfBusinessDetails.setIncludeAddressRecordsType(IncludeAddressRecordsType.CURRENT);

        certificateItemOptions.setGeneralPartnerDetails(generalPartnerDetails);
        certificateItemOptions.setLimitedPartnerDetails(limitedPartnerDetails);
        certificateItemOptions.setPrincipalPlaceOfBusinessDetails(principalPlaceOfBusinessDetails);
        certificateItemOptions.setIncludeGeneralNatureOfBusinessInformation(true);
        certificateItemOptions.setCompanyType("limited-partnership");
        certificateItemOptions.setCompanyStatus(CompanyStatus.ACTIVE.getStatusName());

        //when
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        //then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Reports only the incorrectly set fields")
    void reportsOnlyIncorrectlySetFields() {
        // Given
        DirectorOrSecretaryDetails directorOrSecretaryDetails = new DirectorOrSecretaryDetails();
        directorOrSecretaryDetails.setIncludeAddress(true);
        directorOrSecretaryDetails.setIncludeAppointmentDate(false);
        directorOrSecretaryDetails.setIncludeNationality(true);
        directorOrSecretaryDetails.setIncludeOccupation(true);

        certificateItemOptions.setDirectorDetails(directorOrSecretaryDetails);
        certificateItemOptions.setSecretaryDetails(directorOrSecretaryDetails);
        certificateItemOptions.setCompanyType("limited");
        certificateItemOptions.setCompanyStatus(CompanyStatus.ACTIVE.getStatusName());

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, contains(
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_ADDRESS, "director_details.include_address: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_NATIONALITY, "director_details.include_nationality: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_INCLUDE_OCCUPATION, "director_details.include_occupation: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_ADDRESS, "secretary_details.include_address: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_NATIONALITY, "secretary_details.include_nationality: must not be set when include_basic_information is false"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_INCLUDE_OCCUPATION, "secretary_details.include_occupation: must not be set when include_basic_information is false")
        ));
    }

    @Test
    @DisplayName("Handles absence of item options smoothly")
    void handlesAbsenceOfItemOptionsSmoothly() {
        // Given
        when(requestValidatable.getItemOptions()).thenReturn(null);

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Handles absence of details smoothly")
    void handlesAbsenceOfDetailsSmoothly() {
        // Given
        certificateItemOptions.setCompanyType("any");

        // When
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Request is valid if directors' or secretaries' details specified for an llp")
    void allowDirectorsOrSecretariesDetailsWhenSpecifiedForLlp() {
        //given
        DirectorOrSecretaryDetails directorOrSecretaryDetails = new DirectorOrSecretaryDetails();

        certificateItemOptions.setCompanyType("llp");
        certificateItemOptions.setDirectorDetails(directorOrSecretaryDetails);
        certificateItemOptions.setSecretaryDetails(directorOrSecretaryDetails);
	
        //when
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        //then
        assertThat(errors, empty());
    }

    @Test
    @DisplayName(
            "Request is invalid if directors' or secretaries' details specified for an limited-partnership")
    void rejectDirectorsOrSecretariesDetailsIfSpecifiedForLimitedPartnership() {
        //given
        DirectorOrSecretaryDetails directorOrSecretaryDetails = new DirectorOrSecretaryDetails();

        certificateItemOptions.setCompanyType("limited-partnership");
        certificateItemOptions.setDirectorDetails(directorOrSecretaryDetails);
        certificateItemOptions.setSecretaryDetails(directorOrSecretaryDetails);
        certificateItemOptions.setCompanyStatus(CompanyStatus.ACTIVE.getStatusName());

        //when
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        //then
        assertThat(errors, contains(
                ApiErrors.raiseError(ApiErrors.ERR_DIRECTOR_DETAILS_SUPPLIED, "include_director_details: must not exist when company type is limited-partnership"),
                ApiErrors.raiseError(ApiErrors.ERR_SECRETARY_DETAILS_SUPPLIED, "include_secretary_details: must not exist when company type is limited-partnership")
        ));
    }

    @Test
    @DisplayName("Request is invalid if LP and LLP details specified for an limited company")
    void rejectLPAndLLPDetailsIfSpecifiedForLimitedCompany() {
        //given
        MemberDetails memberDetails = new MemberDetails();
        memberDetails.setIncludeAddress(true);
        memberDetails.setIncludeDobType(PARTIAL);
        memberDetails.setIncludeAppointmentDate(true);
        memberDetails.setIncludeCountryOfResidence(true);
        memberDetails.setIncludeBasicInformation(true);

        DesignatedMemberDetails designatedMemberDetails = new DesignatedMemberDetails();
        designatedMemberDetails.setIncludeAddress(true);
        designatedMemberDetails.setIncludeDobType(PARTIAL);
        designatedMemberDetails.setIncludeAppointmentDate(true);
        designatedMemberDetails.setIncludeCountryOfResidence(true);
        designatedMemberDetails.setIncludeBasicInformation(true);

        GeneralPartnerDetails generalPartnerDetails = new GeneralPartnerDetails();
        generalPartnerDetails.setIncludeBasicInformation(true);

        LimitedPartnerDetails limitedPartnerDetails = new LimitedPartnerDetails();
        limitedPartnerDetails.setIncludeBasicInformation(true);

        PrincipalPlaceOfBusinessDetails principalPlaceOfBusinessDetails = new PrincipalPlaceOfBusinessDetails();
        principalPlaceOfBusinessDetails.setIncludeDates(true);
        principalPlaceOfBusinessDetails.setIncludeAddressRecordsType(IncludeAddressRecordsType.CURRENT);

        DirectorOrSecretaryDetails directorOrSecretaryDetails = new DirectorOrSecretaryDetails();

        certificateItemOptions.setGeneralPartnerDetails(generalPartnerDetails);
        certificateItemOptions.setLimitedPartnerDetails(limitedPartnerDetails);
        certificateItemOptions.setPrincipalPlaceOfBusinessDetails(principalPlaceOfBusinessDetails);
        certificateItemOptions.setIncludeGeneralNatureOfBusinessInformation(true);
        certificateItemOptions.setMemberDetails(memberDetails);
        certificateItemOptions.setDesignatedMemberDetails(designatedMemberDetails);
        certificateItemOptions.setCompanyType("limited");
        certificateItemOptions.setDirectorDetails(directorOrSecretaryDetails);
        certificateItemOptions.setSecretaryDetails(directorOrSecretaryDetails);

        //when
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        //then
        assertThat(errors, containsInAnyOrder(
                ApiErrors.raiseError(ApiErrors.ERR_PRINCIPAL_PLACE_OF_BUSINESS_DETAILS_SUPPLIED, "include_principal_place_of_business_details: must not exist when company type is limited"),
                ApiErrors.raiseError(ApiErrors.ERR_GENERAL_PARTNER_DETAILS_SUPPLIED, "include_general_partner_details: must not exist when company type is limited"),
                ApiErrors.raiseError(ApiErrors.ERR_LIMITED_PARTNER_DETAILS_SUPPLIED, "include_limited_partner_details: must not exist when company type is limited"),
                ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_GENERAL_NATURE_OF_BUSINESS_INFORMATION_SUPPLIED, "include_general_nature_of_business_information: must not exist when company type is limited"),
                ApiErrors.raiseError(ApiErrors.ERR_DESIGNATED_MEMBERS_DETAILS_SUPPLIED, "include_designated_member_details: must not exist when company type is limited"),
                ApiErrors.raiseError(ApiErrors.ERR_MEMBERS_DETAILS_SUPPLIED, "include_member_details: must not exist when company type is limited")
        ));
    }
}
