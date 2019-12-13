package uk.gov.companieshouse.items.orders.api.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Component
public class NonNullPropertyCopier extends BeanUtilsBean {

    @Override
    public void copyProperty(final Object targetBean, final String name, final Object value)
            throws IllegalAccessException, InvocationTargetException {
        if (value != null) {
            super.copyProperty(targetBean, name, value);
        }
    }
}
