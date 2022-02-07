package uk.gov.companieshouse.certificates.orders.api.controller;

import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptions;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateType;
import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;

@Component
class DefaultCertificateTypeMapper implements CertificateTypeMapper {
    private final FeatureOptions featureOptions;

    @Autowired
    DefaultCertificateTypeMapper(FeatureOptions featureOptions) {
        this.featureOptions = featureOptions;
    }

    @Override
    public CertificateTypeMapping mapToCertificateType(CompanyProfileResource companyProfileResource) {
        CompanyStatus companyStatus = companyProfileResource.getCompanyStatus();

        if (companyStatus == CompanyStatus.ACTIVE ||
                (companyStatus == CompanyStatus.LIQUIDATION && featureOptions.isLiquidatedCompanyCertificateEnabled())) {
            return new CertificateTypeMapping(CertificateType.INCORPORATION);
        } else if (companyStatus == CompanyStatus.DISSOLVED) {
            return new CertificateTypeMapping(CertificateType.DISSOLUTION);
        } else {
            return new CertificateTypeMapping(ApiErrors.ERR_COMPANY_STATUS_INVALID);
        }
    }
}
