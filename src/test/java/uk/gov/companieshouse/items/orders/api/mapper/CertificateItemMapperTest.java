package uk.gov.companieshouse.items.orders.api.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.model.CertificateItemOptions;
import uk.gov.companieshouse.items.orders.api.model.ItemCosts;

import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.STANDARD;

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

    private static final CertificateItemOptions ITEM_OPTIONS;

    static {
        ITEM_OPTIONS = new CertificateItemOptions();
        ITEM_OPTIONS.setDeliveryTimescale(STANDARD);
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
        assertThat(item.getCompanyNumber(), is(dto.getCompanyNumber()));
        assertThat(item.getCustomerReference(), is(dto.getCustomerReference()));
        assertThat(item.getQuantity(), is(dto.getQuantity()));
        assertThat(item.getDescription(), is(dto.getDescription()));
        assertThat(item.getDescriptionIdentifier(), is(dto.getDescriptionIdentifier()));
        assertThat(item.getDescriptionValues(), is(dto.getDescriptionValues()));
        assertThat(item.getItemCosts(), is(dto.getItemCosts()));
        assertThat(item.getKind(), is(dto.getKind()));
        assertThat(item.isPostalDelivery(), is(dto.isPostalDelivery()));
        assertThat(item.getItemOptions(), is(dto.getItemOptions()));
    }

    @Test
    void testCertificateItemEntityToDtoMapping() {
        final CertificateItem item = new CertificateItem();
        item.setId(ID);
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
        final CertificateItemDTO dto = mapperUnderTest.certificateItemToCertificateItemDTO(item);

        assertThat(dto.getId(), is(item.getId()));
        assertThat(dto.getCompanyNumber(), is(item.getCompanyNumber()));
        assertThat(dto.getCustomerReference(), is(item.getCustomerReference()));
        assertThat(dto.getQuantity(), is(item.getQuantity()));
        assertThat(dto.getDescription(), is(item.getDescription()));
        assertThat(dto.getDescriptionIdentifier(), is(item.getDescriptionIdentifier()));
        assertThat(dto.getDescriptionValues(), is(item.getDescriptionValues()));
        assertThat(dto.getItemCosts(), is(item.getItemCosts()));
        assertThat(dto.getKind(), is(item.getKind()));
        assertThat(dto.isPostalDelivery(), is(item.isPostalDelivery()));
        assertThat(dto.getItemOptions(), is(item.getItemOptions()));
    }

}
