package uk.gov.companieshouse.certificates.orders.api.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Calendar;

@Service
public class IdGeneratorService {

    public String autoGenerateId() {
        SecureRandom random = new SecureRandom();
        byte[] values = new byte[4];
        random.nextBytes(values);
        String rand = String.format("%04d", random.nextInt(9999));
        String time = String.format("%08d", Calendar.getInstance().getTimeInMillis() / 100000L);
        String rawId = rand + time;
        String[] tranId = rawId.split("(?<=\\G.{6})");
        return "CRT-" + String.join("-", tranId);
    }

}
