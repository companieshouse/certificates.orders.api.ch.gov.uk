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
import uk.gov.companieshouse.items.orders.api.model.ItemData;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Unit tests the {@link CertificateItemMapper} class.
 */
@ExtendWith(SpringExtension.class)
@SpringJUnitConfig(CertificateItemMapperTest.Config.class)
public class CertificateItemMapperTest {

    private static final String ID = "CHS00000000000000001";
    private static final String COMPANY_NUMBER = "00006444";
    private static final int QUANTITY = 10;

    @Configuration
    @ComponentScan(basePackageClasses = CertificateItemMapperTest.class)
    static class Config {}

    @Autowired
    private CertificateItemMapper mapperUnderTest;

    @Test
    void testDtoToEntityMappingMethod() {
        final CertificateItemDTO dto = new CertificateItemDTO();
        dto.setId(ID);
        dto.setCompanyNumber(COMPANY_NUMBER);
        dto.setQuantity(QUANTITY);
        final CertificateItem item = mapperUnderTest.certificateItemDTOtoCertificateItem(dto);

        assertThat(item.getId(), is(dto.getId()));
        assertThat(item.getData(), is(notNullValue()));
        assertThat(item.getData().getCompanyNumber(), is(dto.getCompanyNumber()));
        assertThat(item.getData().getQuantity(), is(dto.getQuantity()));

        // TODO
//        assertThat(entity.getId(), is(dto.getId()));
//        assertThat(entity.getData(), is(notNullValue()));
//        assertThat(entity.getData().getDatum(), is(dto.getDatum()));
    }

    @Test
    void testEntityToDtoMappingMethod() {
        final CertificateItem item = new CertificateItem();
        item.setId(ID);
        final ItemData data = new ItemData();
        data.setCompanyNumber(COMPANY_NUMBER);
        data.setQuantity(QUANTITY);
        item.setData(data);
        final CertificateItemDTO dto = mapperUnderTest.certificateItemToCertificateItemDTO(item);

        assertThat(dto.getId(), is(item.getId()));
        assertThat(dto.getCompanyNumber(), is(item.getData().getCompanyNumber()));
        assertThat(dto.getQuantity(), is(item.getData().getQuantity()));

        // TODO
//        assertThat(dto.getId(), is(entity.getId()));
//        assertThat(dto.getDatum(), is(entity.getData().getDatum()));


    }

}
