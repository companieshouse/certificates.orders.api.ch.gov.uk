package uk.gov.companieshouse.certificates.orders.api.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptionsConfig;
import uk.gov.companieshouse.certificates.orders.api.controller.ApiErrors;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.DesignatedMemberDetails;
import uk.gov.companieshouse.certificates.orders.api.model.GeneralPartnerDetails;
import uk.gov.companieshouse.certificates.orders.api.model.IncludeAddressRecordsType;
import uk.gov.companieshouse.certificates.orders.api.model.IncludeDobType;
import uk.gov.companieshouse.certificates.orders.api.model.LimitedPartnerDetails;
import uk.gov.companieshouse.certificates.orders.api.model.MemberDetails;
import uk.gov.companieshouse.certificates.orders.api.model.PrincipalPlaceOfBusinessDetails;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;

@Import({CertificateOptionsValidatorConfig.class, FeatureOptionsConfig.class})
@SpringBootTest
@ActiveProfiles("feature-flags-disabled")
class CreateItemRequestValidatorFeatureFlagsDisabledIntegrationTest {

    @MockBean
    private RequestValidatable requestValidatable;

    private CertificateItemOptions certificateItemOptions;

    @Autowired
    private CreateItemRequestValidator validatorUnderTest;

    @BeforeEach
    void beforeEach() {
        certificateItemOptions = new CertificateItemOptions();
        when(requestValidatable.getItemOptions()).thenReturn(certificateItemOptions);
    }

    @Test
    @DisplayName("Request is invalid if company type is llp and appropriate fields set")
    void correctlyErrorWhenCompanyTypeIsLLPAndMembersAndLLPSpecificFieldsAreSet() {
        //given
        MemberDetails memberDetails = new MemberDetails();
        memberDetails.setIncludeAddress(true);
        memberDetails.setIncludeDobType(IncludeDobType.PARTIAL);
        memberDetails.setIncludeAppointmentDate(true);
        memberDetails.setIncludeCountryOfResidence(true);
        memberDetails.setIncludeBasicInformation(true);

        DesignatedMemberDetails designatedMemberDetails = new DesignatedMemberDetails();
        designatedMemberDetails.setIncludeAddress(true);
        designatedMemberDetails.setIncludeDobType(IncludeDobType.PARTIAL);
        designatedMemberDetails.setIncludeAppointmentDate(true);
        designatedMemberDetails.setIncludeCountryOfResidence(true);
        designatedMemberDetails.setIncludeBasicInformation(true);
	
        certificateItemOptions.setMemberDetails(memberDetails);
        certificateItemOptions.setDesignatedMemberDetails(designatedMemberDetails);
        certificateItemOptions.setCompanyType("llp");
        certificateItemOptions.setCompanyStatus(CompanyStatus.ACTIVE.getStatusName());

        //when
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        //then
        assertThat(errors, containsInAnyOrder(
                ApiErrors.raiseError(ApiErrors.ERR_DESIGNATED_MEMBERS_DETAILS_SUPPLIED, "include_designated_member_details: must not exist when company type is llp"),
                ApiErrors.raiseError(ApiErrors.ERR_MEMBERS_DETAILS_SUPPLIED, "include_member_details: must not exist when company type is llp")
            )
        );
    }

    @Test
    @DisplayName(
            "Request is valid if company type is limited-partnership and appropriate fields set")
    void correctlyErrorWhenCompanyTypeIsLPAndLPSpecificFieldsAreSet() {
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

        //when
        final List<ApiError> errors = validatorUnderTest.getValidationErrors(requestValidatable);

        //then
        assertThat(errors, containsInAnyOrder(
                ApiErrors.raiseError(ApiErrors.ERR_GENERAL_PARTNER_DETAILS_SUPPLIED, "include_general_partner_details: must not exist when company type is limited-partnership"),
                ApiErrors.raiseError(ApiErrors.ERR_LIMITED_PARTNER_DETAILS_SUPPLIED, "include_limited_partner_details: must not exist when company type is limited-partnership"),
                ApiErrors.raiseError(ApiErrors.ERR_PRINCIPAL_PLACE_OF_BUSINESS_DETAILS_SUPPLIED, "include_principal_place_of_business_details: must not exist when company type is limited-partnership"),
                ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_GENERAL_NATURE_OF_BUSINESS_INFORMATION_SUPPLIED, "include_general_nature_of_business_information: must not exist when company type is limited-partnership")
        ));
    }
}
