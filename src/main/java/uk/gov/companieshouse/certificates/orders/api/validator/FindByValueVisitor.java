package uk.gov.companieshouse.certificates.orders.api.validator;

import uk.gov.companieshouse.certificates.orders.api.model.Visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * Produces a list of map keys corresponding to found map target values.
 *
 * Note: keys are returned in alphanumeric order.
 */
public class FindByValueVisitor implements Visitor<Map<String, Object>> {
    private final List<Map.Entry<String, Object>> keys = new ArrayList<>();
    private final Object targetValue;

    public FindByValueVisitor(Object targetValue) {
        this.targetValue = targetValue;
    }

    @Override
    public void visit(Map<String, Object> map) {

        keys.addAll(map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .filter(field -> nonNull(field.getValue()) && field.getValue() == targetValue)
                .collect(Collectors.toList()));
    }

    public List<String> getKeys() {
        return Collections.unmodifiableList(keys.stream().map(Map.Entry::getKey).collect(Collectors.toList()));
    }
}
