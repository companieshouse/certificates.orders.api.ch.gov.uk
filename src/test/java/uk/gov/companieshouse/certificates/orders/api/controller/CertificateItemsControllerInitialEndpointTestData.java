package uk.gov.companieshouse.certificates.orders.api.controller;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.certificates.orders.api.service.CompanyNotFoundException;
import uk.gov.companieshouse.certificates.orders.api.service.CompanyServiceException;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

class CertificateItemsControllerInitialEndpointTestData {
    static Stream<Arguments> initialEndpointTestData() {
        try {
            return Stream.of(
                    Arguments.of(JsonRequestFixture.builder()
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/initial/positive/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.CREATED.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/initial/positive/response.json", StandardCharsets.UTF_8))
                            .withCompanyStatus(CompanyStatus.ACTIVE)
                            .withCompanyType(CompanyType.LIMITED_COMPANY)
                            .withDescription("Successfully create initial certificate resource for an active limited company")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/initial/negative/absent_company_number/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.BAD_REQUEST.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/initial/negative/absent_company_number/response.json", StandardCharsets.UTF_8))
                            .withCompanyStatus(CompanyStatus.ACTIVE)
                            .withCompanyType(CompanyType.LIMITED_COMPANY)
                            .withDescription("Raises client error if company number absent")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/initial/negative/company_type_null/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.BAD_REQUEST.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/initial/negative/company_type_null/response.json", StandardCharsets.UTF_8))
                            .withCompanyType(null)
                            .withCompanyStatus(CompanyStatus.ACTIVE)
                            .withDescription("Raises client error if company type null")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/initial/negative/company_status_invalid/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.BAD_REQUEST.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/initial/negative/company_status_invalid/response.json", StandardCharsets.UTF_8))
                            .withCompanyType(CompanyType.LIMITED_COMPANY)
                            .withCompanyStatus(null)
                            .withDescription("Raises client error if company status null")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/initial/negative/company_not_found/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.BAD_REQUEST.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/initial/negative/company_not_found/response.json", StandardCharsets.UTF_8))
                            .withCompanyServiceException(new CompanyNotFoundException("Company not found"))
                            .withCompanyType(null)
                            .withCompanyStatus(null)
                            .withDescription("Raises client error if company not found")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/initial/negative/company_profile_error/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/initial/negative/company_profile_error/response.json", StandardCharsets.UTF_8))
                            .withCompanyServiceException(new CompanyServiceException("Error calling company profile API"))
                            .withCompanyType(null)
                            .withCompanyStatus(null)
                            .withDescription("Raises server error if other error returned by company profile API")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/initial/negative/unknown_properties_set/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.BAD_REQUEST.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/initial/negative/unknown_properties_set/response.json", StandardCharsets.UTF_8))
                            .withCompanyType(CompanyType.LIMITED_COMPANY)
                            .withCompanyStatus(CompanyStatus.ACTIVE)
                            .withDescription("Raises client error if unknown fields present in request")
                            .build())

            );
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
