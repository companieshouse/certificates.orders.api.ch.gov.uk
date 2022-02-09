package uk.gov.companieshouse.certificates.orders.api.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptions;
import uk.gov.companieshouse.certificates.orders.api.model.CertificateType;
import uk.gov.companieshouse.certificates.orders.api.model.CompanyProfileResource;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;

import java.util.stream.Stream;

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

    @ParameterizedTest
    @MethodSource("validCompanyTypes")
    void shouldErrorWhenCompanyStatusIsInvalid(String companyType) {
        //given
        when(companyProfileResource.getCompanyType()).thenReturn(companyType);
        when(companyProfileResource.getCompanyStatus()).thenReturn(null);

        //when
        CertificateTypeMapResult result = certificateTypeMapper.mapToCertificateType(companyProfileResource);

        //then
        assertThat(result, not(nullValue()));
        assertThat(result.getCertificateType(), is(nullValue()));
        assertThat(result.isMappingError(), is(true));
        assertThat(result.getMappingError(), is(ApiErrors.ERR_COMPANY_STATUS_INVALID));
    }

    @ParameterizedTest
    @MethodSource("validCompanyTypes")
    void shouldMapActiveCompanyToIncorporationCertificate(String companyType) {
        //given
        when(companyProfileResource.getCompanyType()).thenReturn(companyType);
        when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);

        //when
        CertificateTypeMapResult result = certificateTypeMapper.mapToCertificateType(companyProfileResource);

        //then
        assertThat(result, not(nullValue()));
        assertThat(result.getCertificateType(), is(CertificateType.INCORPORATION_WITH_ALL_NAME_CHANGES));
        assertThat(result.isMappingError(), is(false));
        assertThat(result.getMappingError(), is(nullValue()));
    }

    @ParameterizedTest
    @MethodSource("validCompanyTypes")
    void shouldMapLiquidatedCompanyToIncorporationCertificateWhenLiquidationFeatureEnabled(String companyType) {
        //given
        when(companyProfileResource.getCompanyType()).thenReturn(companyType);
        when(featureOptions.isLiquidatedCompanyCertificateEnabled()).thenReturn(true);
        when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.LIQUIDATION);

        //when
        CertificateTypeMapResult result = certificateTypeMapper.mapToCertificateType(companyProfileResource);

        //then
        assertThat(result, not(nullValue()));
        assertThat(result.getCertificateType(), is(CertificateType.INCORPORATION_WITH_ALL_NAME_CHANGES));
        assertThat(result.isMappingError(), is(false));
        assertThat(result.getMappingError(), is(nullValue()));
    }

    @ParameterizedTest
    @MethodSource("validCompanyTypes")
    void shouldErrorWhenMappingLiquidatedCompanyToIncorporationCertificateWhenLiquidationFeatureDisabled(String companyType) {
        //given
        when(companyProfileResource.getCompanyType()).thenReturn(companyType);
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

    @ParameterizedTest
    @MethodSource("validCompanyTypes")
    void shouldMapDissolvedCompanyToDissolutionCertificate(String companyType) {
        //given
        when(companyProfileResource.getCompanyType()).thenReturn(companyType);
        when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.DISSOLVED);

        //when
        CertificateTypeMapResult result = certificateTypeMapper.mapToCertificateType(companyProfileResource);

        //then
        assertThat(result, not(nullValue()));
        assertThat(result.getCertificateType(), is(CertificateType.DISSOLUTION));
        assertThat(result.isMappingError(), is(false));
        assertThat(result.getMappingError(), is(nullValue()));
    }

    @Test
    void shouldErrorWhenMappingInvalidCompanyTypeToCertificate() {
        //given
        when(companyProfileResource.getCompanyType()).thenReturn("invalid-type");
        when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);

        //when
        CertificateTypeMapResult result = certificateTypeMapper.mapToCertificateType(companyProfileResource);

        //then
        assertThat(result, not(nullValue()));
        assertThat(result.getCertificateType(), is(nullValue()));
        assertThat(result.isMappingError(), is(true));
        assertThat(result.getMappingError(), is(ApiErrors.ERR_INVALID_COMPANY_TYPE));
    }

    @Test
    void shouldErrorWhenMappingLimitedPartnershipToCertificateTypeWhenCompanyStatusIsNotActive() {
        //given
        when(companyProfileResource.getCompanyType()).thenReturn("limited-partnership");
        when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.DISSOLVED);

        //when
        CertificateTypeMapResult result = certificateTypeMapper.mapToCertificateType(companyProfileResource);

        //then
        assertThat(result, not(nullValue()));
        assertThat(result.getCertificateType(), is(nullValue()));
        assertThat(result.isMappingError(), is(true));
        assertThat(result.getMappingError(), is(ApiErrors.ERR_COMPANY_STATUS_INVALID));
    }

    @Test
    void shouldMapLimitedPartnershipToCertificateTypeWhenCompanyStatusActive() {
        //given
        when(companyProfileResource.getCompanyType()).thenReturn("limited-partnership");
        when(companyProfileResource.getCompanyStatus()).thenReturn(CompanyStatus.ACTIVE);

        //when
        CertificateTypeMapResult result = certificateTypeMapper.mapToCertificateType(companyProfileResource);

        //then
        assertThat(result, not(nullValue()));
        assertThat(result.getCertificateType(), is(CertificateType.INCORPORATION_WITH_ALL_NAME_CHANGES));
        assertThat(result.isMappingError(), is(false));
        assertThat(result.getMappingError(), is(nullValue()));
    }

    private static Stream<String> validCompanyTypes() {
        return Stream.of("llp",
                "ltd",
                "plc",
                "old-public-company",
                "private-limited-guarant-nsc",
                "private-limited-guarant-nsc-limited-exemption",
                "private-limited-shares-section-30-exemption",
                "private-unlimited",
                "private-unlimited-nsc");
    }
}