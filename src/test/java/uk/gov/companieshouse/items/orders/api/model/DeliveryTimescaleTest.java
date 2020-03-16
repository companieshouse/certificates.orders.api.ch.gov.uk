package uk.gov.companieshouse.items.orders.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.items.orders.api.config.CostsConfig;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.SAME_DAY;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.STANDARD;
import static uk.gov.companieshouse.items.orders.api.model.ProductType.*;
import static uk.gov.companieshouse.items.orders.api.util.TestConstants.*;

/**
 * Unit/integration tests the {@link DeliveryTimescale} enum.
 */
class DeliveryTimescaleTest {

    private static final CostsConfig COSTS;

    static {
        COSTS = new CostsConfig();
        COSTS.setStandardCost(STANDARD_INDIVIDUAL_CERTIFICATE_COST);
        COSTS.setSameDayCost(SAME_DAY_INDIVIDUAL_CERTIFICATE_COST);
        COSTS.setStandardDiscount(STANDARD_EXTRA_CERTIFICATE_DISCOUNT);
        COSTS.setSameDayDiscount(SAME_DAY_EXTRA_CERTIFICATE_DISCOUNT);
    }

   @Test
   void serialisesCorrectlyToJson() throws JsonProcessingException {
       final ObjectMapper mapper = new ObjectMapper();
       final String standardJson = mapper.writeValueAsString(STANDARD);
       final String sameDayJson = mapper.writeValueAsString(SAME_DAY);

       assertThat(standardJson, is("\"standard\""));
       assertThat(sameDayJson, is("\"same-day\""));
   }

   @Test
    void standardTimescaleCostsAreCorrect() {
       assertThat(STANDARD.getIndividualCertificateCost(COSTS),         is(STANDARD_INDIVIDUAL_CERTIFICATE_COST));
       assertThat(STANDARD.getExtraCertificateDiscount(COSTS),          is(STANDARD_EXTRA_CERTIFICATE_DISCOUNT));
       assertThat(STANDARD.getFirstCertificateProductType(),            is(CERTIFICATE));
       assertThat(STANDARD.getAdditionalCertificatesProductType(),      is(CERTIFICATE_ADDITIONAL_COPY));
   }

    @Test
    void sameDayTimescaleCostsAreCorrect() {
        assertThat(SAME_DAY.getIndividualCertificateCost(COSTS),         is(SAME_DAY_INDIVIDUAL_CERTIFICATE_COST));
        assertThat(SAME_DAY.getExtraCertificateDiscount(COSTS),          is(SAME_DAY_EXTRA_CERTIFICATE_DISCOUNT));
        assertThat(SAME_DAY.getFirstCertificateProductType(),            is(CERTIFICATE_SAME_DAY));
        assertThat(SAME_DAY.getAdditionalCertificatesProductType(),      is(CERTIFICATE_ADDITIONAL_COPY));
    }

}
