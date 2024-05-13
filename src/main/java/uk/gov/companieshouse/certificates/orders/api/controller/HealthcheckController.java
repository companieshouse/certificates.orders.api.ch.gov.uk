package uk.gov.companieshouse.certificates.orders.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthcheckController {

    //Would rather use actuator health check, but will not work with the way the controller endpoints are configured
    //in the application.yml file
    @GetMapping("/healthcheck")
    public ResponseEntity<Void> getHealthcheck() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}