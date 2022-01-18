package uk.gov.companieshouse.certificates.orders.api.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemRequestDTO;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemInitialDTO;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemResponseDTO;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItem;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateType;
import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;
import uk.gov.companieshouse.certificates.orders.api.model.DeliveryMethod;
import uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale;

@Mapper(componentModel = "spring")
public interface CertificateItemMapper {
    CertificateItem certificateItemDTOtoCertificateItem(CertificateItemRequestDTO certificateItemRequestDTO);
    CertificateItem certificateItemDTOtoCertificateItem(CertificateItemInitialDTO certificateItemInitialDTO);
    CertificateItemRequestDTO certificateItemToCertificateItemDTO(CertificateItem certificateItem);
    CertificateItemResponseDTO certificateItemToCertificateItemResponseDTO(CertificateItem certificateItem);
    @Mapping(source = "identity", target = "certificateItem.userId")
    @Mapping(source = "companyProfile.companyName", target = "certificateItem.companyName")
    @Mapping(source = "companyProfile.companyStatus.statusName", target = "certificateItem.itemOptions.companyStatus")
    @Mapping(source = "companyProfile.companyType", target = "certificateItem.itemOptions.companyType")
    CertificateItem enrichCertificateItem(String identity, CompanyProfileResource companyProfile,
                                          @MappingTarget CertificateItem certificateItem);

    @AfterMapping
    default void setDefaults(CertificateItemRequestDTO certificateItemRequestDTO, @MappingTarget CertificateItem certificateItem){
        int quantity = certificateItemRequestDTO.getQuantity();
        certificateItem.setQuantity(quantity > 0 ? quantity : 1);

        DeliveryMethod deliveryMethod = certificateItemRequestDTO.getItemOptions().getDeliveryMethod();
        certificateItem.getItemOptions().setDeliveryMethod(
                deliveryMethod != null ? deliveryMethod : DeliveryMethod.POSTAL);

        DeliveryTimescale deliveryTimescale = certificateItemRequestDTO.getItemOptions().getDeliveryTimescale();
        certificateItem.getItemOptions().setDeliveryTimescale(
                deliveryTimescale != null ? deliveryTimescale : DeliveryTimescale.STANDARD);

        CertificateType certificateType = certificateItemRequestDTO.getItemOptions().getCertificateType();
        certificateItem.getItemOptions().setCertificateType(
                certificateType != null ? certificateType : CertificateType.INCORPORATION_WITH_ALL_NAME_CHANGES);
    }
}
