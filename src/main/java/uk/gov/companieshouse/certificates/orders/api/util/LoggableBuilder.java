package uk.gov.companieshouse.certificates.orders.api.util;

import uk.gov.companieshouse.certificates.orders.api.logging.LoggingUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public final class LoggableBuilder {
    private String message;
    private Map<String, Object> logMap = new HashMap<>();
    private HttpServletRequest request;

    private LoggableBuilder() {
    }

    private LoggableBuilder(Loggable loggable) {
        this.message = loggable.getMessage();
        this.logMap = new HashMap<>(loggable.getLogMap());
        this.request = loggable.getRequest();
    }

    public static LoggableBuilder newBuilder() {
        return new LoggableBuilder();
    }

    public static LoggableBuilder newBuilder(Loggable loggable) {
        return new LoggableBuilder(loggable);
    }

    public LoggableBuilder withMessage(String message, Object... args) {
        this.message = String.format(message, args);
        return this;
    }

    public LoggableBuilder withLogMapPut(String key, Object value) {
        this.logMap.put(key, value);
        return this;
    }

    public LoggableBuilder withLogMapIfNotNullPut(String key, Object value) {
        LoggingUtils.logIfNotNull(this.logMap, key, value);
        return this;
    }

    public LoggableBuilder withRequest(HttpServletRequest request) {
        this.request = request;
        return this;
    }

    public Loggable build() {
        return new SimpleLoggable(this);
    }

    private static class SimpleLoggable implements Loggable {
        private final String message;
        private final Map<String, Object> logMap;
        private final HttpServletRequest request;

        public SimpleLoggable(LoggableBuilder builder) {
            this.message = builder.message;
            this.logMap = new HashMap<>(builder.logMap);
            this.request = builder.request;
        }

        @Override
        public Map<String, Object> getLogMap() {
            return logMap;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public HttpServletRequest getRequest() {
            return request;
        }
    }
}
