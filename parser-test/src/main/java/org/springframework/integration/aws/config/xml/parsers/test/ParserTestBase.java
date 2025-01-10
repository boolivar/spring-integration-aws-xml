package org.springframework.integration.aws.config.xml.parsers.test;

import org.bool.junit.mockito.inline.MockitoInlineExtension;

import org.apache.commons.io.IOUtils;
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
public abstract class ParserTestBase extends BDDMockito {

    protected final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    protected ParserTestBase() {
        beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver());
    }

    protected void registerBean(String name, Object bean) {
        registerBean(name, bean.getClass(), bean);
    }

    protected <T> void registerBean(String name, Class<T> type, Object bean) {
        beanFactory.registerBeanDefinition(name, BeanDefinitionBuilder.genericBeanDefinition(type, () -> type.cast(bean)).getBeanDefinition());
    }

    protected <T> T getBean(Class<T> type) {
        return beanFactory.getBean(type);
    }

    protected int parse(String content) {
        return new XmlBeanDefinitionReader(beanFactory)
            .loadBeanDefinitions(new ByteArrayResource(formatXml(content).getBytes(StandardCharsets.UTF_8)));
    }

    protected String formatXml(String content) {
        try {
            return IOUtils.resourceToString("/template.xml", StandardCharsets.UTF_8)
                .replace("<!-- template -->", content);
        } catch (IOException e) {
            throw new UncheckedIOException("Error read template.xml", e);
        }
    }
}
