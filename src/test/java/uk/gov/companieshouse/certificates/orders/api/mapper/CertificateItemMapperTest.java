package uk.gov.companieshouse.certificates.orders.api.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemCreate;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItem;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptionsRequest;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateType;
import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;
import uk.gov.companieshouse.certificates.orders.api.model.DeliveryMethod;
import uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale;
import uk.gov.companieshouse.certificates.orders.api.model.DesignatedMemberDetails;
import uk.gov.companieshouse.certificates.orders.api.model.DirectorOrSecretaryDetails;
import uk.gov.companieshouse.certificates.orders.api.model.GeneralPartnerDetails;
import uk.gov.companieshouse.certificates.orders.api.model.IncludeAddressRecordsType;
import uk.gov.companieshouse.certificates.orders.api.model.IncludeDobType;
import uk.gov.companieshouse.certificates.orders.api.model.ItemCosts;
import uk.gov.companieshouse.certificates.orders.api.model.LimitedPartnerDetails;
import uk.gov.companieshouse.certificates.orders.api.model.MemberDetails;
import uk.gov.companieshouse.certificates.orders.api.model.PrincipalPlaceOfBusinessDetails;
import uk.gov.companieshouse.certificates.orders.api.model.RegisteredOfficeAddressDetails;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.companieshouse.certificates.orders.api.model.CertificateType.INCORPORATION;
import static uk.gov.companieshouse.certificates.orders.api.model.CertificateType.INCORPORATION_WITH_ALL_NAME_CHANGES;
import static uk.gov.companieshouse.certificates.orders.api.model.CollectionLocation.BELFAST;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryMethod.POSTAL;
import static uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale.STANDARD;
import static uk.gov.companieshouse.certificates.orders.api.model.IncludeAddressRecordsType.CURRENT;
import static uk.gov.companieshouse.certificates.orders.api.model.IncludeDobType.PARTIAL;
import static uk.gov.companieshouse.certificates.orders.api.model.ProductType.CERTIFICATE;

/**
 * Unit tests the {@link CertificateItemMapper} class.
 */
@ExtendWith(SpringExtension.class)
@SpringJUnitConfig(CertificateItemMapperTest.Config.class)
class CertificateItemMapperTest {

    private static final String ID = "CHS00000000000000001";
    private static final String COMPANY_NUMBER = "00006444";
    private static final int QUANTITY = 10;
    private static final String DESCRIPTION = "Certificate";
    private static final String DESCRIPTION_IDENTIFIER = "Description Identifier";
    private static final Map<String, String> DESCRIPTION_VALUES = singletonMap("key1", "value1");
    private static final List<ItemCosts> ITEM_COSTS;
    private static final String KIND = "certificate";
    private static final boolean POSTAL_DELIVERY = true;
    private static final String CUSTOMER_REFERENCE = "Certificate ordered by NJ.";
    private static final String COMPANY_NAME = "Phillips & Daughters";
    private static final String TOKEN_ETAG = "9d39ea69b64c80ca42ed72328b48c303c4445e28";
    private static final String CONTACT_NUMBER = "+44 1234 123456";
    private static final boolean INCLUDE_COMPANY_OBJECTS_INFORMATION = true;
    private static final boolean INCLUDE_EMAIL_COPY = true;
    private static final boolean INCLUDE_GOOD_STANDING_INFORMATION = false;

    private static final boolean INCLUDE_ADDRESS = true;
    private static final boolean INCLUDE_APPOINTMENT_DATE = false;
    private static final boolean INCLUDE_BASIC_INFORMATION = true;
    private static final boolean INCLUDE_COUNTRY_OF_RESIDENCE = false;
    private static final IncludeDobType INCLUDE_DOB_TYPE = PARTIAL;
    private static final boolean INCLUDE_NATIONALITY= false;
    private static final boolean INCLUDE_OCCUPATION = true;

    private static final IncludeAddressRecordsType INCLUDE_ADDRESS_RECORDS_TYPE = CURRENT;
    private static final boolean INCLUDE_DATES = true;

    private static final CertificateItemOptions ITEM_OPTIONS;
    private static final CertificateItemOptionsRequest ITEM_OPTIONS_REQUEST;
    private static final CertificateItemOptionsRequest ITEM_OPTIONS_NO_DEFAULTS;
    private static final DirectorOrSecretaryDetails DIRECTOR_OR_SECRETARY_DETAILS;
    private static final RegisteredOfficeAddressDetails REGISTERED_OFFICE_ADDRESS_DETAILS;

    private static final DesignatedMemberDetails DESIGNATED_MEMBER_DETAILS;
    private static final MemberDetails MEMBER_DETAILS;
    private static final GeneralPartnerDetails GENERAL_PARTNER_DETAILS;
    private static final LimitedPartnerDetails LIMITED_PARTNER_DETAILS;
    private static final PrincipalPlaceOfBusinessDetails PRINCIPAL_PLACE_OF_BUSINESS_DETAILS;
    private static final boolean INCLUDE_GENERAL_NATURE_OF_BUSINESS_DETAILS = false;
    private static final String COMPANY_TYPE = "LTD";

    private static final CertificateType NO_DEFAULT_CERTIFICATE_TYPE = INCORPORATION_WITH_ALL_NAME_CHANGES;
    private static final DeliveryMethod NO_DEFAULT_DELIVERY_METHOD = POSTAL;
    private static final DeliveryTimescale NO_DEFAULT_DELIVERY_TIMESCALE = STANDARD;
    private static final int NO_DEFAULT_QUANTITY = 1;

    private static final String FORENAME = "John";
    private static final String SURNAME = "Smith";
    private static final String POSTAGE_COST = "0";
    private static final String TOTAL_ITEM_COST = "100";

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

        DESIGNATED_MEMBER_DETAILS = new DesignatedMemberDetails();
        DESIGNATED_MEMBER_DETAILS.setIncludeAddress(INCLUDE_ADDRESS);
        DESIGNATED_MEMBER_DETAILS.setIncludeAppointmentDate(INCLUDE_APPOINTMENT_DATE);
        DESIGNATED_MEMBER_DETAILS.setIncludeBasicInformation(INCLUDE_BASIC_INFORMATION);
        DESIGNATED_MEMBER_DETAILS.setIncludeCountryOfResidence(INCLUDE_COUNTRY_OF_RESIDENCE);
        DESIGNATED_MEMBER_DETAILS.setIncludeDobType(INCLUDE_DOB_TYPE);

        MEMBER_DETAILS = new MemberDetails();
        MEMBER_DETAILS.setIncludeAddress(INCLUDE_ADDRESS);
        MEMBER_DETAILS.setIncludeAppointmentDate(INCLUDE_APPOINTMENT_DATE);
        MEMBER_DETAILS.setIncludeBasicInformation(INCLUDE_BASIC_INFORMATION);
        MEMBER_DETAILS.setIncludeCountryOfResidence(INCLUDE_COUNTRY_OF_RESIDENCE);
        MEMBER_DETAILS.setIncludeDobType(INCLUDE_DOB_TYPE);

        GENERAL_PARTNER_DETAILS = new GeneralPartnerDetails();
        GENERAL_PARTNER_DETAILS.setIncludeBasicInformation(INCLUDE_BASIC_INFORMATION);

        LIMITED_PARTNER_DETAILS = new LimitedPartnerDetails();
        LIMITED_PARTNER_DETAILS.setIncludeBasicInformation(INCLUDE_BASIC_INFORMATION);

        PRINCIPAL_PLACE_OF_BUSINESS_DETAILS = new PrincipalPlaceOfBusinessDetails();
        PRINCIPAL_PLACE_OF_BUSINESS_DETAILS.setIncludeAddressRecordsType(INCLUDE_ADDRESS_RECORDS_TYPE);
        PRINCIPAL_PLACE_OF_BUSINESS_DETAILS.setIncludeDates(INCLUDE_DATES);

        ITEM_OPTIONS_REQUEST = new CertificateItemOptionsRequest();
        ITEM_OPTIONS_REQUEST.setCollectionLocation(BELFAST);
        ITEM_OPTIONS_REQUEST.setContactNumber(CONTACT_NUMBER);
        ITEM_OPTIONS_REQUEST.setDeliveryMethod(POSTAL);
        ITEM_OPTIONS_REQUEST.setDeliveryTimescale(STANDARD);
        ITEM_OPTIONS_REQUEST.setDirectorDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        ITEM_OPTIONS_REQUEST.setForename(FORENAME);
        ITEM_OPTIONS_REQUEST.setIncludeCompanyObjectsInformation(INCLUDE_COMPANY_OBJECTS_INFORMATION);
        ITEM_OPTIONS_REQUEST.setIncludeEmailCopy(INCLUDE_EMAIL_COPY);
        ITEM_OPTIONS_REQUEST.setIncludeGoodStandingInformation(INCLUDE_GOOD_STANDING_INFORMATION);
        ITEM_OPTIONS_REQUEST.setRegisteredOfficeAddressDetails(REGISTERED_OFFICE_ADDRESS_DETAILS);
        ITEM_OPTIONS_REQUEST.setSecretaryDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        ITEM_OPTIONS_REQUEST.setSurname(SURNAME);
        ITEM_OPTIONS_REQUEST.setDesignatedMemberDetails(DESIGNATED_MEMBER_DETAILS);
        ITEM_OPTIONS_REQUEST.setMemberDetails(MEMBER_DETAILS);
        ITEM_OPTIONS_REQUEST.setGeneralPartnerDetails(GENERAL_PARTNER_DETAILS);
        ITEM_OPTIONS_REQUEST.setLimitedPartnerDetails(LIMITED_PARTNER_DETAILS);
        ITEM_OPTIONS_REQUEST.setPrincipalPlaceOfBusinessDetails(PRINCIPAL_PLACE_OF_BUSINESS_DETAILS);
        ITEM_OPTIONS_REQUEST.setIncludeGeneralNatureOfBusinessInformation(INCLUDE_GENERAL_NATURE_OF_BUSINESS_DETAILS);

        ITEM_OPTIONS = new CertificateItemOptions();
        ITEM_OPTIONS.setCertificateType(INCORPORATION_WITH_ALL_NAME_CHANGES);
        ITEM_OPTIONS.setCompanyType(COMPANY_TYPE);
        ITEM_OPTIONS.setCompanyStatus(CompanyStatus.ACTIVE.getStatusName());
        ITEM_OPTIONS.setCollectionLocation(BELFAST);
        ITEM_OPTIONS.setContactNumber(CONTACT_NUMBER);
        ITEM_OPTIONS.setDeliveryMethod(POSTAL);
        ITEM_OPTIONS.setDeliveryTimescale(STANDARD);
        ITEM_OPTIONS.setDirectorDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        ITEM_OPTIONS.setForename(FORENAME);
        ITEM_OPTIONS.setIncludeCompanyObjectsInformation(INCLUDE_COMPANY_OBJECTS_INFORMATION);
        ITEM_OPTIONS.setIncludeEmailCopy(INCLUDE_EMAIL_COPY);
        ITEM_OPTIONS.setIncludeGoodStandingInformation(INCLUDE_GOOD_STANDING_INFORMATION);
        ITEM_OPTIONS.setRegisteredOfficeAddressDetails(REGISTERED_OFFICE_ADDRESS_DETAILS);
        ITEM_OPTIONS.setSecretaryDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        ITEM_OPTIONS.setSurname(SURNAME);
        ITEM_OPTIONS.setDesignatedMemberDetails(DESIGNATED_MEMBER_DETAILS);
        ITEM_OPTIONS.setMemberDetails(MEMBER_DETAILS);
        ITEM_OPTIONS.setGeneralPartnerDetails(GENERAL_PARTNER_DETAILS);
        ITEM_OPTIONS.setLimitedPartnerDetails(LIMITED_PARTNER_DETAILS);
        ITEM_OPTIONS.setPrincipalPlaceOfBusinessDetails(PRINCIPAL_PLACE_OF_BUSINESS_DETAILS);
        ITEM_OPTIONS.setIncludeGeneralNatureOfBusinessInformation(INCLUDE_GENERAL_NATURE_OF_BUSINESS_DETAILS);

        ITEM_OPTIONS_NO_DEFAULTS = new CertificateItemOptionsRequest();

        ITEM_COSTS = new ArrayList<>();
        ITEM_COSTS.add(new ItemCosts("1", "2", "3", CERTIFICATE));
    }

    @Configuration
    @ComponentScan(basePackageClasses = CertificateItemMapperTest.class)
    static class Config {}

    @Autowired
    private CertificateItemMapper mapperUnderTest;

    @Test
    void testCertificateItemDtoToEntityMapping() {
        final CertificateItemCreate dto = setupCertificateItemDTO();
        dto.setItemOptions(ITEM_OPTIONS_REQUEST);
        dto.setQuantity(QUANTITY);

        final CertificateItem item = mapperUnderTest.certificateItemCreateToCertificateItem(dto);

        assertThat(item.getData(), is(notNullValue()));
        assertThat(item.getCompanyNumber(), is(dto.getCompanyNumber()));
        assertThat(item.getCustomerReference(), is(dto.getCustomerReference()));
        assertThat(item.getQuantity(), is(dto.getQuantity()));
        assertThat(item.getKind(), is(dto.getKind()));
        assertThat(item.isPostalDelivery(), is(dto.isPostalDelivery()));
        assertItemOptionsSame(item.getItemOptions(), dto.getItemOptions());
    }

    @Test
    void testCertificateItemDtoToEntityMappingNoDefaults() {
        final CertificateItemCreate dto = setupCertificateItemDTO();
        dto.setItemOptions(ITEM_OPTIONS_NO_DEFAULTS);

        final CertificateItem item = mapperUnderTest.certificateItemCreateToCertificateItem(dto);

        assertThat(item.getData(), is(notNullValue()));
        assertThat(item.getCompanyNumber(), is(dto.getCompanyNumber()));
        assertThat(item.getCustomerReference(), is(dto.getCustomerReference()));
        assertThat(item.getQuantity(), is(NO_DEFAULT_QUANTITY));
        assertThat(item.getKind(), is(dto.getKind()));
        assertThat(item.isPostalDelivery(), is(dto.isPostalDelivery()));
        CertificateItemOptions itemOptions = item.getItemOptions();
        assertNull(itemOptions.getCertificateType());
        assertEquals(NO_DEFAULT_DELIVERY_METHOD, itemOptions.getDeliveryMethod());
        assertEquals(NO_DEFAULT_DELIVERY_TIMESCALE, itemOptions.getDeliveryTimescale());
    }

    @Test
    void testCertificateItemEntityToDtoMapping() {
        final CertificateItem item = new CertificateItem();
        item.setId(ID);
        item.setCompanyName(COMPANY_NAME);
        item.setCompanyNumber(COMPANY_NUMBER);
        item.setCustomerReference(CUSTOMER_REFERENCE);
        item.setQuantity(QUANTITY);
        item.setDescription(DESCRIPTION);
        item.setDescriptionIdentifier(DESCRIPTION_IDENTIFIER);
        item.setDescriptionValues(DESCRIPTION_VALUES);
        item.setItemCosts(ITEM_COSTS);
        item.setKind(KIND);
        item.setPostalDelivery(POSTAL_DELIVERY);
        item.setItemOptions(ITEM_OPTIONS);
        item.setEtag(TOKEN_ETAG);
        item.setPostageCost(POSTAGE_COST);
        item.setTotalItemCost(TOTAL_ITEM_COST);

        final CertificateItemCreate dto = mapperUnderTest.certificateItemToCertificateItemDTO(item);

        assertThat(dto.getCompanyNumber(), is(item.getCompanyNumber()));
        assertThat(dto.getCustomerReference(), is(item.getCustomerReference()));
        assertThat(dto.getQuantity(), is(item.getQuantity()));
        assertThat(dto.getKind(), is(item.getKind()));
        assertThat(dto.isPostalDelivery(), is(item.isPostalDelivery()));
        assertItemOptionsSame(item.getItemOptions(), dto.getItemOptions());
    }

    @Test
    void testEnrichCertificateItem() {
        //given
        CertificateItem certificateItem = new CertificateItem();
        CompanyProfileResource companyProfileResource = new CompanyProfileResource("TEST LTD", "ltd", CompanyStatus.ACTIVE);

        //when
        CertificateItem actual = mapperUnderTest.enrichCertificateItem("user", companyProfileResource, ()->CertificateType.INCORPORATION, certificateItem);

        //then
        assertThat(actual.getUserId(), is("user"));
        assertThat(actual.getCompanyName(), is("TEST LTD"));
        assertThat(actual.getItemOptions().getCompanyType(), is("ltd"));
        assertThat(actual.getItemOptions().getCompanyStatus(), is("active"));
        assertThat(actual.getItemOptions().getCertificateType(), is(INCORPORATION));
    }

    /**
     * Utility that asserts that each member of the options objects passed it has the same value
     * in both. The alternative would be to generate CertificateItemOptions equals() and hashCode()
     * and thoroughly unit test those.
     * @param actual options
     * @param expected options
     */
    private void assertItemOptionsSame(final CertificateItemOptions actual,
                                       final CertificateItemOptionsRequest expected) {
        assertThat(actual.getCollectionLocation(), is(expected.getCollectionLocation()));
        assertThat(actual.getContactNumber(), is(expected.getContactNumber()));
        assertThat(actual.getDeliveryMethod(), is(expected.getDeliveryMethod()));
        assertThat(actual.getDeliveryTimescale(), is(expected.getDeliveryTimescale()));
        assertDetailsSame(actual.getDirectorDetails(), expected.getDirectorDetails());
        assertThat(actual.getForename(), is(expected.getForename()));
        assertThat(actual.getIncludeCompanyObjectsInformation(), is(expected.getIncludeCompanyObjectsInformation()));
        assertThat(actual.getIncludeEmailCopy(), is(expected.getIncludeEmailCopy()));
        assertThat(actual.getIncludeGoodStandingInformation(), is(expected.getIncludeGoodStandingInformation()));
        assertAddressDetailsSame(actual.getRegisteredOfficeAddressDetails(), expected.getRegisteredOfficeAddressDetails());
        assertDetailsSame(actual.getSecretaryDetails(), expected.getSecretaryDetails());
        assertThat(actual.getSurname(), is(expected.getSurname()));

        // New LLP and LP options
        assertDesignatedMembersDetailsSame(actual.getDesignatedMemberDetails(), expected.getDesignatedMemberDetails());
        assertMembersDetailsSame(actual.getMemberDetails(), expected.getMemberDetails());
        assertGeneralPartnerDetailsSame(actual.getGeneralPartnerDetails(), expected.getGeneralPartnerDetails());
        assertLimitedPartnerDetailsSame(actual.getLimitedPartnerDetails(), expected.getLimitedPartnerDetails());
        assertPrincipalPlaceOfBusinessDetailsSame(actual.getPrincipalPlaceOfBusinessDetails(), expected.getPrincipalPlaceOfBusinessDetails());
        assertThat(actual.getIncludeGeneralNatureOfBusinessInformation(), is(expected.getIncludeGeneralNatureOfBusinessInformation()));
    }

    /**
     * Utility that asserts that each member of the director or secretary details options objects passed it has
     * the same value in both.
     * @param details1 director/secretary options
     * @param details2 director/secretary options
     */
    private void assertDetailsSame(final DirectorOrSecretaryDetails details1,
                                   final DirectorOrSecretaryDetails details2) {
        assertThat(details1.getIncludeAddress(), is(details2.getIncludeAddress()));
        assertThat(details1.getIncludeAppointmentDate(), is(details2.getIncludeAppointmentDate()));
        assertThat(details1.getIncludeBasicInformation(), is(details2.getIncludeBasicInformation()));
        assertThat(details1.getIncludeCountryOfResidence(), is(details2.getIncludeCountryOfResidence()));
        assertThat(details1.getIncludeDobType(), is(details2.getIncludeDobType()));
        assertThat(details1.getIncludeNationality(), is(details2.getIncludeNationality()));
        assertThat(details1.getIncludeOccupation(), is(details2.getIncludeOccupation()));
    }

    /**
     * Utility that asserts that each member of the registered office address details options objects passed it has
     * the same value in both.
     * @param details1 registered office address options
     * @param details2 registered office address options
     */
    private void assertAddressDetailsSame(final RegisteredOfficeAddressDetails details1,
                                          final RegisteredOfficeAddressDetails details2) {
        assertThat(details1.getIncludeAddressRecordsType(), is(details2.getIncludeAddressRecordsType()));
        assertThat(details1.getIncludeDates(), is(details2.getIncludeDates()));
    }

    private CertificateItemCreate setupCertificateItemDTO() {
        CertificateItemCreate dto = new CertificateItemCreate();
        dto.setCompanyNumber(COMPANY_NUMBER);
        dto.setCustomerReference(CUSTOMER_REFERENCE);
        dto.setKind(KIND);
        dto.setPostalDelivery(POSTAL_DELIVERY);
        return dto;
    }

    // Helper methods for assertions on LP and LLP details
    private void assertDesignatedMembersDetailsSame(DesignatedMemberDetails details1, DesignatedMemberDetails details2) {
        assertThat(details1.getIncludeAddress(), is(details2.getIncludeAddress()));
        assertThat(details1.getIncludeAppointmentDate(), is(details2.getIncludeAppointmentDate()));
        assertThat(details1.getIncludeBasicInformation(), is(details2.getIncludeBasicInformation()));
        assertThat(details1.getIncludeCountryOfResidence(), is(details2.getIncludeCountryOfResidence()));
        assertThat(details1.getIncludeDobType(), is(details2.getIncludeDobType()));
    }

    private void assertMembersDetailsSame(MemberDetails details1, MemberDetails details2) {
        assertThat(details1.getIncludeAddress(), is(details2.getIncludeAddress()));
        assertThat(details1.getIncludeAppointmentDate(), is(details2.getIncludeAppointmentDate()));
        assertThat(details1.getIncludeBasicInformation(), is(details2.getIncludeBasicInformation()));
        assertThat(details1.getIncludeCountryOfResidence(), is(details2.getIncludeCountryOfResidence()));
        assertThat(details1.getIncludeDobType(), is(details2.getIncludeDobType()));
    }

    private void assertGeneralPartnerDetailsSame(GeneralPartnerDetails details1, GeneralPartnerDetails details2) {
        assertEquals(details1.getIncludeBasicInformation(), details2.getIncludeBasicInformation());
    }

    private void assertLimitedPartnerDetailsSame(LimitedPartnerDetails details1, LimitedPartnerDetails details2) {
        assertEquals(details1.getIncludeBasicInformation(), details2.getIncludeBasicInformation());
    }

    private void assertPrincipalPlaceOfBusinessDetailsSame(PrincipalPlaceOfBusinessDetails details1, PrincipalPlaceOfBusinessDetails details2) {
        assertThat(details1.getIncludeAddressRecordsType(), is(details2.getIncludeAddressRecordsType()));
        assertEquals(details1.getIncludeDates(), details2.getIncludeDates());
    }
}
