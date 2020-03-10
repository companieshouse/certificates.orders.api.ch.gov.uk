package uk.gov.companieshouse.items.orders.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.SAME_DAY;
import static uk.gov.companieshouse.items.orders.api.model.DeliveryTimescale.STANDARD;
import static uk.gov.companieshouse.items.orders.api.model.ProductType.*;

/**
 * Unit/integration tests the {@link DeliveryTimescale} enum.
 */
class DeliveryTimescaleTest {

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
       assertThat(STANDARD.getIndividualCertificateCost(),          is(15));
       assertThat(STANDARD.getExtraCertificateDiscount(),           is(5));
       assertThat(STANDARD.getFirstCertificateProductType(),        is(CERTIFICATE));
       assertThat(STANDARD.getAdditionalCertificatesProductType(),  is(CERTIFICATE_ADDITIONAL_COPY));
   }

    @Test
    void sameDayTimescaleCostsAreCorrect() {
        assertThat(SAME_DAY.getIndividualCertificateCost(),         is(50));
        assertThat(SAME_DAY.getExtraCertificateDiscount(),          is(40));
        assertThat(SAME_DAY.getFirstCertificateProductType(),       is(CERTIFICATE_SAME_DAY));
        assertThat(SAME_DAY.getAdditionalCertificatesProductType(), is(CERTIFICATE_ADDITIONAL_COPY));
    }

}
