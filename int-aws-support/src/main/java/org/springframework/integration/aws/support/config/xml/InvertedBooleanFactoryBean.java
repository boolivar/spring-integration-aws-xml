package org.springframework.integration.aws.support.config.xml;

import org.springframework.beans.factory.FactoryBean;

public class InvertedBooleanFactoryBean implements FactoryBean<Boolean> {
    
    private final boolean value;

    public InvertedBooleanFactoryBean(boolean value) {
        this.value = value;
    }

    @Override
    public Class<?> getObjectType() {
        return Boolean.class;
    }

    @Override
    public Boolean getObject() {
        return !value;
    }
}
