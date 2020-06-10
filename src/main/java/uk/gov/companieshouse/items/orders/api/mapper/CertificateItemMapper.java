package uk.gov.companieshouse.items.orders.api.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import uk.gov.companieshouse.items.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.items.orders.api.model.CertificateItem;
import uk.gov.companieshouse.items.orders.api.model.CertificateType;
import uk.gov.companieshouse.items.orders.api.model.DeliveryMethod;
import uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale;

@Mapper(componentModel = "spring")
public interface CertificateItemMapper {
    CertificateItem certificateItemDTOtoCertificateItem(CertificateItemDTO certificateItemDTO);
    CertificateItemDTO certificateItemToCertificateItemDTO(CertificateItem certificateItem);

    @AfterMapping
    default void setDefaults(CertificateItemDTO certificateItemDTO, @MappingTarget CertificateItem certificateItem){
        int quantity = certificateItemDTO.getQuantity();
        certificateItem.setQuantity(quantity > 0 ? quantity : 1);

        DeliveryMethod deliveryMethod = certificateItemDTO.getItemOptions().getDeliveryMethod();
        certificateItem.getItemOptions().setDeliveryMethod(
                deliveryMethod != null ? deliveryMethod : DeliveryMethod.POSTAL);

        DeliveryTimescale deliveryTimescale = certificateItemDTO.getItemOptions().getDeliveryTimescale();
        certificateItem.getItemOptions().setDeliveryTimescale(
                deliveryTimescale != null ? deliveryTimescale : DeliveryTimescale.STANDARD);

        CertificateType certificateType = certificateItemDTO.getItemOptions().getCertificateType();
        certificateItem.getItemOptions().setCertificateType(
                certificateType != null ? certificateType : CertificateType.INCORPORATION_WITH_ALL_NAME_CHANGES);
    }
}
