package uk.gov.companieshouse.certificates.orders.api.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.certificates.orders.api.config.FeatureOptions;

@Component
public class OptionsValidationHelperFactory {

    private FeatureOptions featureOptions;

    @Autowired
    public OptionsValidationHelperFactory(FeatureOptions featureOptions) {
        this.featureOptions = featureOptions;
    }

    public OptionsValidationHelper createOptionsValidationHelper(RequestValidatable requestValidatable) {
        return new OptionsValidationHelper(requestValidatable, featureOptions);
    }
}
