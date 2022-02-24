package uk.gov.companieshouse.certificates.orders.api.controller;

import org.junit.jupiter.params.provider.Arguments;
import uk.gov.companieshouse.certificates.orders.api.model.AdministratorsDetails;
import uk.gov.companieshouse.certificates.orders.api.model.LiquidatorsDetails;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyType;

import java.util.stream.Stream;

import static java.util.Collections.singletonList;

class CertificateItemsControllerTestData {
    static Stream<Arguments> invalidStatusTypeErrorFixtures() {
        return Stream.of(
                // liquidators details
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_COMPANY)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .withLiquidatorsDetails(new LiquidatorsDetails())
                        .withExpectedErrors(singletonList(ApiErrors.raiseError(ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED, "include_liquidators_details: must not exist when company status is active")))
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_COMPANY)
                        .withCompanyStatus(CompanyStatus.LIQUIDATION)
                        .withIncludeGoodStandingInformation(true)
                        .withExpectedErrors(singletonList(ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_GOOD_STANDING_INFORMATION_SUPPLIED, "include_good_standing_information: must not exist when company status is liquidation")))
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_LIABILITY_PARTNERSHIP)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .withLiquidatorsDetails(new LiquidatorsDetails())
                        .withExpectedErrors(singletonList(ApiErrors.raiseError(ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED, "include_liquidators_details: must not exist when company status is active")))
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_LIABILITY_PARTNERSHIP)
                        .withCompanyStatus(CompanyStatus.LIQUIDATION)
                        .withIncludeGoodStandingInformation(true)
                        .withExpectedErrors(singletonList(ApiErrors.raiseError(ApiErrors.ERR_INCLUDE_GOOD_STANDING_INFORMATION_SUPPLIED, "include_good_standing_information: must not exist when company status is liquidation")))
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_PARTNERSHIP)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .withLiquidatorsDetails(new LiquidatorsDetails())
                        .withExpectedErrors(singletonList(ApiErrors.raiseError(ApiErrors.ERR_LIQUIDATORS_DETAILS_SUPPLIED, "include_liquidators_details: must not exist when company type is limited-partnership")))
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_PARTNERSHIP)
                        .withCompanyStatus(CompanyStatus.LIQUIDATION)
                        .withExpectedErrors(singletonList(ApiErrors.raiseError(ApiErrors.ERR_COMPANY_STATUS_INVALID, "company_status: liquidation not valid for company type limited-partnership")))
                        .build()),
                //administrators details
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_COMPANY)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .withAdministratorsDetails(new AdministratorsDetails())
                        .withExpectedErrors(singletonList(ApiErrors.raiseError(ApiErrors.ERR_ADMINISTRATORS_DETAILS_SUPPLIED, "include_administrators_details: must not exist when company status is active")))
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_COMPANY)
                        .withCompanyStatus(CompanyStatus.LIQUIDATION)
                        .withAdministratorsDetails(new AdministratorsDetails())
                        .withExpectedErrors(singletonList(ApiErrors.raiseError(ApiErrors.ERR_ADMINISTRATORS_DETAILS_SUPPLIED, "include_administrators_details: must not exist when company status is liquidation")))
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_LIABILITY_PARTNERSHIP)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .withAdministratorsDetails(new AdministratorsDetails())
                        .withExpectedErrors(singletonList(ApiErrors.raiseError(ApiErrors.ERR_ADMINISTRATORS_DETAILS_SUPPLIED, "include_administrators_details: must not exist when company status is active")))
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_LIABILITY_PARTNERSHIP)
                        .withCompanyStatus(CompanyStatus.LIQUIDATION)
                        .withAdministratorsDetails(new AdministratorsDetails())
                        .withExpectedErrors(singletonList(ApiErrors.raiseError(ApiErrors.ERR_ADMINISTRATORS_DETAILS_SUPPLIED, "include_administrators_details: must not exist when company status is liquidation")))
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_PARTNERSHIP)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .withAdministratorsDetails(new AdministratorsDetails())
                        .withExpectedErrors(singletonList(ApiErrors.raiseError(ApiErrors.ERR_ADMINISTRATORS_DETAILS_SUPPLIED, "include_administrators_details: must not exist when company type is limited-partnership")))
                        .build())
        );
    }

    static Stream<Arguments> initialCertificateItemFixtures() {
        return Stream.of(
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_COMPANY)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_LIABILITY_PARTNERSHIP)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PUBLIC_LIMITED_COMPANY)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.OLD_PUBLIC_COMPANY)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PRIVATE_LIMITED_GUARANT_NSC)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PRIVATE_LIMITED_GUARANT_NSC_LIMITED_EXEMPTION)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PRIVATE_LIMITED_SHARES_SECTION_30_EXEMPTION)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PRIVATE_UNLIMITED)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PRIVATE_UNLIMITED_NSC)
                        .withCompanyStatus(CompanyStatus.ACTIVE)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_COMPANY)
                        .withCompanyStatus(CompanyStatus.ADMINISTRATION)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_LIABILITY_PARTNERSHIP)
                        .withCompanyStatus(CompanyStatus.ADMINISTRATION)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PUBLIC_LIMITED_COMPANY)
                        .withCompanyStatus(CompanyStatus.ADMINISTRATION)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.OLD_PUBLIC_COMPANY)
                        .withCompanyStatus(CompanyStatus.ADMINISTRATION)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PRIVATE_LIMITED_GUARANT_NSC)
                        .withCompanyStatus(CompanyStatus.ADMINISTRATION)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PRIVATE_LIMITED_GUARANT_NSC_LIMITED_EXEMPTION)
                        .withCompanyStatus(CompanyStatus.ADMINISTRATION)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PRIVATE_LIMITED_SHARES_SECTION_30_EXEMPTION)
                        .withCompanyStatus(CompanyStatus.ADMINISTRATION)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PRIVATE_UNLIMITED)
                        .withCompanyStatus(CompanyStatus.ADMINISTRATION)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PRIVATE_UNLIMITED_NSC)
                        .withCompanyStatus(CompanyStatus.ADMINISTRATION)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_COMPANY)
                        .withCompanyStatus(CompanyStatus.LIQUIDATION)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.LIMITED_LIABILITY_PARTNERSHIP)
                        .withCompanyStatus(CompanyStatus.LIQUIDATION)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PUBLIC_LIMITED_COMPANY)
                        .withCompanyStatus(CompanyStatus.LIQUIDATION)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.OLD_PUBLIC_COMPANY)
                        .withCompanyStatus(CompanyStatus.LIQUIDATION)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PRIVATE_LIMITED_GUARANT_NSC)
                        .withCompanyStatus(CompanyStatus.LIQUIDATION)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PRIVATE_LIMITED_GUARANT_NSC_LIMITED_EXEMPTION)
                        .withCompanyStatus(CompanyStatus.LIQUIDATION)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PRIVATE_LIMITED_SHARES_SECTION_30_EXEMPTION)
                        .withCompanyStatus(CompanyStatus.LIQUIDATION)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PRIVATE_UNLIMITED)
                        .withCompanyStatus(CompanyStatus.LIQUIDATION)
                        .build()),
                Arguments.of(CertificateItemsFixture.newBuilder()
                        .withCompanyType(CompanyType.PRIVATE_UNLIMITED_NSC)
                        .withCompanyStatus(CompanyStatus.LIQUIDATION)
                        .build())
        );
    }
}
