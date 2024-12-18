package org.springframework.integration.aws.config.xml.parsers;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.config.xml.XmlBeanDefinitionBuilder;
import org.springframework.integration.aws.outbound.SnsMessageHandler;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.w3c.dom.Element;

public class SnsOutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, SnsMessageHandler.class)
            .addConstructorArgReference("sns")
            .setPropertyIfAttributeDefined("async")
            .setPropertyIfAttributeDefined("output-channel", "outputChannelName")
            .setPropertyOrExpressionIfAttributeDefined("topic-arn")
            .setPropertyOrExpressionIfAttributeDefined("subject")
            .setPropertyOrExpressionIfAttributeDefined("message-group-id")
            .setPropertyOrExpressionIfAttributeDefined("message-deduplication-id")
            .setPropertyOrExpressionIfAttributeDefined("body")
            .setPropertyOrExpressionIfAttributeDefined("send-timeout")
            .build();
    }
}
