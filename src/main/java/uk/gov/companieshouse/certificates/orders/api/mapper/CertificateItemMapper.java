package uk.gov.companieshouse.certificates.orders.api.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.companieshouse.certificates.orders.api.controller.CertificateTypeable;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemInitial;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemCreate;
import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemResponse;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItem;
import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;
import uk.gov.companieshouse.certificates.orders.api.model.DeliveryMethod;
import uk.gov.companieshouse.certificates.orders.api.model.DeliveryTimescale;

@Mapper(componentModel = "spring")
public interface CertificateItemMapper {
    CertificateItem certificateItemCreateToCertificateItem(CertificateItemCreate certificateItemCreate);
    @Mapping(target = "quantity", constant = "1")
    CertificateItem certificateItemInitialToCertificateItem(CertificateItemInitial certificateItemInitial);
    CertificateItemCreate certificateItemToCertificateItemDTO(CertificateItem certificateItem);
    CertificateItemResponse certificateItemToCertificateItemResponse(CertificateItem certificateItem);
    @Mapping(source = "identity", target = "certificateItem.userId")
    @Mapping(source = "companyProfile.companyName", target = "certificateItem.companyName")
    @Mapping(source = "companyProfile.companyStatus.statusName", target = "certificateItem.itemOptions.companyStatus")
    @Mapping(source = "companyProfile.companyType", target = "certificateItem.itemOptions.companyType")
    @Mapping(source = "certificateTypeable.certificateType", target = "certificateItem.itemOptions.certificateType")
    CertificateItem enrichCertificateItem(String identity,
                                          CompanyProfileResource companyProfile,
                                          CertificateTypeable certificateTypeable,
                                          @MappingTarget CertificateItem certificateItem);

    @AfterMapping
    default void setDefaults(CertificateItemCreate certificateItemCreate, @MappingTarget CertificateItem certificateItem){
        int quantity = certificateItemCreate.getQuantity();
        certificateItem.setQuantity(quantity > 0 ? quantity : 1);

        DeliveryMethod deliveryMethod = certificateItemCreate.getItemOptions().getDeliveryMethod();
        certificateItem.getItemOptions().setDeliveryMethod(
                deliveryMethod != null ? deliveryMethod : DeliveryMethod.POSTAL);

        DeliveryTimescale deliveryTimescale = certificateItemCreate.getItemOptions().getDeliveryTimescale();
        certificateItem.getItemOptions().setDeliveryTimescale(
                deliveryTimescale != null ? deliveryTimescale : DeliveryTimescale.STANDARD);
        certificateItem.setCompanyNumber(certificateItemCreate.getCompanyNumber());
    }
}
