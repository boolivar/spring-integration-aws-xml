package org.springframework.integration.aws.config.xml;

import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.integration.config.ExpressionFactoryBean;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface ValueFactory<T> extends Function<String, T> {

    static ValueFactory<TypedStringValue> typedString() {
        return TypedStringValue::new;
    }

    static ValueFactory<TypedStringValue> typedString(Class<?> type) {
        return value -> new TypedStringValue(value, type);
    }

    static ValueFactory<AbstractBeanDefinition> expressionBean() {
        return bean(ExpressionFactoryBean.class);
    }

    static ValueFactory<AbstractBeanDefinition> bean(Class<?> type) {
        return value -> BeanDefinitionBuilder.genericBeanDefinition(type)
            .addConstructorArgValue(value)
            .applyCustomizers(def -> def.setAutowireCandidate(false))
            .getBeanDefinition();
    }

    default BiConsumer<BeanDefinitionBuilder, String> propertyValue(String name) {
        return (builder, value) -> builder.addPropertyValue(name, apply(value));
    }

    default BiConsumer<BeanDefinitionBuilder, String> constructorValue(String name) {
        return (builder, value) -> builder.addConstructorArgValue(apply(value));
    }
}
