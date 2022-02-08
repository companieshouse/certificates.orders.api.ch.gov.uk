package uk.gov.companieshouse.certificates.orders.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptions;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateType;
import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;

@Component
class CertificateTypeMapper implements CompanyProfileToCertificateTypeMapper {
    private final FeatureOptions featureOptions;

    @Autowired
    CertificateTypeMapper(FeatureOptions featureOptions) {
        this.featureOptions = featureOptions;
    }

    @Override
    public CertificateTypeMapResult mapToCertificateType(CompanyProfileResource companyProfileResource) {
        CompanyStatus companyStatus = companyProfileResource.getCompanyStatus();

        if (companyStatus == CompanyStatus.ACTIVE ||
                (companyStatus == CompanyStatus.LIQUIDATION && featureOptions.isLiquidatedCompanyCertificateEnabled())) {
            return new CertificateTypeMapResult(CertificateType.INCORPORATION);
        } else if (companyStatus == CompanyStatus.DISSOLVED) {
            return new CertificateTypeMapResult(CertificateType.DISSOLUTION);
        } else {
            return new CertificateTypeMapResult(ApiErrors.ERR_COMPANY_STATUS_INVALID);
        }
    }
}