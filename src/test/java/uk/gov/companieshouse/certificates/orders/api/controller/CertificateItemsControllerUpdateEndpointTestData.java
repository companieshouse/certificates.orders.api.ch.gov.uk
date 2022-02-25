package uk.gov.companieshouse.certificates.orders.api.controller;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

class CertificateItemsControllerUpdateEndpointTestData {
    static Stream<Arguments> updateEndpointTestData() {
        try {
            return Stream.of(
                    Arguments.of(JsonRequestFixture.builder()
                            .withSavedResource(IOUtils.resourceToString("/integrationTestData/update/positive/item.json", StandardCharsets.UTF_8))
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/update/positive/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.OK.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/update/positive/response.json", StandardCharsets.UTF_8))
                            .withCompanyStatus(CompanyStatus.ACTIVE)
                            .withCompanyType(CompanyType.LIMITED_LIABILITY_PARTNERSHIP)
                            .withDescription("Successfully updates a certificate resource")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withSavedResource(IOUtils.resourceToString("/integrationTestData/update/negative/deserialisation_error/item.json", StandardCharsets.UTF_8))
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/update/negative/deserialisation_error/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.BAD_REQUEST.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/update/negative/deserialisation_error/response.json", StandardCharsets.UTF_8))
                            .withCompanyStatus(CompanyStatus.ACTIVE)
                            .withCompanyType(CompanyType.LIMITED_LIABILITY_PARTNERSHIP)
                            .withDescription("Raises client error if deserialisation error raised")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withSavedResource(IOUtils.resourceToString("/integrationTestData/update/negative/invalid_field_value/item.json", StandardCharsets.UTF_8))
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/update/negative/invalid_field_value/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.BAD_REQUEST.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/update/negative/invalid_field_value/response.json", StandardCharsets.UTF_8))
                            .withCompanyStatus(CompanyStatus.ACTIVE)
                            .withCompanyType(CompanyType.LIMITED_LIABILITY_PARTNERSHIP)
                            .withDescription("Raises client error if JSR-303 validation error raised")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withSavedResource(IOUtils.resourceToString("/integrationTestData/update/negative/item_options_error/item.json", StandardCharsets.UTF_8))
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/update/negative/item_options_error/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.BAD_REQUEST.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/update/negative/item_options_error/response.json", StandardCharsets.UTF_8))
                            .withCompanyStatus(CompanyStatus.ACTIVE)
                            .withCompanyType(CompanyType.LIMITED_LIABILITY_PARTNERSHIP)
                            .withDescription("Raises client error if certificate options invalid")
                            .build()),
                    Arguments.of(JsonRequestFixture.builder()
                            .withSavedResource(IOUtils.resourceToString("/integrationTestData/update/negative/unknown_properties_set/item.json", StandardCharsets.UTF_8))
                            .withRequestBody(IOUtils.resourceToString("/integrationTestData/update/negative/unknown_properties_set/request.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.BAD_REQUEST.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/update/negative/unknown_properties_set/response.json", StandardCharsets.UTF_8))
                            .withCompanyStatus(CompanyStatus.ACTIVE)
                            .withCompanyType(CompanyType.LIMITED_LIABILITY_PARTNERSHIP)
                            .withDescription("Raises client error if unknown fields present in request")
                            .build())
                    );
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
