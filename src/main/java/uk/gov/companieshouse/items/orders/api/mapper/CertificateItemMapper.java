package uk.gov.companieshouse.items.orders.api.mapper;

import org.mapstruct.Mapper;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;

@Mapper(componentModel = "spring")
public interface CertificateItemMapper {
    CertificateItem certificateItemDTOtoCertificateItem(CertificateItemDTO certificateItemDTO);
}
