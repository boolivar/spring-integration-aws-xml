package org.springframework.integration.aws.config.xml.parsers;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.config.xml.XmlBeanDefinitionBuilder;
import org.springframework.integration.aws.outbound.SqsMessageHandler;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.w3c.dom.Element;

public class SqsOutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, SqsMessageHandler.class)
            .addConstructorArgReference("sqs")
            .setPropertyOrExpressionStringIfAttributeDefined("queue")
            .setPropertyOrExpressionStringIfAttributeDefined("delay")
            .setPropertyOrExpressionStringIfAttributeDefined("message-group-id")
            .setPropertyOrExpressionStringIfAttributeDefined("message-deduplication-id")
            .setPropertyOrExpressionStringIfAttributeDefined("send-timeout")
            .setPropertyIfAttributeDefined("async")
            .setPropertyIfAttributeDefined("queue-not-found-strategy")
            .setPropertyIfAttributeDefined("output-channel", "outputChannelName")
            .setPropertyReferenceIfAttributeDefined("message-converter")
            .build();
    }
}
