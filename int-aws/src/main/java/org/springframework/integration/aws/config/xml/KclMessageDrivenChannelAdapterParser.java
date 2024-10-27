package org.springframework.integration.aws.config.xml;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.inbound.kinesis.KclMessageDrivenChannelAdapter;
import org.springframework.integration.config.xml.AbstractChannelAdapterParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map.Entry;

public class KclMessageDrivenChannelAdapterParser extends AbstractChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition doParse(Element element, ParserContext parserContext, String channelName) {
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, KclMessageDrivenChannelAdapter.class)
            .configure(builder -> addConstructorArgs(builder, element))
            .addConstructorArgValue("streams")
            .setPropertyIfAttributeDefined("error-channel", "errorChannelName")
            .setPropertyIfAttributeDefined("worker-id")
            .setPropertyIfAttributeDefined("consumer-group")
            .setPropertyIfAttributeDefined("lease-table-name")
            .setPropertyIfAttributeDefined("consumer-backoff")
            .setPropertyIfAttributeDefined("empty-record-list")
            .setPropertyIfAttributeDefined("fan-out")
            .setPropertyIfAttributeDefined("bind-source-record")
            .setPropertyIfAttributeDefined("checkpoint-mode")
            .setPropertyIfAttributeDefined("checkpoints-interval")
            .setPropertyIfAttributeDefined("listener-mode")
            .setPropertyIfAttributeDefined("metrics-level")
            .setPropertyIfAttributeDefined("polling-idle-time")
            .setPropertyIfAttributeDefined("polling-max-records")
            .setPropertyIfAttributeDefined("graceful-shutdown-timeout")
            .setPropertyReferenceIfAttributeDefined("glue-schema-registry-deserializer")
            .setPropertyReferenceIfAttributeDefined("executor")
            .setPropertyReferenceIfAttributeDefined("converter")
            .setPropertyReferenceIfAttributeDefined("embedded-headers-mapper")
            .setPropertyReferenceIfAttributeDefined("stream-initial-sequence")
            .getBeanDefinitionBuilder()
            .addPropertyValue("outputChannelName", channelName)
            .getBeanDefinition();
    }

    private void addConstructorArgs(BeanDefinitionBuilder builder, Element element) {
        var args = List.of(
            new AbstractMap.SimpleEntry<>("kinesis-client", "software.amazon.awssdk.services.kinesis.KinesisAsyncClient"),
            new AbstractMap.SimpleEntry<>("cloud-watch-client", "software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient"),
            new AbstractMap.SimpleEntry<>("dynamo-db-client", "software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient")
        );
        if (args.stream().map(Entry::getKey).map(element::getAttribute).anyMatch(StringUtils::hasText)) {
            for (AbstractMap.SimpleEntry<String, String> e : args) {
                builder.addConstructorArgValue(arg(element.getAttribute(e.getKey()), e.getValue()));
            }
        }
    }

    private Object arg(String ref, String beanClass) {
        return StringUtils.hasText(ref)
            ? new RuntimeBeanReference(ref)
            : BeanDefinitionBuilder.genericBeanDefinition(beanClass)
                .setFactoryMethod("create")
                .applyCustomizers(def -> def.setAutowireCandidate(false))
                .getBeanDefinition();
    }
}
