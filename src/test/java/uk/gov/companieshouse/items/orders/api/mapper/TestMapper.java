package uk.gov.companieshouse.items.orders.api.mapper;


import org.mapstruct.Mapper;

/**
 * Temporary class to test how to map nested data elements.
 */
@Mapper(componentModel = "spring")
public interface TestMapper {
    Entity dtoToEntity(Dto dto);
    Dto entityToDto(Entity entity);
}
