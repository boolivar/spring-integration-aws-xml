package org.springframework.integration.aws.config.xml;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.outbound.KinesisMessageHandler;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.w3c.dom.Element;

public class KinesisOutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, KinesisMessageHandler.class)
            .addConstructorArgReference("kinesis-client")
            .setPropertyIfAttributeDefined("async")
            .setPropertyIfAttributeDefined("output-channel", "outputChannelName")
            .setPropertyIfAttributeDefined("send-timeout")
            .setPropertyOrExpressionStringIfAttributeDefined("stream")
            .setPropertyOrExpressionStringIfAttributeDefined("partition-key")
            .setPropertyOrExpressionStringIfAttributeDefined("explicit-hash-key")
            .setPropertyIfAttributeDefined("sequence-number-expression", "sequenceNumberExpressionString")
            .setPropertyReferenceIfAttributeDefined("embedded-headers-mapper")
            .setPropertyReferenceIfAttributeDefined("message-converter")
            .build();
    }
}
