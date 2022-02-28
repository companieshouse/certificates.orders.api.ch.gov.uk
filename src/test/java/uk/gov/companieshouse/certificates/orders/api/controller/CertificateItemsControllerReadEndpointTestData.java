package uk.gov.companieshouse.certificates.orders.api.controller;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyStatus;
import uk.gov.companieshouse.certificates.orders.api.validator.CompanyType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

class CertificateItemsControllerReadEndpointTestData {
    static Stream<Arguments> readEndpointTestData() {
        try {
            return Stream.of(
                    Arguments.of(JsonRequestFixture.builder()
                            .withSavedResource(IOUtils.resourceToString("/integrationTestData/read/positive/item.json", StandardCharsets.UTF_8))
                            .withExpectedResponseCode(HttpStatus.OK.value())
                            .withExpectedResponseBody(IOUtils.resourceToString("/integrationTestData/read/positive/response.json", StandardCharsets.UTF_8))
                            .withCompanyStatus(CompanyStatus.ACTIVE)
                            .withCompanyType(CompanyType.LIMITED_COMPANY)
                            .withDescription("Successfully reads a certificate resource")
                            .build())
            );
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
