package uk.gov.companieshouse.items.orders.api.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;

/**
 * Service that uses {@link uk.gov.companieshouse.GenerateEtagUtil} to generate unique ETAG values.
 */
@Service
public class EtagGeneratorService {

    public String generateEtag() {
        return GenerateEtagUtil.generateEtag();
    }

}
