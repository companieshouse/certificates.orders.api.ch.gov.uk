package uk.gov.companieshouse.items.orders.api.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Temporary class to test how to map nested data elements.
 */
@Mapper(componentModel = "spring")
public interface TestMapper {

    //@Mapping(target = "data.datum", source = "datum")
    Entity dtoToEntity(Dto dto);

    //@Mapping(target = "datum", source = "data.datum")
    Dto entityToDto(Entity entity);
}
