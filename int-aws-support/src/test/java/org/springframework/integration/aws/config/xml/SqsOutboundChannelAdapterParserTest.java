package org.springframework.integration.aws.config.xml;

import org.assertj.core.api.Condition;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.integration.aws.outbound.SqsMessageHandler;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SqsOutboundChannelAdapterParserTest {

    private final BeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();

    private final XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(registry);

    @Test
    void testReader() {
        int beansLoaded = xmlBeanDefinitionReader.loadBeanDefinitions("classpath:/sqs-outbound-channel-adapter.xml");
        var beans = Stream.of(registry.getBeanDefinitionNames()).collect(Collectors.toMap(k -> k, registry::getBeanDefinition));

        assertThat(beans)
            .hasSize(beansLoaded)
            .containsKey("adapter");

        var def = beans.values().stream()
            .filter(bd -> bd.getBeanClassName().equals(SqsMessageHandler.class.getName()))
            .findAny().orElseThrow();
        
        assertThat(def)
            .matches(bd -> bd.getConstructorArgumentValues().getArgumentValue(0, null).getValue().equals(new RuntimeBeanReference("sqsClient")))
            .extracting(BeanDefinition::getPropertyValues)
                .matches(properties -> properties.get("async").equals(new TypedStringValue("false")), "async")
                .matches(properties -> properties.get("messageConverter").equals(new RuntimeBeanReference("msgConv")), "message converter")
            ;
    }
}
