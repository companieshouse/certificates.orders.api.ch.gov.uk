package uk.gov.companieshouse.certificates.orders.api.validator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptionsConfig;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.DesignatedMemberDetails;
import uk.gov.companieshouse.certificates.orders.api.model.DirectorOrSecretaryDetails;
import uk.gov.companieshouse.certificates.orders.api.model.GeneralPartnerDetails;
import uk.gov.companieshouse.certificates.orders.api.model.IncludeAddressRecordsType;
import uk.gov.companieshouse.certificates.orders.api.model.IncludeDobType;
import uk.gov.companieshouse.certificates.orders.api.model.LimitedPartnerDetails;
import uk.gov.companieshouse.certificates.orders.api.model.MemberDetails;
import uk.gov.companieshouse.certificates.orders.api.model.PrincipalPlaceOfBusinessDetails;
import uk.gov.companieshouse.certificates.orders.api.model.RegisteredOfficeAddressDetails;

@Import({CertificateOptionsValidatorConfig.class, FeatureOptionsConfig.class})
@SpringBootTest
@ActiveProfiles("feature-flags-disabled")
class CreateItemRequestValidatorFeatureFlagsDisabledTest {

    private static final IncludeAddressRecordsType
            INCLUDE_ADDRESS_RECORDS_TYPE =
            IncludeAddressRecordsType.CURRENT;
    private static final DirectorOrSecretaryDetails DIRECTOR_OR_SECRETARY_DETAILS;
    private static final RegisteredOfficeAddressDetails REGISTERED_OFFICE_ADDRESS_DETAILS;
    private static final boolean INCLUDE_ADDRESS = true;
    private static final boolean INCLUDE_APPOINTMENT_DATE = false;
    private static final boolean INCLUDE_BASIC_INFORMATION = true;
    private static final boolean INCLUDE_COUNTRY_OF_RESIDENCE = false;
    private static final IncludeDobType INCLUDE_DOB_TYPE = IncludeDobType.PARTIAL;
    private static final boolean INCLUDE_NATIONALITY = false;
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
        REGISTERED_OFFICE_ADDRESS_DETAILS.setIncludeAddressRecordsType(
                INCLUDE_ADDRESS_RECORDS_TYPE);
        REGISTERED_OFFICE_ADDRESS_DETAILS.setIncludeDates(INCLUDE_DATES);
    }

    @Autowired
    private CreateItemRequestValidator validatorUnderTest;

    @Test
    @DisplayName("Request is valid if company type is llp and appropriate fields set")
    void correctlyErrorWhenCompanyTypeIsLLPAndMembersAndLLPSpecificFieldsAreSet() {
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
        final List<String> errors =
                validatorUnderTest.getValidationErrors(
                        new RequestValidatableImpl(certificateItemDTO));

        //then
        assertThat(errors, containsInAnyOrder(
                "include_designated_member_details: must not exist when company type is llp",
                "include_member_details: must not exist when company type is llp"));
    }

    @Test
    @DisplayName(
            "Request is valid if company type is limited-partnership and appropriate fields set")
    void correctlyErrorWhenCompanyTypeIsLPAndLPSpecificFieldsAreSet() {
        //given
        final CertificateItemDTO certificateItemDTO = new CertificateItemDTO();
        final CertificateItemOptions itemOptions = new CertificateItemOptions();
        final GeneralPartnerDetails generalPartnerDetails = new GeneralPartnerDetails();
        final LimitedPartnerDetails limitedPartnerDetails = new LimitedPartnerDetails();
        final PrincipalPlaceOfBusinessDetails principalPlaceOfBusinessDetails = new PrincipalPlaceOfBusinessDetails();
        generalPartnerDetails.setIncludeBasicInformation(true);
        limitedPartnerDetails.setIncludeBasicInformation(true);
        principalPlaceOfBusinessDetails.setIncludeDates(true);
        principalPlaceOfBusinessDetails.setIncludeAddressRecordsType(INCLUDE_ADDRESS_RECORDS_TYPE);
        itemOptions.setGeneralPartnerDetails(generalPartnerDetails);
        itemOptions.setLimitedPartnerDetails(limitedPartnerDetails);
        itemOptions.setPrincipalPlaceOfBusinessDetails(principalPlaceOfBusinessDetails);
        itemOptions.setIncludeGeneralNatureOfBusinessInformation(true);
        itemOptions.setCompanyType("limited-partnership");
        certificateItemDTO.setItemOptions(itemOptions);

        //when
        final List<String> errors =
                validatorUnderTest.getValidationErrors(
                        new RequestValidatableImpl(certificateItemDTO));

        //then
        assertThat(errors, containsInAnyOrder(
                "include_general_partner_details: must not exist when company type is "
                        + "limited-partnership",
                "include_limited_partner_details: must not exist when company type is "
                        + "limited-partnership",
                "include_principal_place_of_business_details: must not exist when company type is limited-partnership",
                "include_general_nature_of_business_information: must not exist when company type is limited-partnership"));
    }

}
