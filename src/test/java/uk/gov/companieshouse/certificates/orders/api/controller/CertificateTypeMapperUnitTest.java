package uk.gov.companieshouse.certificates.orders.api.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptions;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateType;
import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificateTypeMapperUnitTest {
    @Mock
    private FeatureOptions featureOptions;

    @Mock
    private CompanyProfileResource companyProfileResource;

    @InjectMocks
    private CertificateTypeMapper certificateTypeMapper;

    @Test
    void shouldErrorWhenCompanyStatusIsOther() {
        //given
        when(companyProfileResource.getCompanyStatus()).thenReturn(null);

        //when
        CertificateTypeMapResult result = certificateTypeMapper.mapToCertificateType(companyProfileResource);

        //then
        assertThat(result, not(nullValue()));
        assertThat(result.getCertificateType(), is(nullValue()));
        assertThat(result.isMappingError(), is(true));
        assertThat(result.getMappingError(), is(ApiErrors.ERR_COMPANY_STATUS_INVALID));
    }

    @Test
    void shouldMapActiveCompanyToIncorporationCertificate() {
        //given
        when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);

        //when
        CertificateTypeMapResult result = certificateTypeMapper.mapToCertificateType(companyProfileResource);

        //then
        assertThat(result, not(nullValue()));
        assertThat(result.getCertificateType(), is(CertificateType.INCORPORATION));
        assertThat(result.isMappingError(), is(false));
        assertThat(result.getMappingError(), is(nullValue()));
    }

    @Test
    void shouldMapLiquidatedCompanyToIncorporationCertificateWhenLiquidationFeatureEnabled() {
        //given
        when(featureOptions.isLiquidatedCompanyCertificateEnabled()).thenReturn(true);
        when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.LIQUIDATION);

        //when
        CertificateTypeMapResult result = certificateTypeMapper.mapToCertificateType(companyProfileResource);

        //then
        assertThat(result, not(nullValue()));
        assertThat(result.getCertificateType(), is(CertificateType.INCORPORATION));
        assertThat(result.isMappingError(), is(false));
        assertThat(result.getMappingError(), is(nullValue()));
    }

    @Test
    void shouldErrorWhenMappingLiquidatedCompanyToIncorporationCertificateWhenLiquidationFeatureDisabled() {
        //given
        when(featureOptions.isLiquidatedCompanyCertificateEnabled()).thenReturn(false);
        when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.LIQUIDATION);

        //when
        CertificateTypeMapResult result = certificateTypeMapper.mapToCertificateType(companyProfileResource);

        //then
        assertThat(result, not(nullValue()));
        assertThat(result.getCertificateType(), is(nullValue()));
        assertThat(result.isMappingError(), is(true));
        assertThat(result.getMappingError(), is(ApiErrors.ERR_COMPANY_STATUS_INVALID));
    }

    @Test
    void shouldMapDissolvedCompanyToDissolutionCertificate() {
        //given
        when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.DISSOLVED);

        //when
        CertificateTypeMapResult result = certificateTypeMapper.mapToCertificateType(companyProfileResource);

        //then
        assertThat(result, not(nullValue()));
        assertThat(result.getCertificateType(), is(CertificateType.DISSOLUTION));
        assertThat(result.isMappingError(), is(false));
        assertThat(result.getMappingError(), is(nullValue()));
    }
}