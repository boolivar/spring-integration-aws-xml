package org.springframework.integration.aws.support.config.xml;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.NonNull;

/**
 * Factory bean to invert(negate) Boolean value.
 * 
 * <pre><code lang="spock">
 * def "boolean inverted"() {
 *   expect:
 *     new InvertedBooleanFactoryBean(true).getObject() == false
 *     new InvertedBooleanFactoryBean(false).getObject() == true
 * }
 * </code></pre>
 */
public class InvertedBooleanFactoryBean implements FactoryBean<Boolean> {

    private final boolean value;

    public InvertedBooleanFactoryBean(boolean value) {
        this.value = value;
    }

    @Override
    public Class<?> getObjectType() {
        return Boolean.class;
    }

    @NonNull
    @Override
    public Boolean getObject() {
        return !value;
    }
}
