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

class CertificateItemsControllerCreateEndpointTestData {
    static Stream<Arguments> createEndpointTestData() {
        try {
            return Stream.of(
                    Arguments.of(JsonRequestFixture.builder()
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/create/positive/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.CREATED.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/create/positive/response.json", StandardCharsets.UTF_8))
                            .withCompanyStatus(CompanyStatus.ACTIVE)
                            .withCompanyType(CompanyType.LIMITED_LIABILITY_PARTNERSHIP)
                            .withDescription("Successfully create certificate resource for an active limited company")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/create/negative/deserialisation_error/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.BAD_REQUEST.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/create/negative/deserialisation_error/response.json", StandardCharsets.UTF_8))
                            .withCompanyStatus(CompanyStatus.ACTIVE)
                            .withCompanyType(CompanyType.LIMITED_COMPANY)
                            .withDescription("Raises client error if deserialisation error occurs")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/create/negative/company_type_null/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.BAD_REQUEST.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/create/negative/company_type_null/response.json", StandardCharsets.UTF_8))
                            .withCompanyType(null)
                            .withCompanyStatus(null)
                            .withDescription("Raises client error if company type null")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/create/negative/company_not_found/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.BAD_REQUEST.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/create/negative/company_not_found/response.json", StandardCharsets.UTF_8))
                            .withCompanyServiceException(new CompanyNotFoundException("Company not found"))
                            .withCompanyType(null)
                            .withCompanyStatus(null)
                            .withDescription("Raises client error if company not found")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/create/negative/company_profile_error/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/create/negative/company_profile_error/response.json", StandardCharsets.UTF_8))
                            .withCompanyServiceException(new CompanyServiceException("Error calling company profile API"))
                            .withCompanyType(null)
                            .withCompanyStatus(null)
                            .withDescription("Raises server error if other error returned by company profile API")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/create/negative/item_options_error/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.BAD_REQUEST.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/create/negative/item_options_error/response.json", StandardCharsets.UTF_8))
                            .withCompanyStatus(CompanyStatus.ACTIVE)
                            .withCompanyType(CompanyType.LIMITED_COMPANY)
                            .withDescription("Raises client error if item options validation error occurs")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/create/negative/item_options_absent/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.BAD_REQUEST.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/create/negative/item_options_absent/response.json", StandardCharsets.UTF_8))
                            .withCompanyStatus(CompanyStatus.ACTIVE)
                            .withCompanyType(CompanyType.LIMITED_COMPANY)
                            .withDescription("Raises client error if item options absent")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/create/negative/quantity_error/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.BAD_REQUEST.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/create/negative/quantity_error/response.json", StandardCharsets.UTF_8))
                            .withCompanyStatus(CompanyStatus.ACTIVE)
                            .withCompanyType(CompanyType.LIMITED_COMPANY)
                            .withDescription("Raises client error if quantity is less than 1")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/create/negative/unknown_properties_set/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.BAD_REQUEST.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/create/negative/unknown_properties_set/response.json", StandardCharsets.UTF_8))
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
