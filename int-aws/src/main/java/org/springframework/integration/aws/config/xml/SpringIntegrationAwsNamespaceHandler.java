package org.springframework.integration.aws.config.xml;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;

public class SpringIntegrationAwsNamespaceHandler extends NamespaceHandlerSupport {

    private final String mappingOverride;

    SpringIntegrationAwsNamespaceHandler() {
        this("META-INF/spring-integration-aws-element-parser.mapping");
    }

    public SpringIntegrationAwsNamespaceHandler(String mappingOverrides) {
        this.mappingOverride = mappingOverrides;
    }

    @Override
    public void init() {
        var mapping = new Properties();
        try (var defaults = getClass().getResourceAsStream("element-parser.mapping")) {
            mapping.load(defaults);
            mapping.putAll(PropertiesLoaderUtils.loadAllProperties(mappingOverride));
        } catch (IOException e) {
            throw new UncheckedIOException("Error load spring-integration-aws element-parser mapping", e);
        }
        mapping.forEach(this::registerParserMapping);
    }

    private void registerParserMapping(Object elementName, Object className) {
        try {
            var type = Thread.currentThread().getContextClassLoader().loadClass((String) className);
            var parser = (BeanDefinitionParser) BeanUtils.instantiateClass(type);
            registerBeanDefinitionParser((String) elementName, parser);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error load parser for " + elementName + ": " + className, e);
        }
    }
}
