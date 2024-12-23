package org.springframework.integration.aws.support.config.xml.parsers;

import org.bool.junit.mockito.inline.MockitoInlineExtension;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@ExtendWith(MockitoInlineExtension.class)
@ExtendWith(MockitoExtension.class)
class ParserTestBase extends BDDMockito {

    final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    @BeforeEach
    void configureBeanFactory() {
        beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver());
    }

    void registerBean(String name, Object bean) {
        registerBean(name, bean.getClass(), bean);
    }

    <T> void registerBean(String name, Class<T> type, Object bean) {
        beanFactory.registerBeanDefinition(name, BeanDefinitionBuilder.genericBeanDefinition(type, () -> type.cast(bean)).getBeanDefinition());
    }

    int parse(String content) {
        return new XmlBeanDefinitionReader(beanFactory)
            .loadBeanDefinitions(new ByteArrayResource(formatXml(content).getBytes(StandardCharsets.UTF_8)));
    }

    private String formatXml(String content) {
        try {
            return IOUtils.resourceToString("/template.xml", StandardCharsets.UTF_8)
                .replace("<!-- template -->", content);
        } catch (IOException e) {
            throw new UncheckedIOException("Error read template.xml", e);
        }
    }
}
