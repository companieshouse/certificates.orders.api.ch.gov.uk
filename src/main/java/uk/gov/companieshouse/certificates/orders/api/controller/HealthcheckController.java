package uk.gov.companieshouse.certificates.orders.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthcheckController {

    @GetMapping("/healthcheck")
    public ResponseEntity<Void> getHealthcheck() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
