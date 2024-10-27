package org.springframework.integration.aws.config.xml;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.inbound.kinesis.KinesisMessageDrivenChannelAdapter;
import org.springframework.integration.config.xml.AbstractChannelAdapterParser;
import org.w3c.dom.Element;

public class KinesisMessageDrivenChannelAdapterParser extends AbstractChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition doParse(Element element, ParserContext parserContext, String channelName) {
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, KinesisMessageDrivenChannelAdapter.class)
            .addConstructorArgReference("kinesis-client")
            .addConstructorArgValue("streams")
            .setPropertyIfAttributeDefined("error-channel", "errorChannelName")
            .setPropertyIfAttributeDefined("consumer-group")
            .setPropertyIfAttributeDefined("bind-source-record")
            .setPropertyIfAttributeDefined("checkpoint-mode")
            .setPropertyIfAttributeDefined("checkpoints-interval")
            .setPropertyIfAttributeDefined("lock-renewal-timeout")
            .setPropertyIfAttributeDefined("concurrency")
            .setPropertyIfAttributeDefined("consumer-backoff")
            .setPropertyIfAttributeDefined("idle-between-polls")
            .setPropertyIfAttributeDefined("listener-mode")
            .setPropertyIfAttributeDefined("records-limit")
            .setPropertyIfAttributeDefined("start-timeout")
            .setPropertyReferenceIfAttributeDefined("consumer-executor")
            .setPropertyReferenceIfAttributeDefined("dispatcher-executor")
            .setPropertyReferenceIfAttributeDefined("checkpoint-store")
            .setPropertyReferenceIfAttributeDefined("lock-registry")
            .setPropertyReferenceIfAttributeDefined("converter")
            .setPropertyReferenceIfAttributeDefined("embedded-headers-mapper")
            .setPropertyReferenceIfAttributeDefined("shard-list-filter")
            .setPropertyReferenceIfAttributeDefined("stream-initial-sequence")
            .getBeanDefinitionBuilder()
            .addPropertyValue("outputChannelName", channelName)
            .getBeanDefinition();
    }
}
