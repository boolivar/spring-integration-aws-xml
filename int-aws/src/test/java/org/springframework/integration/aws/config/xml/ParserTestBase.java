package org.springframework.integration.aws.config.xml;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.integration.support.channel.ChannelResolverUtils;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.core.DestinationResolver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@ExtendWith(MockitoExtension.class)
public abstract class ParserTestBase extends BDDMockito {

    @Mock
    protected DestinationResolver<MessageChannel> channelResolver;

    protected final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    protected final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);

    private final String resource;

    private final String template;

    public ParserTestBase() {
        this("/template.xml", "<!-- template -->");
    }

    public ParserTestBase(String resource, String template) {
        this.resource = resource;
        this.template = template;
    }

    @BeforeEach
    void configureBeanFactory() {
        beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver());
        registerBean(ChannelResolverUtils.CHANNEL_RESOLVER_BEAN_NAME, DestinationResolver.class, channelResolver);
    }

    protected <T> void registerBean(String name, Class<? super T> type, T bean) {
        beanFactory.registerBeanDefinition(name, BeanDefinitionBuilder.genericBeanDefinition(type, () -> bean).getBeanDefinition());
    }

    protected <T> T loadBean(Class<T> type, String content) {
        var xml = formatXml(content);
        reader.loadBeanDefinitions(new ByteArrayResource(xml.getBytes()));
        return beanFactory.getBean(type);
    }

    protected String formatXml(String content) {
        try {
            return IOUtils.resourceToString(resource, StandardCharsets.UTF_8)
                .replace(template, content);
        } catch (IOException e) {
            throw new UncheckedIOException("Error load resource: " + resource, e);
        }
    }
}