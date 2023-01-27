package uk.gov.companieshouse.certificates.orders.api.util;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import static uk.gov.companieshouse.certificates.orders.api.logging.LoggingUtils.APPLICATION_NAMESPACE;

@Component
public class Log {
    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    public void info(Loggable loggable) {
        LOGGER.info(loggable.getMessage(), loggable.getLogMap());
    }

    public void infoRequest(Loggable loggable) {
        LOGGER.infoRequest(loggable.getRequest(), loggable.getMessage(), loggable.getLogMap());
    }
}
