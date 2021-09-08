package uk.gov.companieshouse.certificates.orders.api.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.DesignatedMemberDetails;
import uk.gov.companieshouse.certificates.orders.api.model.DirectorOrSecretaryDetails;
import uk.gov.companieshouse.certificates.orders.api.model.GeneralPartnerDetails;
import uk.gov.companieshouse.certificates.orders.api.model.IncludeAddressRecordsType;
import uk.gov.companieshouse.certificates.orders.api.model.IncludeDobType;
import uk.gov.companieshouse.certificates.orders.api.model.LimitedPartnerDetails;
import uk.gov.companieshouse.certificates.orders.api.model.MemberDetails;
import uk.gov.companieshouse.certificates.orders.api.model.PrinciplePlaceOfBusinessDetails;
import uk.gov.companieshouse.certificates.orders.api.model.RegisteredOfficeAddressDetails;
import uk.gov.companieshouse.certificates.orders.api.util.FieldNameConverter;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static uk.gov.companieshouse.certificates.orders.api.model.CertificateType.DISSOLUTION;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryMethod.COLLECTION;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale.SAME_DAY;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale.STANDARD;
import static uk.gov.companieshouse.certificates.orders.api.model.IncludeDobType.PARTIAL;

/**
 * Unit tests the {@link CreateItemRequestValidator} class.
 */
class CreateItemRequestValidatorTest {

    private CreateItemRequestValidator validatorUnderTest;
    private static final IncludeAddressRecordsType INCLUDE_ADDRESS_RECORDS_TYPE = IncludeAddressRecordsType.CURRENT;
    private static final DirectorOrSecretaryDetails DIRECTOR_OR_SECRETARY_DETAILS;
    private static final RegisteredOfficeAddressDetails REGISTERED_OFFICE_ADDRESS_DETAILS;
    private static final boolean INCLUDE_ADDRESS = true;
    private static final boolean INCLUDE_APPOINTMENT_DATE = false;
    private static final boolean INCLUDE_BASIC_INFORMATION = true;
    private static final boolean INCLUDE_COUNTRY_OF_RESIDENCE = false;
    private static final IncludeDobType INCLUDE_DOB_TYPE = IncludeDobType.PARTIAL;
    private static final boolean INCLUDE_NATIONALITY= false;
    private static final boolean INCLUDE_OCCUPATION = true;
    private static final boolean INCLUDE_DATES = true;

    static {
        DIRECTOR_OR_SECRETARY_DETAILS = new DirectorOrSecretaryDetails();
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeAddress(INCLUDE_ADDRESS);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeAppointmentDate(INCLUDE_APPOINTMENT_DATE);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeBasicInformation(INCLUDE_BASIC_INFORMATION);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeCountryOfResidence(INCLUDE_COUNTRY_OF_RESIDENCE);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeDobType(INCLUDE_DOB_TYPE);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeNationality(INCLUDE_NATIONALITY);
        DIRECTOR_OR_SECRETARY_DETAILS.setIncludeOccupation(INCLUDE_OCCUPATION);

        REGISTERED_OFFICE_ADDRESS_DETAILS = new RegisteredOfficeAddressDetails();
        REGISTERED_OFFICE_ADDRESS_DETAILS.setIncludeAddressRecordsType(INCLUDE_ADDRESS_RECORDS_TYPE);
        REGISTERED_OFFICE_ADDRESS_DETAILS.setIncludeDates(INCLUDE_DATES);
    }

    @BeforeEach
    void setUp() {
        validatorUnderTest = new CreateItemRequestValidator(new FieldNameConverter());
    }

    @Test
    @DisplayName("ID is mandatory")
    void idIsMandatory() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        item.setId("1");

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, contains("id: must be null in a create item request"));
    }

    @Test
    @DisplayName("Collection location is optional by default")
    void collectionLocationIsOptionalByDefault() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Collection details are mandatory for collection delivery method")
    void collectionDetailsAreMandatoryForCollectionDeliveryMethod() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryMethod(COLLECTION);
        item.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, containsInAnyOrder(
                "collection_location: must not be null when delivery method is collection",
                "forename: must not be blank when delivery method is collection",
                "surname: must not be blank when delivery method is collection"));
    }

    @Test
    @DisplayName("Company objects and good standing info may be requested by default")
    void companyObjectsAndGoodStandingInfoMayBeRequestedByDefault() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setIncludeCompanyObjectsInformation(true);
        options.setIncludeGoodStandingInformation(true);
        item.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Company objects, general nature of business information, good standing, registered office details, secretary details or director details" +
        "should not be requested for dissolution")
    void certainCompanyObjectsMustNotBeRequestedForDissolution() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        final RegisteredOfficeAddressDetails registeredOfficeAddressDetails = new RegisteredOfficeAddressDetails();
        registeredOfficeAddressDetails.setIncludeAddressRecordsType(INCLUDE_ADDRESS_RECORDS_TYPE);
        options.setCertificateType(DISSOLUTION);
        options.setIncludeCompanyObjectsInformation(true);
        options.setIncludeGoodStandingInformation(true);
        options.setIncludeGeneralNatureOfBusinessInformation(true);
        options.setRegisteredOfficeAddressDetails(REGISTERED_OFFICE_ADDRESS_DETAILS);
        options.setSecretaryDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        options.setDirectorDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        options.setMemberDetails(new MemberDetails());
        options.setDesignatedMemberDetails(new DesignatedMemberDetails());
        options.setGeneralPartnerDetails(new GeneralPartnerDetails());
        options.setLimitedPartnerDetails(new LimitedPartnerDetails());
        options.setPrinciplePlaceOfBusinessDetails(new PrinciplePlaceOfBusinessDetails());
        item.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, containsInAnyOrder(
            "include_company_objects_information: must not exist when certificate type is dissolution",
            "include_good_standing_information: must not exist when certificate type is dissolution",
            "include_general_nature_of_business_information: must not exist when certificate type is dissolution",
            "include_registered_office_address_details: must not exist when certificate type is dissolution",
            "include_secretary_details: must not exist when certificate type is dissolution",
            "include_director_details: must not exist when certificate type is dissolution",
            "include_member_details: must not exist when certificate type is dissolution",
            "include_designated_member_details: must not exist when certificate type is dissolution",
            "include_general_partner_details: must not exist when certificate type is dissolution",
            "include_limited_partner_details: must not exist when certificate type is dissolution",
            "include_principle_place_of_business_details: must not exist when certificate type is dissolution",
            "include_member_details: must not exist when company type is not llp",
            "include_designated_member_details: must not exist when company type is not llp",
            "include_general_partner_details: must not exist when company type is not limited-partnership",
            "include_limited_partner_details: must not exist when company type is not limited-partnership",
            "include_principle_place_of_business_details: must not exist when company type is not limited-partnership",
            "include_general_nature_of_business_information: must not exist when company type is not limited-partnership"));
    }

    @Test
    @DisplayName("Company objects and good standing set as null when certificate type is dissolution")
    void companyObjectsGoodStandingAsNullWhenRequestedForDissolution() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setCertificateType(DISSOLUTION);
        options.setIncludeCompanyObjectsInformation(null);
        options.setIncludeGoodStandingInformation(null);
        item.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, empty());
    }

    @Test
    @DisplayName("(Only) include email copy for same day delivery timescale")
    void includeEmailCopyForSameDayDeliveryTimescale() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryTimescale(SAME_DAY);
        options.setIncludeEmailCopy(true);
        item.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Do not include email copy for standard delivery timescale")
    void doNotIncludeEmailCopyForStandardDeliveryTimescale() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        options.setDeliveryTimescale(STANDARD);
        options.setIncludeEmailCopy(true);
        item.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, contains(
                "include_email_copy: can only be true when delivery timescale is same_day"));
    }

    @Test
    @DisplayName("Do not include other details without basic information")
    void doNotIncludeOtherDetailsWithoutBasicInformation() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        final DirectorOrSecretaryDetails details = new DirectorOrSecretaryDetails();
        final DesignatedMemberDetails designatedMemberDetails = new DesignatedMemberDetails();
        final MemberDetails memberDetails = new MemberDetails();
        details.setIncludeAddress(true);
        details.setIncludeAppointmentDate(true);
        details.setIncludeCountryOfResidence(true);
        details.setIncludeDobType(PARTIAL);
        details.setIncludeNationality(true);
        details.setIncludeOccupation(true);
        designatedMemberDetails.setIncludeAddress(true);
        designatedMemberDetails.setIncludeAppointmentDate(true);
        designatedMemberDetails.setIncludeCountryOfResidence(true);
        designatedMemberDetails.setIncludeDobType(PARTIAL);
        memberDetails.setIncludeAddress(true);
        memberDetails.setIncludeAppointmentDate(true);
        memberDetails.setIncludeCountryOfResidence(true);
        memberDetails.setIncludeDobType(PARTIAL);
        options.setDirectorDetails(details);
        options.setSecretaryDetails(details);
        options.setDesignatedMemberDetails(designatedMemberDetails);
        options.setMemberDetails(memberDetails);
        options.setCompanyType("limited");
        item.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, contains(
                "include_designated_member_details: must not exist when company type is not llp",
                "include_member_details: must not exist when company type is not llp",
                "director_details: include_address, include_appointment_date, include_country_of_residence,"
                        + " include_nationality, include_occupation must not be true when include_basic_information"
                        + " is false",
                "director_details: include_dob_type must not be non-null when include_basic_information is false",
                "secretary_details: include_address, include_appointment_date, include_country_of_residence,"
                        + " include_nationality, include_occupation must not be true when include_basic_information"
                        + " is false",
                "secretary_details: include_dob_type must not be non-null when include_basic_information is false",
                "designated_member_details: include_address, include_appointment_date, include_country_of_residence"
                        + " must not be true when include_basic_information"
                        + " is false",
                "designated_member_details: include_dob_type must not be non-null when include_basic_information is false",
                "member_details: include_address, include_appointment_date, include_country_of_residence"
                        + " must not be true when include_basic_information"
                        + " is false",
                "member_details: include_dob_type must not be non-null when include_basic_information is false"));
    }

    @Test
    @DisplayName("Can include other details with basic information")
    void canIncludeOtherDetailsWithBasicInformation() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        final DirectorOrSecretaryDetails details = new DirectorOrSecretaryDetails();
        details.setIncludeAddress(true);
        details.setIncludeAppointmentDate(true);
        details.setIncludeBasicInformation(true);
        details.setIncludeCountryOfResidence(true);
        details.setIncludeDobType(PARTIAL);
        details.setIncludeNationality(true);
        details.setIncludeOccupation(true);
        options.setDirectorDetails(details);
        options.setSecretaryDetails(details);
        item.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Request is valid if company type is llp and appropriate fields set")
    void allowMembersAndDesignatedMembersFieldValuesForLlps() {
        //given
        final CertificateItemDTO certificateItemDTO = new CertificateItemDTO();
        final CertificateItemOptions itemOptions = new CertificateItemOptions();
        final MemberDetails memberDetails = new MemberDetails();
        final DesignatedMemberDetails designatedMemberDetails = new DesignatedMemberDetails();
        memberDetails.setIncludeAddress(true);
        memberDetails.setIncludeDobType(INCLUDE_DOB_TYPE);
        memberDetails.setIncludeAppointmentDate(true);
        memberDetails.setIncludeCountryOfResidence(true);
        memberDetails.setIncludeBasicInformation(true);
        designatedMemberDetails.setIncludeAddress(true);
        designatedMemberDetails.setIncludeDobType(INCLUDE_DOB_TYPE);
        designatedMemberDetails.setIncludeAppointmentDate(true);
        designatedMemberDetails.setIncludeCountryOfResidence(true);
        designatedMemberDetails.setIncludeBasicInformation(true);
        itemOptions.setMemberDetails(memberDetails);
        itemOptions.setDesignatedMemberDetails(designatedMemberDetails);
        itemOptions.setCompanyType("llp");
        certificateItemDTO.setItemOptions(itemOptions);

        //when
        final List<String> errors = validatorUnderTest.getValidationErrors(certificateItemDTO);

        //then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Request is valid if company type is limited-partnership and appropriate fields set")
    void allowGeneralPartnersLimitedPartnersPrinciplePlaceOfBusinessGeneralNatureOfBusinessInformationFieldValuesForLimitedPartnerships() {
        //given
        final CertificateItemDTO certificateItemDTO = new CertificateItemDTO();
        final CertificateItemOptions itemOptions = new CertificateItemOptions();
        final GeneralPartnerDetails generalPartnerDetails = new GeneralPartnerDetails();
        final LimitedPartnerDetails limitedPartnerDetails = new LimitedPartnerDetails();
        final PrinciplePlaceOfBusinessDetails principlePlaceOfBusinessDetails = new PrinciplePlaceOfBusinessDetails();
        generalPartnerDetails.setIncludeBasicInformation(true);
        limitedPartnerDetails.setIncludeBasicInformation(true);
        principlePlaceOfBusinessDetails.setIncludeDates(true);
        principlePlaceOfBusinessDetails.setIncludeAddressRecordsType(INCLUDE_ADDRESS_RECORDS_TYPE);
        itemOptions.setGeneralPartnerDetails(generalPartnerDetails);
        itemOptions.setLimitedPartnerDetails(limitedPartnerDetails);
        itemOptions.setPrinciplePlaceOfBusinessDetails(principlePlaceOfBusinessDetails);
        itemOptions.setIncludeGeneralNatureOfBusinessInformation(true);
        itemOptions.setCompanyType("limited-partnership");
        certificateItemDTO.setItemOptions(itemOptions);

        //when
        final List<String> errors = validatorUnderTest.getValidationErrors(certificateItemDTO);

        //then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Reports only the incorrectly set fields")
    void reportsOnlyIncorrectlySetFields() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        final DirectorOrSecretaryDetails details = new DirectorOrSecretaryDetails();
        details.setIncludeAddress(true);
        details.setIncludeAppointmentDate(false);
        details.setIncludeNationality(true);
        details.setIncludeOccupation(true);
        options.setDirectorDetails(details);
        options.setSecretaryDetails(details);
        item.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, contains(
                "director_details: include_address, include_nationality, include_occupation must not be true when "
                        + "include_basic_information is false",
                "secretary_details: include_address, include_nationality, include_occupation must not be true when "
                        + "include_basic_information is false"));
    }

    @Test
    @DisplayName("Handles absence of item options smoothly")
    void handlesAbsenceOfItemOptionsSmoothly() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Handles absence of details smoothly")
    void handlesAbsenceOfDetailsSmoothly() {
        // Given
        final CertificateItemDTO item = new CertificateItemDTO();
        final CertificateItemOptions options = new CertificateItemOptions();
        item.setItemOptions(options);

        // When
        final List<String> errors = validatorUnderTest.getValidationErrors(item);

        // Then
        assertThat(errors, is(empty()));
    }

    @Test
    @DisplayName("Request is invalid if directors' or secretaries' details specified for an llp")
    void rejectDirectorsOrSecretariesDetailsIfSpecifiedForLlp() {
        //given
        final CertificateItemDTO certificateItemDTO = new CertificateItemDTO();
        final CertificateItemOptions certificateItemOptions = new CertificateItemOptions();
        final DirectorOrSecretaryDetails directorOrSecretaryDetails = new DirectorOrSecretaryDetails();
        certificateItemOptions.setCompanyType("llp");
        certificateItemOptions.setDirectorDetails(directorOrSecretaryDetails);
        certificateItemOptions.setSecretaryDetails(directorOrSecretaryDetails);
        certificateItemDTO.setItemOptions(certificateItemOptions);

        //when
        final List<String> errors = validatorUnderTest.getValidationErrors(certificateItemDTO);

        //then
        assertThat(errors, contains(
                "include_director_details: must not exist when company type is llp",
                "include_secretary_details: must not exist when company type is llp"
        ));
    }

    @Test
    @DisplayName("Request is invalid if directors' or secretaries' details specified for an limited-partnership")
    void rejectDirectorsOrSecretariesDetailsIfSpecifiedForLimitedPartnership() {
        //given
        final CertificateItemDTO certificateItemDTO = new CertificateItemDTO();
        final CertificateItemOptions certificateItemOptions = new CertificateItemOptions();
        final DirectorOrSecretaryDetails directorOrSecretaryDetails = new DirectorOrSecretaryDetails();
        certificateItemOptions.setCompanyType("limited-partnership");
        certificateItemOptions.setDirectorDetails(directorOrSecretaryDetails);
        certificateItemOptions.setSecretaryDetails(directorOrSecretaryDetails);
        certificateItemDTO.setItemOptions(certificateItemOptions);

        //when
        final List<String> errors = validatorUnderTest.getValidationErrors(certificateItemDTO);

        //then
        assertThat(errors, contains(
                "include_director_details: must not exist when company type is limited-partnership",
                "include_secretary_details: must not exist when company type is limited-partnership"
        ));
    }
}
