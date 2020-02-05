package uk.gov.companieshouse.items.orders.api.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.model.*;

import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static uk.gov.companieshouse.items.orders.api.model.CertificateType.INCORPORATION;
import static uk.gov.companieshouse.items.orders.api.model.CollectionLocation.BELFAST;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryMethod.POSTAL;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.STANDARD;
import static uk.gov.companieshouse.items.orders.api.model.IncludeAddressRecordsType.CURRENT;
import static uk.gov.companieshouse.items.orders.api.model.IncludeDobType.PARTIAL;

/**
 * Unit tests the {@link CertificateItemMapper} class.
 */
@ExtendWith(SpringExtension.class)
@SpringJUnitConfig(CertificateItemMapperTest.Config.class)
public class CertificateItemMapperTest {

    private static final String ID = "CHS00000000000000001";
    private static final String COMPANY_NUMBER = "00006444";
    private static final int QUANTITY = 10;
    private static final String DESCRIPTION = "Certificate";
    private static final String DESCRIPTION_IDENTIFIER = "Description Identifier";
    private static final Map<String, String> DESCRIPTION_VALUES = singletonMap("key1", "value1");
    private static final ItemCosts ITEM_COSTS = new ItemCosts("1", "2", "3", "4");
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
    private static final DirectorOrSecretaryDetails DIRECTOR_OR_SECRETARY_DETAILS;
    private static final RegisteredOfficeAddressDetails REGISTERED_OFFICE_ADDRESS_DETAILS;

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

        ITEM_OPTIONS = new CertificateItemOptions();
        ITEM_OPTIONS.setCertificateType(INCORPORATION);
        ITEM_OPTIONS.setCollectionLocation(BELFAST);
        ITEM_OPTIONS.setContactNumber(CONTACT_NUMBER);
        ITEM_OPTIONS.setDeliveryMethod(POSTAL);
        ITEM_OPTIONS.setDeliveryTimescale(STANDARD);
        ITEM_OPTIONS.setDirectorDetails(DIRECTOR_OR_SECRETARY_DETAILS);
        ITEM_OPTIONS.setIncludeCompanyObjectsInformation(INCLUDE_COMPANY_OBJECTS_INFORMATION);
        ITEM_OPTIONS.setIncludeEmailCopy(INCLUDE_EMAIL_COPY);
        ITEM_OPTIONS.setIncludeGoodStandingInformation(INCLUDE_GOOD_STANDING_INFORMATION);
        ITEM_OPTIONS.setRegisteredOfficeAddressDetails(REGISTERED_OFFICE_ADDRESS_DETAILS);
        ITEM_OPTIONS.setSecretaryDetails(DIRECTOR_OR_SECRETARY_DETAILS);
    }

    @Configuration
    @ComponentScan(basePackageClasses = CertificateItemMapperTest.class)
    static class Config {}

    @Autowired
    private CertificateItemMapper mapperUnderTest;

    @Test
    void testCertificateItemDtoToEntityMapping() {
        final CertificateItemDTO dto = new CertificateItemDTO();
        dto.setId(ID);
        dto.setCompanyName(COMPANY_NAME);
        dto.setCompanyNumber(COMPANY_NUMBER);
        dto.setCustomerReference(CUSTOMER_REFERENCE);
        dto.setQuantity(QUANTITY);
        dto.setDescription(DESCRIPTION);
        dto.setDescriptionIdentifier(DESCRIPTION_IDENTIFIER);
        dto.setDescriptionValues(DESCRIPTION_VALUES);
        dto.setItemCosts(ITEM_COSTS);
        dto.setKind(KIND);
        dto.setPostalDelivery(POSTAL_DELIVERY);
        dto.setItemOptions(ITEM_OPTIONS);
        final CertificateItem item = mapperUnderTest.certificateItemDTOtoCertificateItem(dto);

        assertThat(item.getId(), is(dto.getId()));
        assertThat(item.getData(), is(notNullValue()));
        assertThat(item.getData().getId(), is(dto.getId()));
        assertThat(item.getCompanyName(), is(dto.getCompanyName()));
        assertThat(item.getCompanyNumber(), is(dto.getCompanyNumber()));
        assertThat(item.getCustomerReference(), is(dto.getCustomerReference()));
        assertThat(item.getQuantity(), is(dto.getQuantity()));
        assertThat(item.getDescription(), is(dto.getDescription()));
        assertThat(item.getDescriptionIdentifier(), is(dto.getDescriptionIdentifier()));
        assertThat(item.getDescriptionValues(), is(dto.getDescriptionValues()));
        assertThat(item.getItemCosts(), is(dto.getItemCosts()));
        assertThat(item.getKind(), is(dto.getKind()));
        assertThat(item.isPostalDelivery(), is(dto.isPostalDelivery()));
        assertItemOptionsSame(dto.getItemOptions(), item.getItemOptions());
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

        final CertificateItemDTO dto = mapperUnderTest.certificateItemToCertificateItemDTO(item);

        assertThat(dto.getId(), is(item.getId()));
        assertThat(dto.getCompanyName(), is(item.getCompanyName()));
        assertThat(dto.getCompanyNumber(), is(item.getCompanyNumber()));
        assertThat(dto.getCustomerReference(), is(item.getCustomerReference()));
        assertThat(dto.getQuantity(), is(item.getQuantity()));
        assertThat(dto.getDescription(), is(item.getDescription()));
        assertThat(dto.getDescriptionIdentifier(), is(item.getDescriptionIdentifier()));
        assertThat(dto.getDescriptionValues(), is(item.getDescriptionValues()));
        assertThat(dto.getItemCosts(), is(item.getItemCosts()));
        assertThat(dto.getKind(), is(item.getKind()));
        assertThat(dto.isPostalDelivery(), is(item.isPostalDelivery()));
        assertItemOptionsSame(dto.getItemOptions(), item.getItemOptions());
        assertThat(dto.getEtag(), is(item.getEtag()));
    }

    /**
     * Utility that asserts that each member of the options objects passed it has the same value
     * in both. The alternative would be to generate CertificateItemOptions equals() and hashCode()
     * and thoroughly unit test those.
     * @param options1 options
     * @param options2 options
     */
    private void assertItemOptionsSame(final CertificateItemOptions options1,
                                       final CertificateItemOptions options2) {
        assertThat(options1.getCertificateType(), is(options2.getCertificateType()));
        assertThat(options1.getCollectionLocation(), is(options2.getCollectionLocation()));
        assertThat(options1.getContactNumber(), is(options2.getContactNumber()));
        assertThat(options1.getDeliveryMethod(), is(options2.getDeliveryMethod()));
        assertThat(options1.getDeliveryTimescale(), is(options2.getDeliveryTimescale()));
        assertDetailsSame(options1.getDirectorDetails(), options2.getDirectorDetails());
        assertThat(options1.getIncludeCompanyObjectsInformation(), is(options2.getIncludeCompanyObjectsInformation()));
        assertThat(options1.getIncludeEmailCopy(), is(options2.getIncludeEmailCopy()));
        assertThat(options1.getIncludeGoodStandingInformation(), is(options2.getIncludeGoodStandingInformation()));
        assertAddressDetailsSame(options1.getRegisteredOfficeAddressDetails(), options2.getRegisteredOfficeAddressDetails());
        assertDetailsSame(options1.getSecretaryDetails(), options2.getSecretaryDetails());
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

}
