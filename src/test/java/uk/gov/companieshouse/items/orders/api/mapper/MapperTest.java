package uk.gov.companieshouse.items.orders.api.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Temporary class to test how to map nested data elements.
 */
@ExtendWith(SpringExtension.class)
@SpringJUnitConfig(MapperTest.Config.class)
public class MapperTest {

    @Configuration
    @ComponentScan(basePackageClasses = MapperTest.class)
    static class Config {}

    @Autowired
    private TestMapper mapper;

    @Test
    void testDtoToEntity() {
        final Dto dto = new Dto("1", "X");
        final Entity entity = mapper.dtoToEntity(dto);

        assertThat(entity.getId(), is(dto.getId()));
        assertThat(entity.getData(), is(notNullValue()));
        assertThat(entity/*.getData()*/.getDatum(), is(dto.getDatum()));
    }

    @Test
    void testEntityToDto() {
        final Entity entity = new Entity("1", new EntityData("X"));
        final Dto dto = mapper.entityToDto(entity);

        assertThat(dto.getId(), is(entity.getId()));
        assertThat(dto.getDatum(), is(entity/*.getData()*/.getDatum()));
    }

}
