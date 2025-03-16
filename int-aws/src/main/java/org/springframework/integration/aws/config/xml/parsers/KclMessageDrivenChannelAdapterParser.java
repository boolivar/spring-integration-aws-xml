package org.springframework.integration.aws.config.xml.parsers;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.config.xml.XmlBeanDefinitionBuilder;
import org.springframework.integration.aws.inbound.kinesis.KclMessageDrivenChannelAdapter;
import org.springframework.integration.config.xml.AbstractChannelAdapterParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;

public class KclMessageDrivenChannelAdapterParser extends AbstractChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition doParse(Element element, ParserContext parserContext, String channelName) {
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, KclMessageDrivenChannelAdapter.class)
            .configure(builder -> builder
                .addConstructorArgValue(arg(element, "kinesis-client", KinesisAsyncClient.class))
                .addConstructorArgValue(arg(element, "cloud-watch-client", CloudWatchAsyncClient.class))
                .addConstructorArgValue(arg(element, "dynamo-db-client", DynamoDbAsyncClient.class)))
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

    private Object arg(Element element, String attr, Class<?> beanClass) {
        var ref = element.getAttribute(attr);
        return StringUtils.hasText(ref)
            ? new RuntimeBeanReference(ref)
            : BeanDefinitionBuilder.genericBeanDefinition(beanClass)
                .setFactoryMethod("create")
                .applyCustomizers(def -> def.setAutowireCandidate(false))
                .getBeanDefinition();
    }
}
