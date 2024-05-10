package uk.gov.companieshouse.certificates.orders.api.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


class IdGeneratorServiceTest {

    @Test
    @DisplayName("autoGenerateId returns in the format CRT-######-######")
    void autoGenerateIdGenerateIdInCorrectFormat() {

        final IdGeneratorService idGeneratorService = new IdGeneratorService();

        final String id = idGeneratorService.autoGenerateId();

        assertTrue(id.matches("^CRT-\\d{6}-\\d{6}$"));

    }

}
