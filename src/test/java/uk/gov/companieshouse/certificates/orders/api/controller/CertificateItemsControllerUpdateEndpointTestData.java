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
                            .build())
            );
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
