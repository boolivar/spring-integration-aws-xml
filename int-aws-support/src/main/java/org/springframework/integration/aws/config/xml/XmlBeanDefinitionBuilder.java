package org.springframework.integration.aws.config.xml;

import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.springframework.core.Conventions.attributeNameToPropertyName;
import static org.springframework.integration.config.xml.IntegrationNamespaceUtils.setReferenceIfAttributeDefined;
import static org.springframework.integration.config.xml.IntegrationNamespaceUtils.setValueIfAttributeDefined;

public class XmlBeanDefinitionBuilder {

    private final Element element;

    private final ParserContext parserContext;

    private final BeanDefinitionBuilder builder;

    public static XmlBeanDefinitionBuilder newInstance(Element element, ParserContext parserContext, Class<?> beanClass) {
        return new XmlBeanDefinitionBuilder(element, parserContext, BeanDefinitionBuilder.genericBeanDefinition(beanClass));
    }

    public XmlBeanDefinitionBuilder(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        this.element = element;
        this.parserContext = parserContext;
        this.builder = builder;
    }

    public BeanDefinitionBuilder getBeanDefinitionBuilder() {
        return builder;
    }

    public XmlBeanDefinitionBuilder configure(Consumer<BeanDefinitionBuilder> config) {
        config.accept(builder);
        return this;
    }

    public XmlBeanDefinitionBuilder addConstructorArgReference(String attributeName) {
        builder.addConstructorArgReference(element.getAttribute(attributeName));
        return this;
    }

    public XmlBeanDefinitionBuilder addConstructorArgValue(String attributeName) {
        builder.addConstructorArgValue(new TypedStringValue(element.getAttribute(attributeName)));
        return this;
    }

    public XmlBeanDefinitionBuilder addExclusiveConstructorArgReference(String attribute1, String attribute2) {
        return setExclusiveAttribute(attribute1, attribute2, BeanDefinitionBuilder::addConstructorArgReference, BeanDefinitionBuilder::addConstructorArgReference);
    }

    public XmlBeanDefinitionBuilder addExclusiveConstructorArgValue(String attribute1, String attribute2, Function<String, ?> arg1, Function<String, ?> arg2) {
        return setExclusiveAttribute(attribute1, attribute2, (b, v) -> b.addConstructorArgValue(arg1.apply(v)), (b, v) -> b.addConstructorArgValue(arg2.apply(v)));
    }

    public XmlBeanDefinitionBuilder setPropertyReference(String attributeName) {
        builder.addPropertyReference(attributeNameToPropertyName(attributeName), element.getAttribute(attributeName));
        return this;
    }

    public XmlBeanDefinitionBuilder setPropertyReference(String propertyName, String attributeName) {
        builder.addPropertyReference(propertyName, element.getAttribute(attributeName));
        return this;
    }

    public XmlBeanDefinitionBuilder setPropertyReferenceIfAttributeDefined(String attributeName) {
        setReferenceIfAttributeDefined(builder, element, attributeName);
        return this;
    }

    public XmlBeanDefinitionBuilder setPropertyReferenceIfAttributeDefined(String propertyName, String attributeName) {
        setReferenceIfAttributeDefined(builder, element, attributeName, propertyName);
        return this;
    }

    public XmlBeanDefinitionBuilder setPropertyValue(String attributeName) {
        builder.addPropertyValue(attributeNameToPropertyName(attributeName), new TypedStringValue(element.getAttribute(attributeName)));
        return this;
    }

    public XmlBeanDefinitionBuilder setPropertyValue(String propertyName, String attributeName) {
        builder.addPropertyValue(propertyName, new TypedStringValue(element.getAttribute(attributeName)));
        return this;
    }

    public XmlBeanDefinitionBuilder setPropertyValueIfAttributeDefined(String attributeName) {
        setValueIfAttributeDefined(builder, element, attributeName);
        return this;
    }

    public XmlBeanDefinitionBuilder setPropertyValueIfAttributeDefined(String propertyName, String attributeName) {
        setValueIfAttributeDefined(builder, element, attributeName, propertyName);
        return this;
    }

    public XmlBeanDefinitionBuilder setPropertyValueIfAttributeDefined(String propertyName, String attributeName, Function<String, ?> attributeValueMapper) {
        var value = element.getAttribute(attributeName);
        if (StringUtils.hasText(value)) {
            builder.addPropertyValue(propertyName, attributeValueMapper.apply(value));
        }
        return this;
    }

    public XmlBeanDefinitionBuilder setExpressionPropertyIfAttributeDefined(String attribute) {
        return setExpressionPropertyIfAttributeDefined(attribute, attribute + "-expression");
    }

    public XmlBeanDefinitionBuilder setExpressionPropertyIfAttributeDefined(String attribute, String expressionAttribute) {
        return setExpressionPropertyIfAttributeDefined(attribute, expressionAttribute, attributeNameToPropertyName(attribute), attributeNameToPropertyName(expressionAttribute));
    }

    public XmlBeanDefinitionBuilder setExpressionPropertyIfAttributeDefined(String attribute, String expressionAttribute, String property, String expressionProperty) {
        return setIfExclusiveAttributeDefined(attribute, expressionAttribute, (b, v) -> b.addPropertyValue(property, v), (b, v) -> b.addPropertyValue(expressionProperty, new ExpressionBeanDefinitionFactory().createBeanDefinition(v)));
    }

    public XmlBeanDefinitionBuilder setExpressionValueIfAttributeDefined(String attribute) {
        return setPropertyValueIfExclusiveAttributeDefined(attribute, attribute + "-expression", attributeNameToPropertyName(attribute), attributeNameToPropertyName(attribute + "-expression-string"));
    }

    public XmlBeanDefinitionBuilder setPropertyValueIfExclusiveAttributeDefined(String attribute1, String attribute2) {
        return setPropertyValueIfExclusiveAttributeDefined(attribute1, attribute2, attributeNameToPropertyName(attribute1), attributeNameToPropertyName(attribute2));
    }

    public XmlBeanDefinitionBuilder setPropertyValueIfExclusiveAttributeDefined(String attribute1, String attribute2, String property1, String property2) {
        return setIfExclusiveAttributeDefined(attribute1, attribute2, (b, v) -> b.addPropertyValue(property1, v), (b, v) -> b.addPropertyValue(property2, v));
    }

    public XmlBeanDefinitionBuilder setExclusiveAttribute(String attribute1, String attribute2, BiConsumer<BeanDefinitionBuilder, String> arg1, BiConsumer<BeanDefinitionBuilder, String> arg2) {
        var value1 = element.getAttribute(attribute1);
        var value2 = element.getAttribute(attribute2);
        if (StringUtils.hasText(value1) == StringUtils.hasText(value2)) {
            error(attribute1 + " or " + attribute2 + " required and mutually exclusive");
        } else if (StringUtils.hasText(value1)) {
            arg1.accept(builder, value1);
        } else {
            arg2.accept(builder, value2);
        }
        return this;
    }

    public XmlBeanDefinitionBuilder setIfExclusiveAttributeDefined(String attribute1, String attribute2, BiConsumer<BeanDefinitionBuilder, String> arg1, BiConsumer<BeanDefinitionBuilder, String> arg2) {
        var value1 = element.getAttribute(attribute1);
        var value2 = element.getAttribute(attribute2);
        if (StringUtils.hasText(value1) && StringUtils.hasText(value2)) {
            error(attribute1 + " and " + attribute2 + " attributes are mutually exclusive");
        } if (StringUtils.hasText(value1)) {
            arg1.accept(builder, value1);
        } else {
            arg2.accept(builder, value2);
        }
        return this;
    }

    public XmlBeanDefinitionBuilder unsupportedAttributeWarning(String... attributes) {
        for (var attribute : attributes) {
            if (element.hasAttribute(attribute)) {
                warning("'" + attribute + "' attribute not supported");
            }
        }
        return this;
    }

    public XmlBeanDefinitionBuilder warning(String message, Throwable cause) {
        parserContext.getReaderContext().warning(message, element, cause);
        return this;
    }

    public XmlBeanDefinitionBuilder warning(String message) {
        parserContext.getReaderContext().warning(message, element);
        return this;
    }

    public XmlBeanDefinitionBuilder error(String message, Throwable cause) {
        parserContext.getReaderContext().error(message, element, cause);
        return this;
    }

    public XmlBeanDefinitionBuilder error(String message) {
        parserContext.getReaderContext().error(message, element);
        return this;
    }

    public AbstractBeanDefinition build() {
        return builder.getBeanDefinition();
    }
}
