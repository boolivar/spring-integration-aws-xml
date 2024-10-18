package org.springframework.integration.aws.config.xml;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.integration.config.ExpressionFactoryBean;

public class ExpressionBeanDefinitionFactory {

    public AbstractBeanDefinition createBeanDefinition(String value) {
        return BeanDefinitionBuilder.genericBeanDefinition(ExpressionFactoryBean.class)
            .addConstructorArgValue(value)
            .applyCustomizers(def -> def.setAutowireCandidate(false))
            .getBeanDefinition();
    }
}
