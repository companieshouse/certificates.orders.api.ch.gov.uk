package uk.gov.companieshouse.certificates.orders.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonMergePatch;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class TestMergePatchFactory {

    private final ObjectMapper mapper;

    /**
     * Constructor.
     * @param mapper the object mapper this relies upon to create merge patches.
     */
    public TestMergePatchFactory(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Performs an equivalent conversion to that carried out by {@link JsonMergePatchHttpMessageConverter} et al to
     * facilitate integration testing.
     * @param pojo the POJO that represents a merge patch
     * @return the {@link JsonMergePatch} representation
     * @throws IOException should something unexpected happen
     */
    public JsonMergePatch patchFromPojo(final Object pojo) throws IOException {
        final String json = mapper.writeValueAsString(pojo);
        final InputStream stream = new ByteArrayInputStream(json.getBytes());
        final JsonReader reader = Json.createReader(stream);
        final JsonMergePatch patch = Json.createMergePatch(reader.readValue());
        stream.close();
        return patch;
    }

    /**
     * Performs an equivalent conversion to that carried out by {@link JsonMergePatchHttpMessageConverter} et al to
     * facilitate integration testing.
     * @param json the JSON that represents a merge patch
     * @return the {@link JsonMergePatch} representation
     * @throws IOException should something unexpected happen
     */
    public JsonMergePatch patchFromJson(final String json) throws IOException {
        final InputStream stream = new ByteArrayInputStream(json.getBytes());
        final JsonReader reader = Json.createReader(stream);
        final JsonMergePatch patch = Json.createMergePatch(reader.readValue());
        stream.close();
        return patch;
    }

}
