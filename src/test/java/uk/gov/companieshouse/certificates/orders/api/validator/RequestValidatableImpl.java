package uk.gov.companieshouse.certificates.orders.api.validator;

import uk.gov.companieshouse.certificates.orders.api.dto.CertificateItemDTO;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItem;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateItemOptions;

class RequestValidatableImpl implements RequestValidatable {
    private final String certificateId;
    private final CompanyStatus companyStatus;
    private final CertificateItemOptions certificateItemOptions;

    public RequestValidatableImpl(CertificateItem certificateItem) {
        this.certificateId = certificateItem.getId();
        this.companyStatus = CompanyStatus.ACTIVE;
        this.certificateItemOptions = certificateItem.getItemOptions();
    }

    public RequestValidatableImpl(CertificateItemOptions certificateItemOptions) {
        this.certificateId = null;
        this.certificateItemOptions = certificateItemOptions;
        this.companyStatus = CompanyStatus.ACTIVE;
    }

    public RequestValidatableImpl(CertificateItemDTO certificateItemDTO) {
        this(CompanyStatus.ACTIVE, certificateItemDTO);
    }

    public RequestValidatableImpl(CompanyStatus companyStatus,
            CertificateItemDTO certificateItemDTO) {
        this.certificateId = certificateItemDTO.getId();
        this.companyStatus = companyStatus;
        this.certificateItemOptions = certificateItemDTO.getItemOptions();
    }

    @Override
    public CompanyStatus getCompanyStatus() {
        return companyStatus;
    }

    @Override
    public String getCertificateId() {
        return certificateId;
    }

    @Override
    public CertificateItemOptions getItemOptions() {
        return certificateItemOptions;
    }
}
