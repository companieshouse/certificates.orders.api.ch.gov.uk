package uk.gov.companieshouse.certificates.orders.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptions;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateType;
import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyType;

import static java.util.Objects.isNull;

@Component
class CertificateTypeMapper implements CompanyProfileToCertificateTypeMapper {
    private final FeatureOptions featureOptions;

    @Autowired
    CertificateTypeMapper(FeatureOptions featureOptions) {
        this.featureOptions = featureOptions;
    }

    @Override
    public CertificateTypeMapResult mapToCertificateType(CompanyProfileResource companyProfileResource) {
        CompanyStatus companyStatus = companyProfileResource.companyStatus();

        // Validate company type
        CompanyType companyType = CompanyType.getEnumValue(companyProfileResource.companyType());
        if (isNull(companyType)) {
            return new CertificateTypeMapResult(ApiErrors.ERR_INVALID_COMPANY_TYPE);
        }

        // Check that company status is active for limited partnerships
        if (companyType == CompanyType.LIMITED_PARTNERSHIP && companyStatus != CompanyStatus.ACTIVE) {
            return new CertificateTypeMapResult(ApiErrors.ERR_COMPANY_STATUS_INVALID);
        }

        // Use company status to derive certificate type
        if (companyStatus == CompanyStatus.ACTIVE) {
            return new CertificateTypeMapResult(CertificateType.INCORPORATION_WITH_ALL_NAME_CHANGES);
        } else if (companyStatus == CompanyStatus.LIQUIDATION && featureOptions.liquidatedCompanyCertificateEnabled()) {
            return new CertificateTypeMapResult(CertificateType.INCORPORATION_WITH_ALL_NAME_CHANGES);
        } else if (companyStatus == CompanyStatus.ADMINISTRATION && featureOptions.administratorCompanyCertificateEnabled()) {
            return new CertificateTypeMapResult(CertificateType.INCORPORATION_WITH_ALL_NAME_CHANGES);
        } else if (companyStatus == CompanyStatus.DISSOLVED) {
            return new CertificateTypeMapResult(CertificateType.DISSOLUTION);
        } else {
            return new CertificateTypeMapResult(ApiErrors.ERR_COMPANY_STATUS_INVALID);
        }
    }
}